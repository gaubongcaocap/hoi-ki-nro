let shopsData = [];
let currentShopIndex = null;
let currentTabIndex = 0;

// Data caches for players, item templates, option templates
let playersData = [];
let itemTemplates = [];
let optionTemplates = [];
let giftcodesData = [];

/**
 * Tải dữ liệu shop từ API. Nếu thành công, lưu vào biến shopsData
 * và hiển thị danh sách NPC. Nếu lỗi, hiển thị thông báo.
 */
function loadShopsFromApi() {
  fetch("/api/shops")
    .then((response) => response.json())
    .then(async (data) => {
      // If option templates have not been loaded yet, fetch them now so that
      // item options can be rendered properly. Only fetch once.
      if (!optionTemplates || optionTemplates.length === 0) {
        try {
          const opts = await fetch("/api/item_option_templates").then((res) =>
            res.json()
          );
          optionTemplates = opts;
        } catch (e) {
          console.error("Lỗi tải option templates:", e);
        }
      }
      // If item templates have not been loaded yet, fetch them now to have names and descriptions
      if (!itemTemplates || itemTemplates.length === 0) {
        try {
          const items = await fetch("/api/item_templates").then((res) =>
            res.json()
          );
          itemTemplates = items;
        } catch (e) {
          console.error("Lỗi tải item templates:", e);
        }
      }
      // Group shops by NPC ID to avoid duplicate NPC entries. Each tab will retain
      // its original shopId and tabIndex to allow edits and inserts to map back to DB.
      const groupMap = {};
      data.forEach((shop) => {
        if (!groupMap[shop.npcId]) {
          groupMap[shop.npcId] = {
            npcId: shop.npcId,
            npcName: shop.npcName,
            npcIcon: shop.npcIcon,
            tabs: [],
          };
        }
        shop.tabs.forEach((tab, idx) => {
          if (!tab) return;
          groupMap[shop.npcId].tabs.push({
            name: tab.name,
            items: tab.items,
            shopId: tab.shopId !== undefined ? tab.shopId : shop.shopId,
            tabIndex: tab.tabIndex !== undefined ? tab.tabIndex : idx,
          });
        });
      });
      shopsData = Object.values(groupMap);
      renderNpcList();
    })
    .catch((err) => {
      console.error("Lỗi tải dữ liệu shop:", err);
      document.getElementById("npc-list").innerHTML =
        "<p>Lỗi tải dữ liệu shop</p>";
    });
}

// Render the list of NPCs on the left
function renderNpcList() {
  const npcListEl = document.getElementById("npc-list");
  npcListEl.innerHTML = "";
  shopsData.forEach((shop, index) => {
    const npcItem = document.createElement("div");
    npcItem.className = "npc-item";
    npcItem.innerHTML = `<img src="${shop.npcIcon}" alt="${shop.npcName}"><span>${shop.npcName}</span>`;
    npcItem.addEventListener("click", () => {
      currentShopIndex = index;
      currentTabIndex = 0;
      renderShop(shop);
    });
    npcListEl.appendChild(npcItem);
  });
}

// Render the selected shop
function renderShop(shop) {
  // Update title
  const titleEl = document.getElementById("shop-title");
  titleEl.textContent = `${shop.npcName} - Shop`;

  // Render tabs
  const tabsEl = document.getElementById("tabs");
  tabsEl.innerHTML = "";
  shop.tabs.forEach((tab, index) => {
    if (!tab) return;
    const tabEl = document.createElement("div");
    tabEl.className = "tab" + (index === currentTabIndex ? " active" : "");
    tabEl.textContent = tab.name;
    tabEl.addEventListener("click", () => {
      currentTabIndex = index;
      renderItems(shop.tabs[index]);
      highlightTabs(tabsEl, index);
    });
    tabsEl.appendChild(tabEl);
  });

  // Render items of the first tab
  renderItems(shop.tabs[currentTabIndex]);
}

// Highlight the active tab
function highlightTabs(tabsEl, activeIndex) {
  Array.from(tabsEl.children).forEach((child, idx) => {
    if (idx === activeIndex) {
      child.classList.add("active");
    } else {
      child.classList.remove("active");
    }
  });
}

// Render items in the selected tab
function renderItems(tab) {
  const itemsContainer = document.getElementById("items-container");
  itemsContainer.innerHTML = "";
  tab.items.forEach((item, itemIndex) => {
    const card = document.createElement("div");
    card.className = "item-card";
    let optionsHtml = "";
    if (item.options && item.options.length > 0) {
      // Build a map from option ID to its description
      const optMap = {};
      optionTemplates.forEach((o) => {
        optMap[o.id] = o.description;
      });
      optionsHtml =
        '<div class="options">' +
        item.options
          .map((opt) => {
            const tpl = optMap[opt.id];
            if (tpl) {
              if (tpl.includes("#")) {
                return tpl.replace(/#/g, opt.param);
              } else {
                return tpl + " +" + opt.param;
              }
            }
            return `Option ${opt.id}: +${opt.param}`;
          })
          .join("<br>") +
        "</div>";
    }
    card.innerHTML = `
      <img src="${item.icon}" alt="${item.name}" class="mx-auto w-12 h-12" />
      <div class="item-name font-medium">${item.name}</div>
      ${
        item.description
          ? `<div class="item-desc text-xs text-gray-500 mb-1">${item.description}</div>`
          : ""
      }
      <div class="item-cost text-sm">Giá: ${item.cost} ${item.currency}</div>
      ${optionsHtml}
      <button class="edit-item-btn" data-item-index="${itemIndex}">Chỉnh sửa</button>
    `;
    itemsContainer.appendChild(card);
  });
  // Gắn sự kiện cho các nút chỉnh sửa
  document.querySelectorAll(".edit-item-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const itemIndex = parseInt(btn.getAttribute("data-item-index"), 10);
      showEditItemDialog(itemIndex);
    });
  });
  // Sau khi hiển thị các item, hiển thị form thêm item nếu có tab
  renderAddItemForm();
}

/**
 * Hiển thị form thêm item dưới danh sách item của tab hiện tại.
 * Form gồm các trường: name, tempId, cost, currency và options. Khi nhấn
 * nút "Thêm", gửi yêu cầu POST tới API. Sau khi thành công, tải lại
 * dữ liệu shop và cập nhật UI.
 */
function renderAddItemForm() {
  const container = document.getElementById("shop-content");
  // Xóa form cũ nếu có
  let existingForm = document.getElementById("add-item-form");
  if (existingForm) existingForm.remove();
  // Không hiển thị nếu chưa chọn shop hoặc tab
  if (
    currentShopIndex === null ||
    !shopsData[currentShopIndex] ||
    !shopsData[currentShopIndex].tabs[currentTabIndex]
  ) {
    return;
  }
  const form = document.createElement("div");
  form.id = "add-item-form";
  form.style.marginTop = "20px";
  form.style.padding = "10px";
  form.style.borderTop = "1px solid #ddd";
  // Trước khi xây dựng form, nếu danh sách optionTemplates rỗng thì tải từ API
  const loadOptions =
    optionTemplates.length === 0
      ? fetch("/api/item_option_templates")
          .then((res) => res.json())
          .then((data) => {
            optionTemplates = data;
          })
      : Promise.resolve();
  loadOptions.then(() => {
    // Build options selector HTML
    let optionsHtml = "";
    optionTemplates.forEach((opt) => {
      optionsHtml += `
        <div style="margin-bottom:6px;">
          <label><input type="checkbox" class="item-opt-checkbox" value="${opt.id}"> ${opt.description}</label>
          <input type="number" class="item-opt-param" placeholder="param" style="width:60px; margin-left:4px;" disabled>
        </div>
      `;
    });
    form.innerHTML = `
      <h3>Thêm item vào tab</h3>
      <div style="display:flex; flex-wrap: wrap; gap:10px; align-items: flex-end;">
        <div style="flex:1; min-width:160px;">
          <label>Tên:</label><br>
          <input type="text" id="item-name" style="width:100%;">
        </div>
        <div style="flex:1; min-width:160px;">
          <label>Temp ID:</label><br>
          <input type="number" id="item-tempId" style="width:100%;">
      <div id="item-desc-preview" class="text-xs text-gray-500 mt-1"></div>
        </div>
        <div style="flex:1; min-width:160px;">
          <label>Giá:</label><br>
          <input type="number" id="item-cost" style="width:100%;">
        </div>
        <div style="flex:1; min-width:160px;">
          <label>Loại tiền:</label><br>
          <select id="item-currency" style="width:100%;">
            <option value="vàng">Vàng</option>
            <option value="ngọc">Ngọc</option>
            <option value="ruby">Ruby</option>
            <option value="coupon">Coupon</option>
          </select>
        </div>
      </div>
      <div style="margin-top:10px;">
        <label>Chọn options và nhập param:</label>
        <div id="item-options-container" style="margin-top:6px; max-height:120px; overflow-y:auto; border:1px solid #ddd; padding:4px;">${optionsHtml}</div>
      </div>
      <button id="add-item-button" style="margin-top:10px;">Thêm</button>
      <p id="add-item-message" style="color:#c00;"></p>
    `;
    container.appendChild(form);
    // When tempId is updated, show description of the corresponding item template
    const tempIdInputEl = form.querySelector("#item-tempId");
    const descPreviewEl = form.querySelector("#item-desc-preview");
    if (tempIdInputEl && descPreviewEl) {
      function updateDesc() {
        const val = parseInt(tempIdInputEl.value, 10);
        const tpl = itemTemplates.find((it) => it.id === val);
        descPreviewEl.textContent =
          tpl && tpl.description ? tpl.description : "";
      }
      tempIdInputEl.addEventListener("input", updateDesc);
      // initialise once
      updateDesc();
    }
    // Kích hoạt/khóa input param khi checkbox thay đổi
    form.querySelectorAll(".item-opt-checkbox").forEach((cb) => {
      cb.addEventListener("change", () => {
        // tìm ô nhập param trong cùng container <div>
        const containerDiv = cb.closest("div");
        const paramInput = containerDiv.querySelector(".item-opt-param");
        if (!paramInput) return;
        paramInput.disabled = !cb.checked;
        if (!cb.checked) paramInput.value = "";
      });
    });
    // Xử lý nút thêm
    document.getElementById("add-item-button").addEventListener("click", () => {
      const name = document.getElementById("item-name").value.trim();
      const tempId = parseInt(document.getElementById("item-tempId").value, 10);
      const cost = parseInt(document.getElementById("item-cost").value, 10);
      const currency = document.getElementById("item-currency").value;
      const msgEl = document.getElementById("add-item-message");
      if (!name || isNaN(tempId) || isNaN(cost)) {
        msgEl.textContent = "Vui lòng nhập đầy đủ tên, tempId và giá.";
        return;
      }
      // Thu thập options đã chọn
      const options = [];
      const checkboxes = form.querySelectorAll(".item-opt-checkbox");
      checkboxes.forEach((cb) => {
        if (cb.checked) {
          const id = parseInt(cb.value, 10);
          const containerDiv = cb.closest("div");
          const paramInput = containerDiv.querySelector(".item-opt-param");
          const param = parseInt(paramInput ? paramInput.value : "", 10);
          if (!isNaN(id) && !isNaN(param)) {
            options.push({ id, param });
          }
        }
      });
      const newItem = {
        name,
        tempId,
        cost,
        currency,
        icon: "/icon/x4/3013.png",
        options,
      };
      // Use shopId from shopsData instead of array index for API path
      // Use the original shopId and tabIndex stored in the tab object
      const tabObj = shopsData[currentShopIndex].tabs[currentTabIndex];
      const shopId = tabObj.shopId;
      const tabIdx = tabObj.tabIndex;
      fetch(`/api/shops/${shopId}/tabs/${tabIdx}/items`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newItem),
      })
        .then((resp) => resp.json())
        .then((data) => {
          if (data.success) {
            msgEl.style.color = "#090";
            msgEl.textContent = "Thêm item thành công!";
            document.getElementById("item-name").value = "";
            document.getElementById("item-tempId").value = "";
            document.getElementById("item-cost").value = "";
            // Reset options form
            form
              .querySelectorAll(".item-opt-checkbox")
              .forEach((cb) => (cb.checked = false));
            form.querySelectorAll(".item-opt-param").forEach((inp) => {
              inp.disabled = true;
              inp.value = "";
            });
            loadShopsFromApi();
          } else {
            msgEl.style.color = "#c00";
            msgEl.textContent = data.error || "Có lỗi xảy ra.";
          }
        })
        .catch((err) => {
          msgEl.style.color = "#c00";
          msgEl.textContent = "Có lỗi xảy ra khi gửi yêu cầu.";
          console.error(err);
        });
    });
  });
}

/**
 * Hiển thị hộp thoại chỉnh sửa item. Cho phép sửa giá, loại tiền và options.
 * Sau khi cập nhật thành công, nạp lại dữ liệu shop.
 * @param {number} itemIndex
 */
function showEditItemDialog(itemIndex) {
  const shopIdx = currentShopIndex;
  const tabIdx = currentTabIndex;
  const shop = shopsData[shopIdx];
  const tab = shop.tabs[tabIdx];
  const item = tab.items[itemIndex];
  if (!item) return;
  // Create modal overlay
  const overlay = document.createElement("div");
  overlay.className = "modal-overlay";
  // Create modal box
  const modal = document.createElement("div");
  modal.className = "modal-box";
  // Build options selector for editing
  // Ensure optionTemplates is loaded
  const buildOptionRows = () => {
    return optionTemplates
      .map((opt) => {
        // Check if this option exists in current item
        const existing = (item.options || []).find((o) => o.id === opt.id);
        const checked = existing ? "checked" : "";
        const paramVal = existing ? existing.param : "";
        return `
        <div class="edit-option-row" style="margin-bottom:6px;">
          <label><input type="checkbox" class="edit-opt-checkbox" value="${
            opt.id
          }" ${checked}> ${opt.description}</label>
          <input type="number" class="edit-opt-param" placeholder="param" style="width:60px; margin-left:4px;" ${
            checked ? "" : "disabled"
          } value="${paramVal}">
        </div>
      `;
      })
      .join("");
  };
  modal.innerHTML = `
    <h3 class="mb-2 font-bold text-lg">Chỉnh sửa Item</h3>
    <div style="margin-bottom:10px;">
      <label>Tên:</label><br>
      <input type="text" style="width:100%;" value="${item.name}" disabled>
    </div>
    ${
      item.description
        ? `<div style="margin-bottom:10px;">
      <label>Mô tả:</label><br>
      <div class="text-sm text-gray-700" style="white-space:pre-line;">${item.description}</div>
    </div>`
        : ""
    }
    <div style="margin-bottom:10px;">
      <label>Giá (vàng):</label><br>
      <input type="number" id="edit-cost" style="width:100%;" value="${
        item.cost
      }">
    </div>
    <div style="margin-bottom:10px;">
      <label>Loại tiền:</label><br>
      <select id="edit-currency" style="width:100%;">
        <option value="vàng" ${
          item.currency === "vàng" ? "selected" : ""
        }>Vàng</option>
        <option value="ngọc" ${
          item.currency === "ngọc" ? "selected" : ""
        }>Ngọc</option>
        <option value="ruby" ${
          item.currency === "ruby" ? "selected" : ""
        }>Ruby</option>
        <option value="coupon" ${
          item.currency === "coupon" ? "selected" : ""
        }>Coupon</option>
      </select>
    </div>
    <div style="margin-bottom:10px;">
      <label>Đánh dấu là mới (isNew):</label><br>
      <input type="checkbox" id="edit-isNew" ${item.isNew ? "checked" : ""}>
    </div>
    <div style="margin-bottom:10px;">
      <label>Có bán (isSell):</label><br>
      <input type="checkbox" id="edit-isSell" ${
        item.isSell !== false ? "checked" : ""
      }>
    </div>
    <div style="margin-bottom:10px;">
      <label>Icon Spec (item_spec):</label><br>
      <input type="number" id="edit-iconSpec" style="width:100%;" value="${
        item.iconSpec || 0
      }">
    </div>
    <div style="margin-bottom:10px;">
      <label>Chỉnh sửa options và param:</label>
      <div id="edit-options" style="max-height:150px; overflow-y:auto; border:1px solid #ddd; padding:4px;">
        ${buildOptionRows()}
      </div>
    </div>
    <div style="display:flex; justify-content:flex-end; gap:8px;">
      <button id="cancel-edit" class="bg-gray-300 px-3 py-1 rounded">Hủy</button>
      <button id="save-edit" class="bg-blue-600 text-white px-3 py-1 rounded">Cập nhật</button>
    </div>
  `;
  overlay.appendChild(modal);
  document.body.appendChild(overlay);
  // Add interactions to options checkboxes
  modal.querySelectorAll(".edit-opt-checkbox").forEach((cb) => {
    cb.addEventListener("change", () => {
      const containerDiv = cb.closest(".edit-option-row");
      const paramInput = containerDiv.querySelector(".edit-opt-param");
      if (!paramInput) return;
      paramInput.disabled = !cb.checked;
      if (!cb.checked) paramInput.value = "";
    });
  });
  // Cancel button
  modal.querySelector("#cancel-edit").addEventListener("click", () => {
    document.body.removeChild(overlay);
  });
  // Save button
  modal.querySelector("#save-edit").addEventListener("click", () => {
    const newCost = parseInt(modal.querySelector("#edit-cost").value, 10);
    const newCurrency = modal.querySelector("#edit-currency").value;
    const updates = {};
    if (!isNaN(newCost) && newCost !== item.cost) updates.cost = newCost;
    if (newCurrency && newCurrency !== item.currency)
      updates.currency = newCurrency;
    // Collect options
    const opts = [];
    modal.querySelectorAll(".edit-opt-checkbox").forEach((cb) => {
      if (cb.checked) {
        const id = parseInt(cb.value, 10);
        const paramInput = cb
          .closest(".edit-option-row")
          .querySelector(".edit-opt-param");
        const param = parseInt(paramInput.value, 10);
        if (!isNaN(id) && !isNaN(param)) {
          opts.push({ id, param });
        }
      }
    });
    if (opts.length > 0) updates.options = opts;
    // New flags and iconSpec
    const newIsNew = modal.querySelector("#edit-isNew").checked;
    const newIsSell = modal.querySelector("#edit-isSell").checked;
    const newIconSpecVal = parseInt(
      modal.querySelector("#edit-iconSpec").value,
      10
    );
    if (newIsNew !== item.isNew) updates.isNew = newIsNew;
    if (newIsSell !== item.isSell) updates.isSell = newIsSell;
    if (!isNaN(newIconSpecVal) && newIconSpecVal !== item.iconSpec)
      updates.iconSpec = newIconSpecVal;
    // Only update if something changed
    if (Object.keys(updates).length > 0) {
      const shopId = tab.shopId;
      const originalTabIndex = tab.tabIndex;
      fetch(
        `/api/shops/${shopId}/tabs/${originalTabIndex}/items/${itemIndex}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(updates),
        }
      )
        .then((resp) => resp.json())
        .then((data) => {
          if (data.success) {
            // Close modal and reload shops
            document.body.removeChild(overlay);
            loadShopsFromApi();
          } else {
            alert(data.error || "Có lỗi xảy ra");
          }
        })
        .catch((err) => {
          alert("Có lỗi khi gửi yêu cầu");
          console.error(err);
        });
    } else {
      // Nothing changed, just close modal
      document.body.removeChild(overlay);
    }
  });
}

/**
 * Tải dữ liệu người chơi và hiển thị trong tab Players. Mỗi người chơi hiển thị
 * thông tin cơ bản và danh sách vật phẩm họ sở hữu.
 */
function loadPlayers() {
  fetch("/api/players")
    .then((resp) => resp.json())
    .then((data) => {
      playersData = data;
      renderPlayers();
    })
    .catch((err) => {
      document.getElementById("players-list").innerHTML =
        "<p>Lỗi tải danh sách người chơi.</p>";
      console.error(err);
    });
}

function renderPlayers() {
  const listEl = document.getElementById("players-list");
  // Clear previous contents
  listEl.innerHTML = "";
  // Loop through each player and build a card with summary and expandable details
  playersData.forEach((player) => {
    const card = document.createElement("div");
    // Use Tailwind classes for styling: white background, border, rounded corners and shadow
    card.className =
      "bg-white border border-gray-300 rounded p-4 shadow-sm mb-4";
    // Build stats representation.  If stats is an array, list each index and value;
    // if an object, list key-value pairs; otherwise show as string.
    let statsHtml = "";
    if (player.stats) {
      if (Array.isArray(player.stats)) {
        statsHtml =
          '<ul class="list-disc list-inside text-sm text-gray-700">' +
          player.stats
            .map((val, idx) => `<li>Chỉ số ${idx}: ${val}</li>`)
            .join("") +
          "</ul>";
      } else if (typeof player.stats === "object") {
        statsHtml =
          '<ul class="list-disc list-inside text-sm text-gray-700">' +
          Object.entries(player.stats)
            .map(([k, v]) => `<li>${k}: ${v}</li>`)
            .join("") +
          "</ul>";
      } else {
        statsHtml = `<span class="text-sm text-gray-700">${String(
          player.stats
        )}</span>`;
      }
    }
    // Build items grid.  Each item will display its icon, name, quantity and options
    let itemsGrid = "";
    if (player.items && player.items.length > 0) {
      itemsGrid =
        '<div class="grid grid-cols-2 md:grid-cols-3 gap-2">' +
        player.items
          .map((it) => {
            const optsDesc = (it.options || [])
              .map((opt) => `Option ${opt.id}: +${opt.param}`)
              .join(", ");
            return `
          <div class="flex items-start p-2 border border-gray-200 rounded">
            <img src="${it.icon}" alt="${it.name}" class="w-8 h-8 mr-2">
            <div class="text-sm">
              <div class="font-medium">${it.name}</div>
              <div class="text-gray-600">x${it.quantity}</div>
              ${
                optsDesc
                  ? `<div class="text-xs text-gray-500">${optsDesc}</div>`
                  : ""
              }
            </div>
          </div>
        `;
          })
          .join("") +
        "</div>";
    }
    // Build tasks and side tasks HTML.  We iterate through each task object and display
    // a pretty-printed JSON representation.  You can further customise this to
    // display meaningful fields (e.g. task name or status).
    let tasksHtml = "";
    if (player.tasks && player.tasks.length > 0) {
      tasksHtml =
        '<ul class="list-disc list-inside text-sm text-gray-700">' +
        player.tasks
          .map(
            (t) =>
              `<li><pre class="whitespace-pre-wrap">${JSON.stringify(
                t,
                null,
                2
              )}</pre></li>`
          )
          .join("") +
        "</ul>";
    }
    let sideTasksHtml = "";
    if (player.sideTasks && player.sideTasks.length > 0) {
      sideTasksHtml =
        '<ul class="list-disc list-inside text-sm text-gray-700">' +
        player.sideTasks
          .map(
            (t) =>
              `<li><pre class="whitespace-pre-wrap">${JSON.stringify(
                t,
                null,
                2
              )}</pre></li>`
          )
          .join("") +
        "</ul>";
    }
    let itemTimesHtml = "";
    if (player.itemTimes && player.itemTimes.length > 0) {
      itemTimesHtml = `<pre class="whitespace-pre-wrap text-sm text-gray-700">${JSON.stringify(
        player.itemTimes,
        null,
        2
      )}</pre>`;
    }
    // Compose the HTML structure with a summary header and a button to view details in a modal
    card.innerHTML = `
      <div class="flex items-center">
        <img src="${player.avatarIcon || "/icon/x4/3013.png"}" alt="${
      player.name
    }" class="w-12 h-12 rounded-full mr-4">
        <div class="flex-1">
          <div class="font-semibold text-lg">${player.name}</div>
          <div class="text-sm text-gray-700">Level ${player.level} | Power ${
      player.power
    }</div>
          <div class="text-sm text-gray-700">HP ${player.hp}/${
      player.hpBase
    } | MP ${player.mp}/${player.mpBase}</div>
        </div>
      </div>
      <button class="view-player-detail-btn mt-2 text-blue-600 hover:underline text-sm">Xem chi tiết</button>
    `;
    // Attach click handler to open detailed information in a new tab.  When clicked
    // we open player_detail.html with the player's id in the query string.
    const viewBtn = card.querySelector(".view-player-detail-btn");
    viewBtn.addEventListener("click", () => {
      // Show player detail in a modal instead of opening a new page
      showPlayerDetail(player);
    });
    listEl.appendChild(card);
  });
}

/**
 * Tải dữ liệu item templates và option templates rồi dựng form tạo giftcode.
 */
function loadGiftcodeForm() {
  Promise.all([
    fetch("/api/item_templates").then((res) => res.json()),
    fetch("/api/item_option_templates").then((res) => res.json()),
  ])
    .then(([items, options]) => {
      itemTemplates = items;
      optionTemplates = options;
      renderGiftcodeForm();
    })
    .catch((err) => {
      document.getElementById("giftcode-form-container").innerHTML =
        "<p>Lỗi tải dữ liệu giftcode.</p>";
      console.error(err);
    });
}

function renderGiftcodeForm() {
  const container = document.getElementById("giftcode-form-container");
  container.innerHTML = "";
  const form = document.createElement("div");
  form.id = "giftcode-form";
  form.style.border = "1px solid #ddd";
  form.style.borderRadius = "4px";
  form.style.padding = "10px";
  form.innerHTML = `
    <div style="margin-bottom:10px;">
      <label>Giftcode:</label><br>
      <input type="text" id="giftcode-code" style="width:100%;">
    </div>
    <div style="margin-bottom:10px;">
      <label>Số lần sử dụng (countLeft):</label><br>
      <input type="number" id="giftcode-count" style="width:100%;" value="1" min="1">
    </div>
    <div style="margin-bottom:10px;">
      <label>Loại giftcode (type):</label><br>
      <select id="giftcode-type" style="width:100%;">
        <option value="0">0</option>
        <option value="1">1</option>
      </select>
    </div>
    <div style="margin-bottom:10px;">
      <label>Ngày hết hạn (expired):</label><br>
      <input type="date" id="giftcode-expired" style="width:100%;">
    </div>
    <div id="gc-items-list"></div>
    <button id="gc-add-item" class="bg-green-500 text-white px-2 py-1 rounded mt-2">Thêm vật phẩm</button>
    <button id="create-giftcode" class="bg-blue-600 text-white px-3 py-1 rounded mt-2 ml-2">Tạo Giftcode</button>
    <p id="giftcode-message" style="color:#c00; margin-top:8px;"></p>
  `;
  container.appendChild(form);
  const listEl = form.querySelector("#gc-items-list");
  // Helper to build option rows for each item row
  const createGiftcodeOptionRows = (selectedOpts) => {
    return optionTemplates
      .map((opt) => {
        const existing = Array.isArray(selectedOpts)
          ? selectedOpts.find((o) => o.id === opt.id)
          : undefined;
        const checked = existing ? "checked" : "";
        const paramVal = existing ? existing.param : "";
        return `
        <div class="giftcode-option-row" style="margin-bottom:6px;">
          <label><input type="checkbox" class="gc-opt-checkbox" value="${
            opt.id
          }" ${checked}> ${opt.description}</label>
          <input type="number" class="gc-opt-param" placeholder="param" style="width:60px; margin-left:4px;" ${
            checked ? "" : "disabled"
          } value="${paramVal}">
        </div>
      `;
      })
      .join("");
  };
  // Function to add a new item row to the list. If existingItem is provided, it pre‑fills fields.
  function addGiftcodeItemRow(containerEl, existingItem) {
    const row = document.createElement("div");
    row.className = "gc-item-entry";
    row.style.border = "1px solid #ddd";
    row.style.padding = "8px";
    row.style.marginBottom = "10px";
    row.style.borderRadius = "4px";
    const itemOptionsHtml = itemTemplates
      .map(
        (it) =>
          `<option value="${it.id}" ${
            existingItem && existingItem.temp_id == it.id ? "selected" : ""
          }>${it.name}</option>`
      )
      .join("");
    const qtyVal =
      existingItem && existingItem.quantity ? existingItem.quantity : 1;
    row.innerHTML = `
      <div style="display:flex; flex-wrap:wrap; gap:8px; align-items:flex-end;">
        <div style="flex:1; min-width:160px;">
          <label>Item:</label><br>
          <select class="gc-item-select" style="width:100%;">${itemOptionsHtml}</select>
        </div>
        <div style="flex:1; min-width:80px;">
          <label>Số lượng:</label><br>
          <input type="number" class="gc-item-qty" value="${qtyVal}" min="1" style="width:100%;">
        </div>
        <div style="flex-basis:100%; height:0;"></div>
        <!-- Description of selected item -->
        <div class="gc-item-description text-xs text-gray-500" style="margin-top:4px; flex-basis:100%;"></div>
        <div class="gc-options-container" style="margin-top:6px; max-height:150px; overflow-y:auto; border:1px solid #ddd; padding:4px; width:100%;">
          ${createGiftcodeOptionRows(
            existingItem && existingItem.options ? existingItem.options : []
          )}
        </div>
        <button class="gc-remove-item bg-red-500 text-white px-2 py-1 rounded mt-2" type="button">Xóa</button>
      </div>
    `;
    // Attach option checkbox change handlers within this row
    row.querySelectorAll(".gc-opt-checkbox").forEach((cb) => {
      cb.addEventListener("change", () => {
        const containerDiv = cb.closest(".giftcode-option-row");
        const paramInput = containerDiv.querySelector(".gc-opt-param");
        if (!paramInput) return;
        paramInput.disabled = !cb.checked;
        if (!cb.checked) paramInput.value = "";
      });
    });
    // Remove button handler
    row.querySelector(".gc-remove-item").addEventListener("click", () => {
      containerEl.removeChild(row);
    });
    // Show description for selected item and update on change
    const descEl = row.querySelector(".gc-item-description");
    const selectEl = row.querySelector(".gc-item-select");
    function updateDesc() {
      const selId = parseInt(selectEl.value, 10);
      const tpl = itemTemplates.find((it) => it.id === selId);
      descEl.textContent = tpl && tpl.description ? tpl.description : "";
    }
    updateDesc();
    selectEl.addEventListener("change", updateDesc);
    containerEl.appendChild(row);
  }
  // Add an initial item row
  addGiftcodeItemRow(listEl, null);
  // Add item button
  form.querySelector("#gc-add-item").addEventListener("click", (ev) => {
    ev.preventDefault();
    addGiftcodeItemRow(listEl, null);
  });
  // Create giftcode handler
  form.querySelector("#create-giftcode").addEventListener("click", (ev) => {
    ev.preventDefault();
    const msgEl = form.querySelector("#giftcode-message");
    const code = form.querySelector("#giftcode-code").value.trim();
    if (!code) {
      msgEl.style.color = "#c00";
      msgEl.textContent = "Vui lòng nhập giftcode.";
      return;
    }
    const countLeftVal = parseInt(
      form.querySelector("#giftcode-count").value,
      10
    );
    const typeVal = parseInt(form.querySelector("#giftcode-type").value, 10);
    const expiredDate = form.querySelector("#giftcode-expired").value;
    const items = [];
    listEl.querySelectorAll(".gc-item-entry").forEach((row) => {
      const tempId = parseInt(row.querySelector(".gc-item-select").value, 10);
      const qty = parseInt(row.querySelector(".gc-item-qty").value, 10);
      const opts = [];
      row.querySelectorAll(".giftcode-option-row").forEach((optRow) => {
        const cb = optRow.querySelector(".gc-opt-checkbox");
        if (cb.checked) {
          const id = parseInt(cb.value, 10);
          const param = parseInt(
            optRow.querySelector(".gc-opt-param").value,
            10
          );
          if (!isNaN(id) && !isNaN(param)) {
            opts.push({ id, param });
          }
        }
      });
      if (!isNaN(tempId)) {
        items.push({
          temp_id: tempId,
          quantity: isNaN(qty) ? 1 : qty,
          options: opts,
        });
      }
    });
    if (items.length === 0) {
      msgEl.style.color = "#c00";
      msgEl.textContent = "Vui lòng thêm ít nhất một vật phẩm.";
      return;
    }
    const payload = {
      code,
      items,
      countLeft: isNaN(countLeftVal) ? 1 : countLeftVal,
      type: isNaN(typeVal) ? 0 : typeVal,
      expired: expiredDate || null,
    };
    fetch("/api/giftcodes", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          msgEl.style.color = "#090";
          msgEl.textContent = "Tạo giftcode thành công!";
          // Reset form fields
          form.querySelector("#giftcode-code").value = "";
          form.querySelector("#giftcode-count").value = "1";
          form.querySelector("#giftcode-type").value = "0";
          form.querySelector("#giftcode-expired").value = "";
          // Remove all item rows and add one empty row
          listEl.innerHTML = "";
          addGiftcodeItemRow(listEl, null);
          // Reload list to include new giftcode
          loadGiftcodesList();
        } else {
          msgEl.style.color = "#c00";
          msgEl.textContent = data.error || "Có lỗi.";
        }
      })
      .catch((err) => {
        msgEl.style.color = "#c00";
        msgEl.textContent = "Có lỗi khi gửi yêu cầu.";
        console.error(err);
      });
  });
}

/**
 * Tải danh sách giftcode từ API và hiển thị trong giftcode list section.
 */
function loadGiftcodesList() {
  fetch("/api/giftcodes")
    .then((res) => res.json())
    .then((data) => {
      giftcodesData = Array.isArray(data) ? data : [];
      renderGiftcodesList();
    })
    .catch((err) => {
      console.error(err);
      const listEl = document.getElementById("giftcode-list");
      if (listEl) listEl.innerHTML = "<p>Lỗi tải giftcode.</p>";
    });
}

/**
 * Hiển thị danh sách giftcode. Mỗi giftcode có thể mở rộng để xem các
 * vật phẩm bên trong bằng cách click.
 */
function renderGiftcodesList() {
  const listEl = document.getElementById("giftcode-list");
  if (!listEl) return;
  listEl.innerHTML = "";
  // Show message when no giftcodes exist
  if (!giftcodesData || giftcodesData.length === 0) {
    listEl.innerHTML = "<p>Chưa có giftcode nào.</p>";
    return;
  }
  // For each giftcode, create a card with a clickable header and a collapsible details section
  giftcodesData.forEach((gfc) => {
    const card = document.createElement("div");
    card.className =
      "bg-white border border-gray-300 rounded p-4 shadow-sm mb-4";
    // Header shows code and remaining uses.  Clicking toggles the details below.
    const headerEl = document.createElement("div");
    headerEl.className = "flex justify-between items-center cursor-pointer";
    headerEl.innerHTML = `
      <div class="font-semibold">${gfc.code}</div>
      <div class="text-sm text-gray-600">${gfc.countLeft} lần</div>
    `;
    // Details container, initially hidden
    const detailsEl = document.createElement("div");
    detailsEl.className = "giftcode-details mt-2 hidden";
    // Build items grid similar to shop items.  Each option description is placed on its own line.
    let itemsGrid;
    if (gfc.items && gfc.items.length > 0) {
      itemsGrid =
        '<div class="grid grid-cols-2 md:grid-cols-3 gap-2">' +
        gfc.items
          .map((it) => {
            const optsDesc =
              it.options && it.options.length > 0
                ? it.options.map((o) => o.description).join("<br>")
                : "";
            return `
          <div class="flex items-start p-2 border border-gray-200 rounded">
            <img src="${it.icon}" alt="${it.name}" class="w-8 h-8 mr-2">
            <div class="text-sm">
              <div class="font-medium">${it.name}</div>
              ${
                it.description
                  ? `<div class="text-xs text-gray-500">${it.description}</div>`
                  : ""
              }
              <div class="text-gray-600">x${it.quantity}</div>
              ${
                optsDesc
                  ? `<div class="text-xs text-gray-500">${optsDesc}</div>`
                  : ""
              }
            </div>
          </div>
        `;
          })
          .join("") +
        "</div>";
    } else {
      itemsGrid = '<p class="text-sm text-gray-700">Không có vật phẩm.</p>';
    }
    detailsEl.innerHTML = itemsGrid;
    // Toggle visibility on header click
    headerEl.addEventListener("click", () => {
      detailsEl.classList.toggle("hidden");
    });
    card.appendChild(headerEl);
    card.appendChild(detailsEl);
    // Add edit button for each giftcode
    const editBtn = document.createElement("button");
    editBtn.className = "mt-2 text-blue-600 hover:underline text-sm";
    editBtn.textContent = "Chỉnh sửa";
    editBtn.addEventListener("click", (ev) => {
      ev.stopPropagation();
      showEditGiftcodeDialog(gfc);
    });
    card.appendChild(editBtn);
    listEl.appendChild(card);
  });
}

/**
 * Load data for the mail form. This ensures that item and option templates
 * are available before rendering the form. Called when switching to the
 * 'mail' tab.
 */
function loadMailForm() {
  // If templates already loaded, just render the form
  if (
    itemTemplates &&
    itemTemplates.length > 0 &&
    optionTemplates &&
    optionTemplates.length > 0
  ) {
    renderMailForm();
    return;
  }
  Promise.all([
    itemTemplates && itemTemplates.length > 0
      ? Promise.resolve(itemTemplates)
      : fetch("/api/item_templates").then((res) => res.json()),
    optionTemplates && optionTemplates.length > 0
      ? Promise.resolve(optionTemplates)
      : fetch("/api/item_option_templates").then((res) => res.json()),
  ])
    .then(([items, options]) => {
      if (!itemTemplates || itemTemplates.length === 0) itemTemplates = items;
      if (!optionTemplates || optionTemplates.length === 0)
        optionTemplates = options;
      renderMailForm();
    })
    .catch((err) => {
      const container = document.getElementById("mail-form-container");
      if (container)
        container.innerHTML = "<p>Lỗi tải dữ liệu item/option.</p>";
      console.error(err);
    });
}

/**
 * Render the mail sending form. Allows specifying a player by ID or name,
 * adding multiple items with quantity and options, and sending the items
 * to the player's mailbox via POST /api/send-mail.
 */
function renderMailForm() {
  const container = document.getElementById("mail-form-container");
  if (!container) return;
  container.innerHTML = "";
  // Create the form container
  const form = document.createElement("div");
  form.id = "mail-form";
  form.style.border = "1px solid #ddd";
  form.style.borderRadius = "4px";
  form.style.padding = "10px";
  // Build the static part of the form
  form.innerHTML = `
    <div style="margin-bottom:10px;">
      <label>Mã người chơi (ID):</label><br>
      <input type="number" id="mail-player-id" style="width:100%;" placeholder="Nhập ID người chơi">
    </div>
    <div style="margin-bottom:10px;">
      <label>Tên người chơi:</label><br>
      <input type="text" id="mail-player-name" style="width:100%;" placeholder="Nhập tên người chơi">
    </div>
    <div id="mail-items-list"></div>
    <button id="mail-add-item" class="bg-green-500 text-white px-2 py-1 rounded mt-2" type="button">Thêm vật phẩm</button>
    <button id="send-mail-button" class="bg-blue-600 text-white px-3 py-1 rounded mt-2 ml-2" type="button">Gửi</button>
    <p id="mail-message" style="color:#c00; margin-top:8px;"></p>
  `;
  container.appendChild(form);
  const listEl = form.querySelector("#mail-items-list");
  // Helper: build option rows for each item row in the mail form
  const createMailOptionRows = (selectedOpts) => {
    return optionTemplates
      .map((opt) => {
        const existing = Array.isArray(selectedOpts)
          ? selectedOpts.find((o) => o.id === opt.id)
          : undefined;
        const checked = existing ? "checked" : "";
        const paramVal = existing ? existing.param : "";
        return `
        <div class="mail-option-row" style="margin-bottom:6px;">
          <label><input type="checkbox" class="mail-opt-checkbox" value="${
            opt.id
          }" ${checked}> ${opt.description}</label>
          <input type="number" class="mail-opt-param" placeholder="param" style="width:60px; margin-left:4px;" ${
            checked ? "" : "disabled"
          } value="${paramVal}">
        </div>
      `;
      })
      .join("");
  };
  // Function to add a new item row to the list
  function addMailItemRow(containerEl, existingItem) {
    const row = document.createElement("div");
    row.className = "mail-item-entry";
    row.style.border = "1px solid #ddd";
    row.style.padding = "8px";
    row.style.marginBottom = "10px";
    row.style.borderRadius = "4px";
    const itemOptionsHtml = itemTemplates
      .map(
        (it) =>
          `<option value="${it.id}" ${
            existingItem && existingItem.temp_id == it.id ? "selected" : ""
          }>${it.name}</option>`
      )
      .join("");
    const qtyVal =
      existingItem && existingItem.quantity ? existingItem.quantity : 1;
    row.innerHTML = `
      <div style="display:flex; flex-wrap:wrap; gap:8px; align-items:flex-end;">
        <div style="flex:1; min-width:160px;">
          <label>Item:</label><br>
          <select class="mail-item-select" style="width:100%;">${itemOptionsHtml}</select>
        </div>
        <div style="flex:1; min-width:80px;">
          <label>Số lượng:</label><br>
          <input type="number" class="mail-item-qty" value="${qtyVal}" min="1" style="width:100%;">
        </div>
        <div style="flex-basis:100%; height:0;"></div>
        <!-- Description of selected item -->
        <div class="mail-item-description text-xs text-gray-500" style="margin-top:4px; flex-basis:100%;"></div>
        <div class="mail-options-container" style="margin-top:6px; max-height:150px; overflow-y:auto; border:1px solid #ddd; padding:4px; width:100%;">
          ${createMailOptionRows(
            existingItem && existingItem.options ? existingItem.options : []
          )}
        </div>
        <button class="mail-remove-item bg-red-500 text-white px-2 py-1 rounded mt-2" type="button">Xóa</button>
      </div>
    `;
    // Bind option checkbox interactions
    row.querySelectorAll(".mail-opt-checkbox").forEach((cb) => {
      cb.addEventListener("change", () => {
        const containerDiv = cb.closest(".mail-option-row");
        const paramInput = containerDiv.querySelector(".mail-opt-param");
        if (!paramInput) return;
        paramInput.disabled = !cb.checked;
        if (!cb.checked) paramInput.value = "";
      });
    });
    // Bind remove button
    row.querySelector(".mail-remove-item").addEventListener("click", () => {
      containerEl.removeChild(row);
    });
    // Show description for selected item and update on change
    const descEl = row.querySelector(".mail-item-description");
    const selectEl = row.querySelector(".mail-item-select");
    function updateDesc() {
      const selId = parseInt(selectEl.value, 10);
      const tpl = itemTemplates.find((it) => it.id === selId);
      descEl.textContent = tpl && tpl.description ? tpl.description : "";
    }
    updateDesc();
    selectEl.addEventListener("change", updateDesc);
    containerEl.appendChild(row);
  }
  // Add an initial empty item row
  addMailItemRow(listEl, null);
  // Add new item row when clicking "Thêm vật phẩm"
  form.querySelector("#mail-add-item").addEventListener("click", (ev) => {
    ev.preventDefault();
    addMailItemRow(listEl, null);
  });
  // Handle send mail button
  form.querySelector("#send-mail-button").addEventListener("click", (ev) => {
    ev.preventDefault();
    const msgEl = form.querySelector("#mail-message");
    msgEl.style.color = "#c00";
    msgEl.textContent = "";
    const idVal = parseInt(form.querySelector("#mail-player-id").value, 10);
    const nameVal = (
      form.querySelector("#mail-player-name").value || ""
    ).trim();
    if ((isNaN(idVal) || idVal <= 0) && !nameVal) {
      msgEl.textContent = "Vui lòng nhập ID hoặc tên người chơi.";
      return;
    }
    // Collect items
    const items = [];
    listEl.querySelectorAll(".mail-item-entry").forEach((row) => {
      const tempId = parseInt(row.querySelector(".mail-item-select").value, 10);
      const qty = parseInt(row.querySelector(".mail-item-qty").value, 10);
      const opts = [];
      row.querySelectorAll(".mail-option-row").forEach((optRow) => {
        const cb = optRow.querySelector(".mail-opt-checkbox");
        if (cb.checked) {
          const id = parseInt(cb.value, 10);
          const param = parseInt(
            optRow.querySelector(".mail-opt-param").value,
            10
          );
          if (!isNaN(id) && !isNaN(param)) {
            opts.push({ id, param });
          }
        }
      });
      if (!isNaN(tempId)) {
        items.push({
          temp_id: tempId,
          quantity: isNaN(qty) ? 1 : qty,
          options: opts,
        });
      }
    });
    if (items.length === 0) {
      msgEl.textContent = "Vui lòng thêm ít nhất một vật phẩm.";
      return;
    }
    const payload = {
      items,
    };
    if (!isNaN(idVal) && idVal > 0) payload.playerId = idVal;
    if (nameVal) payload.playerName = nameVal;
    fetch("/api/send-mail", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.success) {
          msgEl.style.color = "#090";
          msgEl.textContent = "Gửi vật phẩm thành công!";
          // Reset form fields
          form.querySelector("#mail-player-id").value = "";
          form.querySelector("#mail-player-name").value = "";
          listEl.innerHTML = "";
          addMailItemRow(listEl, null);
        } else {
          msgEl.textContent = data.error || "Có lỗi xảy ra.";
        }
      })
      .catch((err) => {
        msgEl.textContent = "Có lỗi khi gửi yêu cầu.";
        console.error(err);
      });
  });
}

/**
 * Hiển thị chi tiết người chơi trong một hộp thoại modal. Thông tin được
 * trình bày gọn gàng và có thể đóng lại. Hàm này được gọi khi người
 * dùng click vào nút "Xem chi tiết" trên thẻ người chơi.
 * @param {Object} player Đối tượng người chơi chứa thông tin chi tiết
 */

function showPlayerDetail(player) {
  // Fetch fresh detail from API to ensure we have parsed fields exactly like server
  fetch(`/api/player_detail?id=${player.id}`)
    .then((resp) => resp.json())
    .then((payload) => {
      console.log(payload)
      if (!payload.ok) throw new Error(payload.message || "Lỗi tải chi tiết");
      const d = payload.detail;
      const maps = payload.maps || [];

      // Build item grids
      const renderItem = (it) => {
        console.log(it)
        const optsDesc = (it.options || [])
          .map((opt) => `+${opt.param} ${opt.id}`)
          .join(", ");
        return `
          <div class="flex items-start p-2 border border-gray-200 rounded">
            <img src="${it.icon}" alt="${it.name}" class="w-8 h-8 mr-2">
            <div class="text-sm">
              <div class="font-medium">${it.name}</div>
              <div class="text-gray-600">x${it.quantity}</div>
              ${
                optsDesc
                  ? `<div class="text-xs text-gray-500">${optsDesc}</div>`
                  : ""
              }
            </div>
          </div>`;
      };

      const bodyGrid =
        (d.itemsBody || []).map(renderItem).join("") ||
        '<div class="text-sm text-gray-500">Trống</div>';
      const bagGrid =
        (d.itemsBag || []).map(renderItem).join("") ||
        '<div class="text-sm text-gray-500">Trống</div>';
      const petGrid =
        (d.pet?.body || []).map(renderItem).join("") ||
        '<div class="text-sm text-gray-500">Trống</div>';

      // Stats block in Vietnamese with labels matching server indices
      const s = d.stats || {};
      const statsHtml = `
        <ul class="list-disc list-inside text-sm text-gray-700">
          <li>Sức mạnh: ${s.power ?? 0}</li>
          <li>Tiềm năng: ${s.potential ?? 0}</li>
          <li>Thể lực: ${s.stamina ?? 0}/${s.staminaMax ?? 0}</li>
          <li>HP: ${s.hp ?? 0} (gốc ${s.hpBase ?? 0})</li>
          <li>KI: ${s.mp ?? 0} (gốc ${s.kiBase ?? 0})</li>
          <li>Sức đánh gốc: ${s.damageBase ?? 0}</li>
          <li>Giáp gốc: ${s.defenseBase ?? 0}</li>
          <li>Chí mạng gốc: ${s.critBase ?? 0}</li>
        </ul>`;

      // Teleport dropdown
      const mapOptions = maps
        .map(
          (m) =>
            `<option value="${m.id}" ${m.id === d.mapId ? "selected" : ""}>${
              m.id
            } - ${m.name}</option>`
        )
        .join("");

      // Make modal
      const overlay = document.createElement("div");
      overlay.className = "modal-overlay";
      overlay.innerHTML = '<div class="modal"></div>';
      const modal = overlay.querySelector(".modal");
      const bgToggle =
        '<label class="switch ml-2"><input type="checkbox" id="bgToggle"><span class="slider"></span></label>';

      modal.innerHTML = `
        <div class="flex items-center mb-3">
          <img src="${
            d.avatarIcon || player.avatarIcon || "/icon/x4/3013.png"
          }" class="w-12 h-12 rounded-full mr-3">
          <div class="flex-1">
            <div class="font-semibold text-lg">${d.name}</div>
            <div class="text-sm text-gray-700">Giới tính (chủng tộc): ${
              d.gender === 2 ? "Xayda" : d.gender === 1 ? "Namek" : "Trái Đất"
            }</div>
            <div class="text-sm">Map hiện tại: <strong>${
              d.mapName || d.mapId
            }</strong> (x=${d.x}, y=${d.y})</div>
          </div>
          <div class="text-sm">Nền: Trắng/Đen ${bgToggle}</div>
        </div>
        <div class="grid grid-cols-2 gap-3">
          <div>
            <div class="font-semibold mb-1">Chỉ số nhân vật</div>
            ${statsHtml}
          </div>
          <div>
            <div class="font-semibold mb-1">Dịch chuyển</div>
            <div class="flex items-center gap-2">
              <select id="tele-map" class="border rounded p-1 flex-1">${mapOptions}</select>
              <button id="btn-teleport" class="bg-blue-600 text-white px-3 py-1 rounded">Di chuyển</button>
            </div>
          </div>
        </div>
        <div class="mt-4">
          <div class="tabs">
            <button class="tab-btn active" data-tab="tb-body">Trang bị</button>
            <button class="tab-btn" data-tab="tb-bag">Hành trang</button>
            <button class="tab-btn" data-tab="tb-pet">Hành trang Pet</button>
          </div>
          <div id="tb-body" class="tab-panel grid grid-cols-2 md:grid-cols-3 gap-2 mt-2">${bodyGrid}</div>
          <div id="tb-bag" class="tab-panel hidden grid grid-cols-2 md:grid-cols-3 gap-2 mt-2">${bagGrid}</div>
          <div id="tb-pet" class="tab-panel hidden grid grid-cols-2 md:grid-cols-3 gap-2 mt-2">${petGrid}</div>
        </div>
        <div class="flex justify-end mt-4">
          <button id="close-player-detail" class="bg-blue-600 text-white px-3 py-1 rounded">Đóng</button>
        </div>`;

      document.body.appendChild(overlay);

      // Toggle tabs
      modal.querySelectorAll(".tab-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
          modal
            .querySelectorAll(".tab-btn")
            .forEach((b) => b.classList.remove("active"));
          btn.classList.add("active");
          modal
            .querySelectorAll(".tab-panel")
            .forEach((p) => p.classList.add("hidden"));
          modal.querySelector("#" + btn.dataset.tab).classList.remove("hidden");
        });
      });

      // Background toggle
      const toggle = modal.querySelector("#bgToggle");
      toggle.addEventListener("change", () => {
        modal.classList.toggle("dark", toggle.checked);
      });

      // Teleport action
      modal.querySelector("#btn-teleport").addEventListener("click", () => {
        const mapId = parseInt(modal.querySelector("#tele-map").value, 10);
        fetch("/api/players/teleport", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ playerId: d.id, mapId }),
        })
          .then((r) => r.json())
          .then((r) => {
            if (!r.ok) throw new Error(r.message || "Lỗi dịch chuyển");
            alert("Đã dịch chuyển!");
          })
          .catch((e) => alert(e.message));
      });

      // Close
      modal
        .querySelector("#close-player-detail")
        .addEventListener("click", () => overlay.remove());
    })
    .catch((err) => {
      alert("Không tải được chi tiết người chơi: " + err.message);
    });
}

/**
 * Hiển thị hộp thoại chỉnh sửa một giftcode hiện có. Hộp thoại cho phép
 * chỉnh sửa số lượt sử dụng còn lại, loại, ngày hết hạn và danh sách
 * vật phẩm. Người dùng có thể thêm hoặc xoá vật phẩm, cũng như
 * chỉnh sửa options của từng vật phẩm. Khi lưu, gửi yêu cầu PUT tới
 * server để cập nhật.
 * @param {Object} gfc Đối tượng giftcode với các thuộc tính code, countLeft, type, expired, items
 */
function showEditGiftcodeDialog(gfc) {
  // Ensure itemTemplates and optionTemplates are loaded. If not, fetch them first.
  const ensure =
    itemTemplates &&
    itemTemplates.length > 0 &&
    optionTemplates &&
    optionTemplates.length > 0
      ? Promise.resolve()
      : Promise.all([
          fetch("/api/item_templates")
            .then((res) => res.json())
            .then((data) => {
              itemTemplates = data;
            }),
          fetch("/api/item_option_templates")
            .then((res) => res.json())
            .then((data) => {
              optionTemplates = data;
            }),
        ]);
  ensure.then(() => {
    // Prepare overlay and modal
    const overlay = document.createElement("div");
    overlay.className = "modal-overlay";
    const modal = document.createElement("div");
    modal.className = "modal-box";
    modal.style.maxWidth = "640px";
    modal.style.maxHeight = "85vh";
    modal.style.overflowY = "auto";
    // Build base form structure
    modal.innerHTML = `
      <h3 class="mb-2 font-bold text-lg">Chỉnh sửa Giftcode</h3>
      <div style="margin-bottom:10px;">
        <label>Code:</label><br>
        <input type="text" id="edit-gc-code" style="width:100%;" value="${
          gfc.code
        }" disabled>
      </div>
      <div style="margin-bottom:10px;">
        <label>Số lần sử dụng (countLeft):</label><br>
        <input type="number" id="edit-gc-count" style="width:100%;" min="0" value="${
          gfc.countLeft
        }">
      </div>
      <div style="margin-bottom:10px;">
        <label>Loại giftcode (type):</label><br>
        <select id="edit-gc-type" style="width:100%;">
          <option value="0" ${gfc.type == 0 ? "selected" : ""}>0</option>
          <option value="1" ${gfc.type == 1 ? "selected" : ""}>1</option>
        </select>
      </div>
      <div style="margin-bottom:10px;">
        <label>Ngày hết hạn:</label><br>
        <input type="date" id="edit-gc-expired" style="width:100%;" value="${
          gfc.expired ? new Date(gfc.expired).toISOString().split("T")[0] : ""
        }">
      </div>
      <div id="edit-gc-items-list"></div>
      <button id="edit-gc-add-item" class="bg-green-500 text-white px-2 py-1 rounded mt-2">Thêm vật phẩm</button>
      <div class="flex justify-end mt-4 gap-2">
        <button id="edit-gc-cancel" class="bg-gray-300 px-3 py-1 rounded">Hủy</button>
        <button id="edit-gc-save" class="bg-blue-600 text-white px-3 py-1 rounded">Lưu</button>
      </div>
    `;
    overlay.appendChild(modal);
    document.body.appendChild(overlay);
    const listEl = modal.querySelector("#edit-gc-items-list");
    // Helper to build option rows for each item
    const createGiftcodeOptionRows = (selectedOpts) => {
      return optionTemplates
        .map((opt) => {
          const existing = Array.isArray(selectedOpts)
            ? selectedOpts.find((o) => o.id === opt.id)
            : undefined;
          const checked = existing ? "checked" : "";
          const paramVal = existing ? existing.param : "";
          return `
          <div class="giftcode-option-row" style="margin-bottom:6px;">
            <label><input type="checkbox" class="gc-opt-checkbox" value="${
              opt.id
            }" ${checked}> ${opt.description}</label>
            <input type="number" class="gc-opt-param" placeholder="param" style="width:60px; margin-left:4px;" ${
              checked ? "" : "disabled"
            } value="${paramVal}">
          </div>
        `;
        })
        .join("");
    };
    // Add item row function used for editing as well
    function addGiftcodeItemRow(containerEl, existingItem) {
      const row = document.createElement("div");
      row.className = "gc-item-entry";
      row.style.border = "1px solid #ddd";
      row.style.padding = "8px";
      row.style.marginBottom = "10px";
      row.style.borderRadius = "4px";
      const itemOptionsHtml = itemTemplates
        .map(
          (it) =>
            `<option value="${it.id}" ${
              existingItem && existingItem.temp_id == it.id ? "selected" : ""
            }>${it.name}</option>`
        )
        .join("");
      const qtyVal =
        existingItem && existingItem.quantity ? existingItem.quantity : 1;
      row.innerHTML = `
        <div style="display:flex; flex-wrap:wrap; gap:8px; align-items:flex-end;">
          <div style="flex:1; min-width:160px;">
            <label>Item:</label><br>
            <select class="gc-item-select" style="width:100%;">${itemOptionsHtml}</select>
          </div>
          <div style="flex:1; min-width:80px;">
            <label>Số lượng:</label><br>
            <input type="number" class="gc-item-qty" value="${qtyVal}" min="1" style="width:100%;">
          </div>
          <div style="flex-basis:100%; height:0;"></div>
          <!-- Description of selected item -->
          <div class="gc-item-description text-xs text-gray-500" style="margin-top:4px; flex-basis:100%;"></div>
          <div class="gc-options-container" style="margin-top:6px; max-height:150px; overflow-y:auto; border:1px solid #ddd; padding:4px; width:100%;">
            ${createGiftcodeOptionRows(
              existingItem && existingItem.options ? existingItem.options : []
            )}
          </div>
          <button class="gc-remove-item bg-red-500 text-white px-2 py-1 rounded mt-2" type="button">Xóa</button>
        </div>
      `;
      row.querySelectorAll(".gc-opt-checkbox").forEach((cb) => {
        cb.addEventListener("change", () => {
          const containerDiv = cb.closest(".giftcode-option-row");
          const paramInput = containerDiv.querySelector(".gc-opt-param");
          if (!paramInput) return;
          paramInput.disabled = !cb.checked;
          if (!cb.checked) paramInput.value = "";
        });
      });
      row.querySelector(".gc-remove-item").addEventListener("click", () => {
        containerEl.removeChild(row);
      });
      // Show description for selected item and update on change
      const descEl = row.querySelector(".gc-item-description");
      const selectEl = row.querySelector(".gc-item-select");
      function updateDesc() {
        const selId = parseInt(selectEl.value, 10);
        const tpl = itemTemplates.find((it) => it.id === selId);
        descEl.textContent = tpl && tpl.description ? tpl.description : "";
      }
      updateDesc();
      selectEl.addEventListener("change", updateDesc);
      containerEl.appendChild(row);
    }
    // Prepopulate existing items
    if (gfc.items && Array.isArray(gfc.items)) {
      gfc.items.forEach((it) => {
        // Each item may have options list with id/param/description; convert to {id,param}
        const opts = Array.isArray(it.options)
          ? it.options.map((o) => ({ id: o.id, param: o.param }))
          : [];
        addGiftcodeItemRow(listEl, {
          temp_id: it.temp_id,
          quantity: it.quantity,
          options: opts,
        });
      });
    }
    // Add item button
    modal.querySelector("#edit-gc-add-item").addEventListener("click", (ev) => {
      ev.preventDefault();
      addGiftcodeItemRow(listEl, null);
    });
    // Cancel button
    modal.querySelector("#edit-gc-cancel").addEventListener("click", () => {
      document.body.removeChild(overlay);
    });
    // Save button
    modal.querySelector("#edit-gc-save").addEventListener("click", () => {
      const countLeftVal = parseInt(
        modal.querySelector("#edit-gc-count").value,
        10
      );
      const typeVal = parseInt(modal.querySelector("#edit-gc-type").value, 10);
      const expiredVal = modal.querySelector("#edit-gc-expired").value;
      // Build items array
      const items = [];
      listEl.querySelectorAll(".gc-item-entry").forEach((row) => {
        const tempId = parseInt(row.querySelector(".gc-item-select").value, 10);
        const qty = parseInt(row.querySelector(".gc-item-qty").value, 10);
        const opts = [];
        row.querySelectorAll(".giftcode-option-row").forEach((optRow) => {
          const cb = optRow.querySelector(".gc-opt-checkbox");
          if (cb.checked) {
            const id = parseInt(cb.value, 10);
            const param = parseInt(
              optRow.querySelector(".gc-opt-param").value,
              10
            );
            if (!isNaN(id) && !isNaN(param)) {
              opts.push({ id, param });
            }
          }
        });
        if (!isNaN(tempId)) {
          items.push({
            temp_id: tempId,
            quantity: isNaN(qty) ? 1 : qty,
            options: opts,
          });
        }
      });
      const payload = {};
      if (!isNaN(countLeftVal)) payload.countLeft = countLeftVal;
      if (!isNaN(typeVal)) payload.type = typeVal;
      if (expiredVal) payload.expired = expiredVal;
      if (items.length > 0) payload.items = items;
      fetch(`/api/giftcodes/${encodeURIComponent(gfc.code)}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      })
        .then((res) => res.json())
        .then((data) => {
          if (data.success) {
            // reload giftcodes list
            loadGiftcodesList();
            document.body.removeChild(overlay);
          } else {
            alert(data.error || "Có lỗi khi cập nhật giftcode");
          }
        })
        .catch((err) => {
          alert("Có lỗi khi gửi yêu cầu");
          console.error(err);
        });
    });
  });
}

/**
 * Hiển thị section được chọn và ẩn các section khác. Đồng thời tải dữ liệu
 * cần thiết cho section đó.
 * @param {string} section One of 'shop', 'players', 'giftcode'
 */
function showSection(section) {
  const sections = {
    shop: document.getElementById("shop-section"),
    players: document.getElementById("players-section"),
    giftcode: document.getElementById("giftcode-section"),
    mail: document.getElementById("mail-section"),
  };
  for (const key in sections) {
    sections[key].style.display = key === section ? "block" : "none";
  }
  // Highlight nav button
  const navs = {
    shop: document.getElementById("nav-shop"),
    players: document.getElementById("nav-players"),
    giftcode: document.getElementById("nav-giftcode"),
    mail: document.getElementById("nav-mail"),
  };
  for (const key in navs) {
    navs[key].style.background = key === section ? "#555" : "#444";
  }
  // Load data when switching
  if (section === "players") {
    loadPlayers();
  } else if (section === "giftcode") {
    // Load both giftcode list and giftcode form when switching to giftcode tab
    loadGiftcodesList();
    loadGiftcodeForm();
  } else if (section === "mail") {
    // Load mail form when switching to mail tab
    loadMailForm();
  }
}

// Initialize loading after DOM is ready
document.addEventListener("DOMContentLoaded", () => {
  // Bind navigation buttons
  document
    .getElementById("nav-shop")
    .addEventListener("click", () => showSection("shop"));
  document
    .getElementById("nav-players")
    .addEventListener("click", () => showSection("players"));
  document
    .getElementById("nav-giftcode")
    .addEventListener("click", () => showSection("giftcode"));
  const navMail = document.getElementById("nav-mail");
  if (navMail) {
    navMail.addEventListener("click", () => showSection("mail"));
  }
  // Load initial shop data and show shop section by default
  loadShopsFromApi();
  showSection("shop");
});
