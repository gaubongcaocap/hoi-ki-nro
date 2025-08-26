/*
 * server_db.js
 *
 * This server provides a simple JSON API for administering shops, players
 * and gift codes directly against a MySQL database. It serves a static
 * front‑end from the ./web directory and exposes endpoints to list and
 * update shop items, list players and their items, and create gift codes.
 *
 * The server uses mysql2/promise to connect to the database. Connection
 * parameters are read from db_config.json located alongside this file.
 * You must run `npm install mysql2` in your project before starting
 * the server. See db_config.json for configuration format.
 */

const http = require("http");
const fs = require("fs");
const path = require("path");
const mysql = require("mysql2/promise");

// Location of static files and DB config
const WEB_DIR = path.join(__dirname, "web");
const DB_CONFIG_FILE = path.join(__dirname, "db_config.json");

// Caches
let shopsData = [];
let playersData = [];
let itemTemplates = [];
let itemOptionTemplates = [];
let mapsData = [];
const npcTemplateMap = {};
const itemTemplateMap = {};
// Map option ID to its description. Filled in loadItemOptionTemplates().
const optionTemplateMap = {};

let dbPool;

/**
 * Initialise a pooled MySQL connection using parameters from db_config.json
 */
async function initDbPool() {
  const raw = fs.readFileSync(DB_CONFIG_FILE, "utf8");
  const config = JSON.parse(raw);
  dbPool = await mysql.createPool(config);
}

async function loadMaps() {
  const [rows] = await dbPool.query('SELECT id, name FROM map_template');
  mapsData = rows.map(r => ({
    id: r.id,
    name: r.name
  }));
}

/**
 * Load all NPC templates into memory. Each entry contains a name and an
 * optional iconID (pulled from the `head` field) which is used to build
 * an icon path for the front‑end. If the iconID is missing, a
 * placeholder image will be used client side.
 */
async function loadNpcTemplates() {
  // In your database the NPC avatar column is named `avatar` and contains the icon ID.
  const [rows] = await dbPool.query(
    "SELECT id, name, avatar FROM npc_template"
  );
  rows.forEach((r) => {
    npcTemplateMap[r.id] = {
      name: r.name,
      iconID: r.avatar !== null ? parseInt(r.avatar, 10) : -1,
    };
  });
}

/**
 * Load all item templates from the database. Each entry contains an id,
 * name and an iconID which is used to build the icon path. These
 * templates are used both for rendering items in shops and for giftcode
 * creation.
 */
async function loadItemTemplates() {
  // In your database the item name column is `NAME` and the icon column is `icon_id`.
  // Fetch id, name, description and icon_id from item_template.  The description
  // column is used to show additional details about each item on the front‑end.
  const [rows] = await dbPool.query(
    "SELECT id, NAME AS name, description, icon_id FROM item_template"
  );
  itemTemplates = rows.map((r) => ({
    id: r.id,
    name: r.name,
    description: r.description || "",
    iconID: r.icon_id !== null ? parseInt(r.icon_id, 10) : -1,
  }));
  rows.forEach((r) => {
    itemTemplateMap[r.id] = {
      name: r.name,
      description: r.description || "",
      iconID: r.icon_id !== null ? parseInt(r.icon_id, 10) : -1,
    };
  });
}

/**
 * Load option templates from the database. Each entry contains an id
 * and a description. Descriptions are used on the front‑end when
 * presenting option selections.
 */

/**
 * Load all map templates (id, name) so front‑end can display map names and teleport list.
 */
async function loadMapsFromDB() {
  const [rows] = await dbPool.query(
    "SELECT id, name FROM map_template ORDER BY id"
  );
  mapsData = rows.map((r) => ({ id: parseInt(r.id, 10), name: r.name }));
}
async function loadItemOptionTemplates() {
  // Use correct column names: NAME for description
  const [rows] = await dbPool.query(
    "SELECT id, NAME AS name FROM item_option_template"
  );
  itemOptionTemplates = rows.map((r) => ({ id: r.id, description: r.name }));
  // Fill mapping for option descriptions
  rows.forEach((r) => {
    optionTemplateMap[r.id] = r.name;
  });
}

/**
 * Parse a raw options structure from the database into an array of
 * objects with numeric id and param. The `options` field in the DB is
 * stored either as a JSON array of objects or as a semicolon separated
 * string (e.g. "47:5;33:200"). Returns an empty array if no options.
 */

/**
 * Parse item list from JSON string (DB format) into array of objects
 * with tempId, quantity, options, name and icon path.
 */
// Tìm hàm parseItemList hiện có và thay thế bằng nội dung sau:
function parseItemList(jsonStr) {
  const result = [];
  if (!jsonStr) return result;
  let arr;

  // Giải mã chuỗi JSON ban đầu thành mảng
  if (typeof jsonStr === "string") {
    try {
      arr = JSON.parse(jsonStr);
    } catch {
      return result;
    }
  } else if (Array.isArray(jsonStr)) {
    arr = jsonStr;
  } else {
    return result;
  }

  // Duyệt từng item
  arr.forEach((it) => {
    // Nếu mỗi phần tử lại là chuỗi JSON, giải mã tiếp
    if (typeof it === "string") {
      try {
        it = JSON.parse(it);
      } catch {
        return;
      }
    }

    // Trường hợp mảng: [tempId, quantity, optionsJson, ...]
    if (Array.isArray(it)) {
      const tempId = parseInt(it[0], 10);
      const quantity = parseInt(it[1], 10) || 1;
      const optsJson = it[2];
      let opts = [];
      // Giải mã options
      if (optsJson) {
        try {
          const oa = JSON.parse(optsJson);
          opts = oa.map((o) => {
            // option có thể là chuỗi "[id,param]"
            if (typeof o === "string") o = JSON.parse(o);
            return {
              id: parseInt(o[0], 10),
              param: parseInt(o[1], 10),
            };
          });
        } catch {
          /* bỏ qua nếu lỗi */
        }
      }

      const tpl = itemTemplateMap[tempId] || {
        name: `Item ${tempId}`,
        description: "",
        iconID: -1,
      };
      result.push({
        tempId,
        name: tpl.name,
        description: tpl.description || "",
        icon:
          tpl.iconID >= 0
            ? `/icon/x4/${tpl.iconID}.png`
            : "placeholder_item.png",
        quantity,
        options: opts,
      });
    }
    // Trường hợp object: { temp_id, quantity, options }
    else if (it && typeof it === "object") {
      const tempId = parseInt(it.temp_id || it.tempId, 10);
      const quantity = parseInt(it.quantity, 10) || 1;
      const opts = parseOptions(it.options || []);
      const tpl = itemTemplateMap[tempId] || {
        name: `Item ${tempId}`,
        description: "",
        iconID: -1,
      };
      result.push({
        tempId,
        name: tpl.name,
        description: tpl.description || "",
        icon:
          tpl.iconID >= 0
            ? `/icon/x4/${tpl.iconID}.png`
            : "placeholder_item.png",
        quantity,
        options: opts,
      });
    }
  });

  return result;
}

function parseOptions(raw) {
  if (!raw) return [];
  try {
    const arr = typeof raw === "string" ? JSON.parse(raw) : raw;
    if (Array.isArray(arr)) {
      return arr.map((o) => ({
        id: parseInt(o.id, 10),
        param: parseInt(o.param, 10),
      }));
    }
  } catch (e) {
    // fall through
  }
  // Try splitting string
  if (typeof raw === "string") {
    return raw
      .split(";")
      .map((pair) => {
        const [idStr, paramStr] = pair.split(":");
        const id = parseInt(idStr, 10);
        const param = parseInt(paramStr, 10);
        return { id, param };
      })
      .filter((o) => !isNaN(o.id) && !isNaN(o.param));
  }
  return [];
}

/**
 * Load all shops and their tabs from the database. For each tab we parse
 * the `items` JSON array into a friendly structure for the front‑end. The
 * raw representation (compatible with the game's DB schema) is also kept
 * alongside so that we can write back updates easily.
 */
async function loadShops() {
  const sql = `SELECT s.id AS shop_id, s.npc_id, t.tab_index, t.tab_name, t.items AS items_json
               FROM shop s JOIN tab_shop t ON s.id = t.shop_id
               ORDER BY s.id, t.tab_index`;
  const [rows] = await dbPool.query(sql);
  const map = new Map();
  for (const r of rows) {
    if (!map.has(r.shop_id)) {
      const npc = npcTemplateMap[r.npc_id] || {
        name: `NPC ${r.npc_id}`,
        iconID: -1,
      };
      map.set(r.shop_id, {
        shopId: r.shop_id,
        npcId: r.npc_id,
        npcName: npc.name,
        npcIcon:
          npc.iconID >= 0
            ? `/icon/x4/${npc.iconID}.png`
            : "/icon/x4/3013.png",
        tabs: [],
      });
    }
    const shop = map.get(r.shop_id);
    // Parse raw items from JSON in DB
    let rawItems = [];
    if (r.items_json) {
      try {
        rawItems = JSON.parse(r.items_json);
      } catch (_) {
        rawItems = [];
      }
    }
    // Convert raw items to front‑end friendly items
    const parsedItems = rawItems
      // filter out entries explicitly marked as not for sale (is_sell === false)
      .filter((it) => it && it.is_sell !== false)
      .map((it) => {
        const tempId = parseInt(it.temp_id, 10);
        const tpl = itemTemplateMap[tempId] || {
          name: `Item ${tempId}`,
          description: "",
          iconID: -1,
        };
        // Map type_sell numeric to currency string
        const currency =
          it.type_sell === 1
            ? "ngọc"
            : it.type_sell === 3
            ? "ruby"
            : it.type_sell === 4
            ? "coupon"
            : "vàng";
        const icon =
          tpl.iconID >= 0
            ? `/icon/x4/${tpl.iconID}.png`
            : "placeholder_item.png";
        const options = parseOptions(it.options);
        // Additional flags for admin editing.  The DB stores a flag "is_new" to mark new
        // items and "is_sell" to toggle selling.  We also expose the raw
        // item_spec (iconSpec) so that admins can adjust the preview icon.
        const isNew =
          it.is_new === true || it.is_new === 1 || it.is_new === "1";
        // when is_sell is null or undefined, the item is assumed to be for sale
        const isSell =
          it.is_sell !== false && it.is_sell !== 0 && it.is_sell !== "0";
        const iconSpec =
          it.item_spec !== undefined && it.item_spec !== null
            ? parseInt(it.item_spec, 10)
            : 0;
        return {
          name: tpl.name,
          description: tpl.description || "",
          tempId: tempId,
          cost: parseInt(it.cost, 10) || 0,
          currency,
          icon,
          options,
          isNew,
          isSell,
          iconSpec,
        };
      });
    // Clean tab name by removing any '<' or '>' characters to avoid HTML tags in DB
    const cleanName = (r.tab_name || "").replace(/[<>]/g, " ");
    shop.tabs[r.tab_index] = {
      name: cleanName,
      items: parsedItems,
      rawItems,
      // Retain original shopId and tabIndex for client to identify the correct DB record when editing
      shopId: r.shop_id,
      tabIndex: r.tab_index,
    };
  }
  shopsData = Array.from(map.values());
}

/**
 * Load players and their items from the database. Because this source stores
 * items as JSON in the player table (items_body and items_bag), we need to
 * decode those fields. The data_point field (JSON array) is used to
 * extract power, and gender is used as a stand‑in for level. You can
 * customise this function to suit your schema.
 */
async function loadPlayers() {
  // Fetch additional columns for tasks, side tasks and item time.  These JSON fields
  // will be parsed and returned to the client so that the UI can display
  // comprehensive player information beyond just items and power.  If these
  // columns do not exist in your schema they will simply be undefined in
  // the result set and the parsing below will safely fallback.
  // Include the head column to compute or assign avatar icons
  const [rows] = await dbPool.query(
    "SELECT id, name, gender, head, data_point, items_body, items_bag, data_task, data_side_task, data_item_time FROM player"
  );
  const players = [];
  for (const r of rows) {
    // Extract power from data_point JSON array
    let power = 0;
    try {
      const dp = JSON.parse(r.data_point || "[]");
      if (Array.isArray(dp) && dp.length > 1) {
        power = parseInt(dp[1], 10) || 0;
      }
    } catch (_) {}
    const player = {
      id: r.id,
      name: r.name,
      level: r.gender,
      power,
      items: [],
      stats: null,
      tasks: [],
      sideTasks: [],
      itemTimes: [],
      head: r.head !== undefined ? parseInt(r.head, 10) : null,
      // Always use a default avatar image as requested
      avatarIcon: "/icon/x4/3013.png",
      hp: 0,
      mp: 0,
      hpBase: 0,
      mpBase: 0,
    };
    // Helper to parse items from JSON string
    const parseItems = (jsonStr) => {
      const list = [];
      if (!jsonStr) return list;
      try {
        const arr = JSON.parse(jsonStr);
        if (Array.isArray(arr)) {
          for (const it of arr) {
            let tempId;
            let quantity = 1;
            let opts = [];
            if (Array.isArray(it)) {
              tempId = parseInt(it[0], 10);
              quantity = it.length > 1 ? parseInt(it[1], 10) : 1;
            } else if (it && typeof it === "object") {
              tempId = parseInt(it.id || it.tempId || it.templateId, 10);
              quantity = parseInt(it.quantity || it.q || 1, 10);
              opts = parseOptions(it.options);
            }
            if (!isNaN(tempId)) {
              const tpl = itemTemplateMap[tempId] || {
                name: `Item ${tempId}`,
                iconID: -1,
              };
              list.push({
                name: tpl.name,
                tempId,
                quantity: isNaN(quantity) ? 1 : quantity,
                icon:
                  tpl.iconID >= 0
                    ? `/icon/x4/${tpl.iconID}.png`
                    : "placeholder_item.png",
                options: opts,
              });
            }
          }
        }
      } catch (_) {
        // ignore parse errors
      }
      return list;
    };
    const bodyItems = parseItems(r.items_body);
    const bagItems = parseItems(r.items_bag);
    player.items = bodyItems.concat(bagItems);
    // Parse raw data_point (stats).  This is expected to be a JSON array or
    // object.  If parsing fails, keep it as null.  Clients can display
    // this raw structure for debugging or further processing.
    try {
      if (r.data_point) {
        player.stats = JSON.parse(r.data_point);
        // If stats is an array, attempt to extract HP/MP and base values based on index

        if (Array.isArray(player.stats)) {
          const arr = player.stats;
          // Theo PlayerDAO.java:
          // data_point = [0:giới hạn SM, 1:SM, 2:tiềm năng, 3:thể lực, 4:thể lực đầy,
          // 5:HP gốc, 6:KI gốc, 7:SĐ gốc, 8:Giáp gốc, 9:Chí mạng gốc, 10:Chí mạng Ước Rồng, 11:Năng động,
          // 12:HP hiện tại, 13:KI hiện tại]
          if (arr.length > 12 && !isNaN(parseInt(arr[12], 10)))
            player.hp = parseInt(arr[12], 10);
          if (arr.length > 13 && !isNaN(parseInt(arr[13], 10)))
            player.mp = parseInt(arr[13], 10);
          if (arr.length > 5 && !isNaN(parseInt(arr[5], 10)))
            player.hpBase = parseInt(arr[5], 10);
          if (arr.length > 6 && !isNaN(parseInt(arr[6], 10)))
            player.mpBase = parseInt(arr[6], 10);
          player.power = !isNaN(parseInt(arr[1], 10))
            ? parseInt(arr[1], 10)
            : player.power;
          player.damageBase = !isNaN(parseInt(arr[7], 10))
            ? parseInt(arr[7], 10)
            : 0;
          player.defenseBase = !isNaN(parseInt(arr[8], 10))
            ? parseInt(arr[8], 10)
            : 0;
          player.critBase = !isNaN(parseInt(arr[9], 10))
            ? parseInt(arr[9], 10)
            : 0;
        }
        if (arr.length > 4 && !isNaN(parseInt(arr[4], 10))) {
          player.mp = parseInt(arr[4], 10);
        }
        // arr[5] and arr[6] may represent base HP and MP
        if (arr.length > 5 && !isNaN(parseInt(arr[5], 10))) {
          player.hpBase = parseInt(arr[5], 10);
        }
        if (arr.length > 6 && !isNaN(parseInt(arr[6], 10))) {
          player.mpBase = parseInt(arr[6], 10);
        }
      }
    } catch (_) {
      player.stats = null;
    }
    // Parse main tasks JSON.  This field is usually a JSON array of objects.
    try {
      if (r.data_task) {
        const arr = JSON.parse(r.data_task);
        if (Array.isArray(arr)) {
          player.tasks = arr;
        }
      }
    } catch (_) {
      player.tasks = [];
    }
    // Parse side tasks JSON.  Typically a JSON array of side task objects.
    try {
      if (r.data_side_task) {
        const arr = JSON.parse(r.data_side_task);
        if (Array.isArray(arr)) {
          player.sideTasks = arr;
        }
      }
    } catch (_) {
      player.sideTasks = [];
    }
    // Parse item time data.  Some servers store timed items or buffs in this field.
    try {
      if (r.data_item_time) {
        const arr = JSON.parse(r.data_item_time);
        if (Array.isArray(arr)) {
          player.itemTimes = arr;
        }
      }
    } catch (_) {
      player.itemTimes = [];
    }
    players.push(player);
  }
  playersData = players;
}

/**
 * Write updated items back to the tab_shop table. When the front‑end
 * modifies items in a tab (add or edit), we convert our friendly
 * representation back to the DB schema (temp_id, cost, type_sell,
 * item_spec, is_new, is_sell, options) and update the JSON in the
 * database. Note: this function expects the caller to pass shopId and
 * tabIndex rather than array indexes.
 *
 * @param {number} shopId  Id of the shop (primary key of `shop` table)
 * @param {number} tabIndex Index of the tab (tab_index column)
 * @param {Array<Object>} items Array of raw items (DB schema) to save
 */
async function updateTabItems(shopId, tabIndex, items) {
  await dbPool.query(
    "UPDATE tab_shop SET items = ? WHERE shop_id = ? AND tab_index = ?",
    [JSON.stringify(items), shopId, tabIndex]
  );
}

/**
 * Serve static assets from the web directory. For icons under /icon/x4,
 * we serve files from the same root web directory so that NPC and item
 * icons can be displayed. If a file does not exist we send 404.
 */
function serveStatic(req, res) {
  let filePath = req.url;
  // Remove leading slash
  if (filePath.startsWith("/")) filePath = filePath.slice(1);
  // Default to index.html
  if (filePath === "") filePath = "index.html";
  const fullPath = path.join(WEB_DIR, filePath);
  // Prevent directory traversal
  if (!fullPath.startsWith(WEB_DIR)) {
    res.statusCode = 403;
    res.end("Forbidden");
    return;
  }
  fs.promises
    .readFile(fullPath)
    .then((data) => {
      const ext = path.extname(fullPath).toLowerCase();
      const mime =
        ext === ".js"
          ? "application/javascript"
          : ext === ".css"
          ? "text/css"
          : ext === ".png"
          ? "image/png"
          : ext === ".json"
          ? "application/json"
          : "text/html";
      res.statusCode = 200;
      res.setHeader("Content-Type", mime);
      res.end(data);
    })
    .catch(() => {
      res.statusCode = 404;
      res.end("Not found");
    });
}

async function handleApi(req, res) {
  const url = req.url;
  const method = req.method;

  if (method === "GET" && url.startsWith("/api/player_detail")) {
    const q = new URL("http://x" + url); // hack to use URL parser
    const id = parseInt(q.searchParams.get("id"), 10);
    if (!id) {
      res.statusCode = 400;
      res.end(JSON.stringify({ ok: false, message: "Thiếu id" }));
      return;
    }
    try {
      const [rows] = await dbPool.query(
        "SELECT id, name, gender, head, data_point, data_location, items_body, items_bag, items_box, pet, data_task, data_side_task, data_item_time FROM player WHERE id = ?",
        [id]
      );
      if (!rows.length) {
        res.statusCode = 404;
        res.end(
          JSON.stringify({ ok: false, message: "Không tìm thấy người chơi" })
        );
        return;
      }
      const r = rows[0];
      const detail = { id: r.id, name: r.name, gender: r.gender, head: r.head };
      // Avatar by gender
      if (r.gender === 2) detail.avatarIcon = "/images/avatar_xayda.png";
      else if (r.gender === 1) detail.avatarIcon = "/images/avatar_namek.png";
      else detail.avatarIcon = "/images/avatar_earth.png";
      // Stats decode
      try {
        const arr = JSON.parse(r.data_point || "[]");
        if (Array.isArray(arr)) {
          detail.stats = {
            limitPower: parseInt(arr[0] || 0, 10),
            power: parseInt(arr[1] || 0, 10),
            potential: parseInt(arr[2] || 0, 10),
            stamina: parseInt(arr[3] || 0, 10),
            staminaMax: parseInt(arr[4] || 0, 10),
            hpBase: parseInt(arr[5] || 0, 10),
            kiBase: parseInt(arr[6] || 0, 10),
            damageBase: parseInt(arr[7] || 0, 10),
            defenseBase: parseInt(arr[8] || 0, 10),
            critBase: parseInt(arr[9] || 0, 10),
            wishCrit: parseInt(arr[10] || 0, 10),
            activity: parseInt(arr[11] || 0, 10),
            hp: parseInt(arr[12] || 0, 10),
            mp: parseInt(arr[13] || 0, 10),
          };
        }
      } catch (_) {}
      // Location
      try {
        const loc = JSON.parse(r.data_location || "[]");
        detail.mapId = parseInt(loc[0] || 0, 10);
        detail.x = parseInt(loc[1] || 0, 10);
        detail.y = parseInt(loc[2] || 0, 10);
        const m = mapsData.find((m) => m.id === detail.mapId);
        detail.mapName = m ? m.name : String(detail.mapId);
      } catch (_) {}
      // Items
      detail.itemsBody = parseItemList(r.items_body);
      detail.itemsBag = parseItemList(r.items_bag);
      detail.itemsBox = parseItemList(r.items_box);
      // Pet
      detail.pet = { info: null, point: null, body: [], skills: [] };
      try {
        const pet = JSON.parse(r.pet || "[]");
        let petInfo, petPoint, petBody, petSkill;
        if (Array.isArray(pet)) {
          [petInfo, petPoint, petBody, petSkill] = pet.map((x) =>
            typeof x === "string" ? JSON.parse(x) : x
          );
        } else if (pet && typeof pet === "object") {
          petInfo =
            typeof pet.info === "string" ? JSON.parse(pet.info) : pet.info;
          petPoint =
            typeof pet.point === "string" ? JSON.parse(pet.point) : pet.point;
          petBody =
            typeof pet.body === "string" ? JSON.parse(pet.body) : pet.body;
          petSkill =
            typeof pet.skill === "string" ? JSON.parse(pet.skill) : pet.skill;
        }
        detail.pet.info = petInfo;
        detail.pet.point = petPoint;
        // petBody from server is array of JSON strings; parse each -> our item objects
        if (Array.isArray(petBody)) {
          const items = [];
          for (const s of petBody) {
            try {
              const it = typeof s === "string" ? JSON.parse(s) : s;
              // it = [templateId, quantity, ??, optionsStr, ?, ?, createTime]
              const tempId = parseInt(it[0], 10);
              const quantity = parseInt(it[1] || 1, 10);
              const options = parseOptions(it[2] || it.options || []);
              const tpl = itemTemplateMap[tempId];
              items.push({
                tempId,
                quantity,
                options,
                name: tpl ? tpl.name : `#${tempId}`,
                icon:
                  tpl && tpl.iconID >= 0
                    ? `/icon/x4/${tpl.iconID}.png`
                    : "/web/placeholder_item.png",
              });
            } catch {}
          }
          detail.pet.body = items;
        }
        detail.pet.skills = Array.isArray(petSkill) ? petSkill : [];
      } catch (_) {}
      // Tasks
      try {
        detail.tasks = JSON.parse(r.data_task || "[]");
      } catch {
        detail.tasks = [];
      }
      try {
        detail.sideTasks = JSON.parse(r.data_side_task || "[]");
      } catch {
        detail.sideTasks = [];
      }
      try {
        detail.itemTimes = JSON.parse(r.data_item_time || "[]");
      } catch {
        detail.itemTimes = [];
      }
      res.setHeader("Content-Type", "application/json");
      res.end(JSON.stringify({ ok: true, detail, maps: mapsData }));
    } catch (e) {
      res.statusCode = 500;
      res.end(JSON.stringify({ ok: false, message: e.message }));
    }
    return;
  }

  // GET /api/shops
  if (method === "GET" && url === "/api/shops") {
    res.setHeader("Content-Type", "application/json");
    // Do not send rawItems to client
    const cleanShops = shopsData.map((shop) => {
      // Remove undefined/null tabs to prevent client errors and include original identifiers
      const definedTabs = shop.tabs.filter((tab) => !!tab);
      return {
        shopId: shop.shopId,
        npcId: shop.npcId,
        npcName: shop.npcName,
        npcIcon: shop.npcIcon,
        tabs: definedTabs.map((tab) => ({
          name: tab.name,
          items: tab.items,
          // include shopId and tabIndex for client to know DB record
          shopId: tab.shopId,
          tabIndex: tab.tabIndex,
        })),
      };
    });
    res.end(JSON.stringify(cleanShops));
    return;
  }
  // GET /api/maps
  if (method === "GET" && url === "/api/maps") {
    res.setHeader("Content-Type", "application/json");
    res.end(JSON.stringify(mapsData));
    return;
  }
  // POST /api/players/teleport { playerId, mapId, x?, y? }
  if (method === "POST" && url === "/api/players/teleport") {
    let body = "";
    req.on("data", (chunk) => (body += chunk));
    req.on("end", async () => {
      try {
        const payload = JSON.parse(body || "{}");
        const playerId = parseInt(payload.playerId, 10);
        const mapId = parseInt(payload.mapId, 10);
        const x = payload.x !== undefined ? parseInt(payload.x, 10) : 100;
        const y = payload.y !== undefined ? parseInt(payload.y, 10) : 384;
        if (!playerId || isNaN(mapId)) {
          res.statusCode = 400;
          res.end(
            JSON.stringify({
              ok: false,
              message: "Thiếu playerId hoặc mapId",
            })
          );
          return;
        }
        await dbPool.query(
          "UPDATE player SET data_location = JSON_ARRAY(?, ?, ?) WHERE id = ?",
          [mapId, x, y, playerId]
        );
        // Update cache
        const p = playersData.find((p) => p.id === playerId);
        if (p) {
          p.mapId = mapId;
          p.x = x;
          p.y = y;
          const m = mapsData.find((m) => m.id === mapId);
          p.mapName = m ? m.name : String(mapId);
        }
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ ok: true }));
      } catch (e) {
        res.statusCode = 500;
        res.end(JSON.stringify({ ok: false, message: e.message }));
      }
    });
    return;
  }

  // GET /api/play// GET /api/players
  if (method === "GET" && url === "/api/players") {
    res.setHeader("Content-Type", "application/json");
    res.end(JSON.stringify(playersData));
    return;
  }
  // GET /api/item_templates
  if (method === "GET" && url === "/api/item_templates") {
    res.setHeader("Content-Type", "application/json");
    res.end(JSON.stringify(itemTemplates));
    return;
  }
  // GET /api/item_option_templates
  if (method === "GET" && url === "/api/item_option_templates") {
    res.setHeader("Content-Type", "application/json");
    res.end(JSON.stringify(itemOptionTemplates));
    return;
  }

  // GET /api/giftcodes
  if (method === "GET" && url === "/api/giftcodes") {
    // List all giftcodes with parsed items. The giftcode table is expected to
    // have columns code, count_left, detail, datecreate, expired and type.
    dbPool
      .query(
        "SELECT code, count_left, detail, datecreate, expired, `type` FROM giftcode"
      )
      .then(([rows]) => {
        const list = [];
        for (const r of rows) {
          let parsedItems = [];
          if (r.detail) {
            try {
              const arr = JSON.parse(r.detail);
              if (Array.isArray(arr)) {
                parsedItems = arr.map((it) => {
                  const tempId = parseInt(it.temp_id ?? it.tempId, 10);
                  const quantity = parseInt(it.quantity ?? 1, 10) || 1;
                  const tpl = itemTemplateMap[tempId] || {
                    name: `Item ${tempId}`,
                    iconID: -1,
                  };
                  // parse raw options
                  const opts = parseOptions(it.options);
                  // map options to descriptions
                  const detailedOpts = opts.map((o) => {
                    const descTpl = optionTemplateMap[o.id];
                    let description;
                    if (descTpl) {
                      if (descTpl.includes("#")) {
                        // replace all '#' with param
                        description = descTpl.replace(/#/g, o.param);
                      } else {
                        description = descTpl + " " + o.param;
                      }
                    } else {
                      description = `Option ${o.id}: +${o.param}`;
                    }
                    return { id: o.id, param: o.param, description };
                  });
                  const iconPath =
                    tpl.iconID >= 0
                      ? `/icon/x4/${tpl.iconID}.png`
                      : "placeholder_item.png";
                  return {
                    name: tpl.name,
                    description: tpl.description || "",
                    temp_id: tempId,
                    quantity,
                    icon: iconPath,
                    options: detailedOpts,
                  };
                });
              }
            } catch (e) {
              // ignore parse errors
            }
          }
          list.push({
            code: r.code,
            countLeft: r.count_left,
            type: r.type,
            datecreate: r.datecreate,
            expired: r.expired,
            items: parsedItems,
          });
        }
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify(list));
      })
      .catch((err) => {
        console.error(err);
        res.statusCode = 500;
        res.end(JSON.stringify({ error: "Failed to load giftcodes" }));
      });
    return;
  }
  // POST /api/giftcodes
  if (method === "POST" && url === "/api/giftcodes") {
    // Create a giftcode entry. Expected payload: {
    //   code: string,
    //   items: array,
    //   countLeft: number,
    //   type: number (0 or 1),
    //   expired: string (date in YYYY-MM-DD format or ISO string)
    // }
    let body = "";
    req.on("data", (chunk) => {
      body += chunk;
      if (body.length > 1e6) req.connection.destroy();
    });
    req.on("end", async () => {
      try {
        const data = JSON.parse(body);
        // Validate mandatory fields
        if (
          !data.code ||
          !Array.isArray(data.items) ||
          data.items.length === 0
        ) {
          res.statusCode = 400;
          res.end(JSON.stringify({ error: "Invalid giftcode data" }));
          return;
        }
        const countLeft = parseInt(data.countLeft, 10);
        const typeVal = parseInt(data.type, 10);
        const expired = data.expired ? new Date(data.expired) : null;
        const now = new Date();
        // Transform items into DB format: temp_id and quantity keys
        const itemsForDb = Array.isArray(data.items)
          ? data.items.map((it) => {
              const tempId = parseInt(it.temp_id ?? it.tempId, 10);
              const qty = parseInt(it.quantity ?? 1, 10);
              const opts = Array.isArray(it.options)
                ? it.options.map((o) => ({ id: o.id, param: o.param }))
                : [];
              return {
                temp_id: isNaN(tempId) ? null : tempId,
                quantity: isNaN(qty) ? 1 : qty,
                options: opts,
              };
            })
          : [];
        await dbPool.query(
          "INSERT INTO giftcode (code, count_left, detail, datecreate, expired, `type`) VALUES (?, ?, ?, ?, ?, ?)",
          [
            data.code,
            isNaN(countLeft) ? 1 : countLeft,
            JSON.stringify(itemsForDb),
            now,
            expired,
            isNaN(typeVal) ? 0 : typeVal,
          ]
        );
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ success: true }));
      } catch (e) {
        console.error(e);
        res.statusCode = 500;
        res.end(JSON.stringify({ error: "Failed to save giftcode" }));
      }
    });
    return;
  }

  // PUT /api/giftcodes/{code} - update an existing giftcode
  // Payload: {
  //   countLeft?: number,
  //   type?: number,
  //   expired?: string (YYYY-MM-DD or ISO),
  //   items?: [ { temp_id: number, quantity: number, options: [ { id: number, param: number } ] } ]
  // }
  const updateGcMatch = url.match(/^\/api\/giftcodes\/(.+)$/);
  if (method === "PUT" && updateGcMatch) {
    const codeStr = decodeURIComponent(updateGcMatch[1]);
    let body = "";
    req.on("data", (chunk) => {
      body += chunk;
      if (body.length > 1e6) req.connection.destroy();
    });
    req.on("end", async () => {
      try {
        const payload = JSON.parse(body || "{}");
        // Fetch existing giftcode by code
        const [rows] = await dbPool.query(
          "SELECT id FROM giftcode WHERE code = ?",
          [codeStr]
        );
        if (!rows || rows.length === 0) {
          res.statusCode = 404;
          res.end(JSON.stringify({ error: "Giftcode not found" }));
          return;
        }
        const countLeft =
          payload.countLeft !== undefined
            ? parseInt(payload.countLeft, 10)
            : undefined;
        const typeVal =
          payload.type !== undefined ? parseInt(payload.type, 10) : undefined;
        let expiredDate = null;
        if (payload.expired) {
          // Accept YYYY-MM-DD or ISO date string
          const d = new Date(payload.expired);
          if (!isNaN(d.getTime())) expiredDate = d;
        }
        // Build items array if provided
        let detailJson = undefined;
        if (Array.isArray(payload.items)) {
          const arr = payload.items.map((it) => {
            const tempId = parseInt(it.temp_id ?? it.tempId, 10);
            const qty = parseInt(it.quantity ?? 1, 10);
            const opts = Array.isArray(it.options)
              ? it.options.map((o) => ({ id: o.id, param: o.param }))
              : [];
            return {
              temp_id: isNaN(tempId) ? null : tempId,
              quantity: isNaN(qty) ? 1 : qty,
              options: opts,
            };
          });
          detailJson = JSON.stringify(arr);
        }
        // Build update clauses and params
        const fields = [];
        const params = [];
        if (countLeft !== undefined && !isNaN(countLeft)) {
          fields.push("count_left = ?");
          params.push(countLeft);
        }
        if (typeVal !== undefined && !isNaN(typeVal)) {
          fields.push("`type` = ?");
          params.push(typeVal);
        }
        if (expiredDate instanceof Date) {
          fields.push("expired = ?");
          params.push(expiredDate);
        }
        if (detailJson !== undefined) {
          fields.push("detail = ?");
          params.push(detailJson);
        }
        if (fields.length === 0) {
          // nothing to update
          res.setHeader("Content-Type", "application/json");
          res.end(JSON.stringify({ success: true }));
          return;
        }
        params.push(codeStr);
        const sql = `UPDATE giftcode SET ${fields.join(", ")} WHERE code = ?`;
        await dbPool.query(sql, params);
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ success: true }));
      } catch (e) {
        console.error(e);
        res.statusCode = 500;
        res.end(JSON.stringify({ error: "Failed to update giftcode" }));
      }
    });
    return;
  }
  // Add new item: POST /api/shops/{shopId}/tabs/{tabIndex}/items
  const addItemMatch = url.match(/^\/api\/shops\/(\d+)\/tabs\/(\d+)\/items$/);
  if (method === "POST" && addItemMatch) {
    const shopId = parseInt(addItemMatch[1], 10);
    const tabIndex = parseInt(addItemMatch[2], 10);
    const shop = shopsData.find((s) => s.shopId === shopId);
    if (!shop || !shop.tabs[tabIndex]) {
      res.statusCode = 400;
      res.end(JSON.stringify({ error: "Invalid shop or tab index" }));
      return;
    }
    let body = "";
    req.on("data", (chunk) => {
      body += chunk;
      if (body.length > 1e6) req.connection.destroy();
    });
    req.on("end", async () => {
      try {
        const newItem = JSON.parse(body);
        // Validate payload: tempId, cost, currency
        if (!newItem || typeof newItem.tempId !== "number") {
          res.statusCode = 400;
          res.end(JSON.stringify({ error: "Invalid item data" }));
          return;
        }
        // Determine type_sell from currency string
        const currencyMap = { vàng: 0, ngọc: 1, ruby: 3, coupon: 4 };
        const typeSell = currencyMap[newItem.currency] ?? 0;
        const rawItem = {
          temp_id: newItem.tempId,
          cost: newItem.cost || 0,
          item_spec: 0,
          type_sell: typeSell,
          is_new: false,
          is_sell: true,
          options: (newItem.options || []).map((o) => ({
            id: o.id,
            param: o.param,
          })),
        };
        // Append to cached structures
        shop.tabs[tabIndex].items.push({
          name:
            itemTemplateMap[newItem.tempId]?.name || `Item ${newItem.tempId}`,
          tempId: newItem.tempId,
          cost: newItem.cost || 0,
          currency: newItem.currency || "vàng",
          icon:
            itemTemplateMap[newItem.tempId]?.iconID >= 0
              ? `/icon/x4/${itemTemplateMap[newItem.tempId].iconID}.png`
              : "placeholder_item.png",
          options: newItem.options || [],
        });
        shop.tabs[tabIndex].rawItems.push(rawItem);
        // Persist to DB
        await updateTabItems(shopId, tabIndex, shop.tabs[tabIndex].rawItems);
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ success: true }));
      } catch (e) {
        console.error(e);
        res.statusCode = 500;
        res.end(JSON.stringify({ error: "Failed to add item" }));
      }
    });
    return;
  }
  // Update existing item: PUT /api/shops/{shopId}/tabs/{tabIndex}/items/{itemIndex}
  const updateMatch = url.match(
    /^\/api\/shops\/(\d+)\/tabs\/(\d+)\/items\/(\d+)$/
  );
  if (updateMatch && (method === "PUT" || method === "POST")) {
    const shopId = parseInt(updateMatch[1], 10);
    const tabIndex = parseInt(updateMatch[2], 10);
    const itemIndex = parseInt(updateMatch[3], 10);
    const shop = shopsData.find((s) => s.shopId === shopId);
    if (
      !shop ||
      !shop.tabs[tabIndex] ||
      !shop.tabs[tabIndex].items[itemIndex]
    ) {
      res.statusCode = 400;
      res.end(JSON.stringify({ error: "Invalid shop, tab or item index" }));
      return;
    }
    let body = "";
    req.on("data", (chunk) => {
      body += chunk;
      if (body.length > 1e6) req.connection.destroy();
    });
    req.on("end", async () => {
      try {
        const updates = JSON.parse(body);
        const item = shop.tabs[tabIndex].items[itemIndex];
        const rawItem = shop.tabs[tabIndex].rawItems[itemIndex];
        // Update cost
        if (updates.cost !== undefined) {
          const c = parseInt(updates.cost, 10);
          if (!isNaN(c)) {
            item.cost = c;
            rawItem.cost = c;
          }
        }
        // Update currency/type_sell
        if (updates.currency) {
          item.currency = updates.currency;
          const currencyMap = { vàng: 0, ngọc: 1, ruby: 3, coupon: 4 };
          rawItem.type_sell = currencyMap[updates.currency] ?? 0;
        }
        // Update options
        if (Array.isArray(updates.options)) {
          item.options = updates.options.map((o) => ({
            id: o.id,
            param: o.param,
          }));
          rawItem.options = updates.options.map((o) => ({
            id: o.id,
            param: o.param,
          }));
        }
        // Update isNew flag
        if (updates.isNew !== undefined) {
          const flag = !!updates.isNew;
          item.isNew = flag;
          rawItem.is_new = flag;
        }
        // Update isSell flag
        if (updates.isSell !== undefined) {
          const flag = !!updates.isSell;
          item.isSell = flag;
          rawItem.is_sell = flag;
        }
        // Update iconSpec
        if (updates.iconSpec !== undefined) {
          const spec = parseInt(updates.iconSpec, 10);
          if (!isNaN(spec)) {
            item.iconSpec = spec;
            rawItem.item_spec = spec;
          }
        }
        // Persist changes
        await updateTabItems(shopId, tabIndex, shop.tabs[tabIndex].rawItems);
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ success: true }));
      } catch (e) {
        console.error(e);
        res.statusCode = 500;
        res.end(JSON.stringify({ error: "Failed to update item" }));
      }
    });
    return;
  }

  // PUT /api/shops/{shopId}
  // This endpoint allows updating a shop's NPC assignment and tab names.
  // Payload: { npcId?: number, tabs?: [{ index: number, name: string }] }
  const updateShopMatch = url.match(/^\/api\/shops\/(\d+)$/);
  if (method === "PUT" && updateShopMatch) {
    const shopIdNum = parseInt(updateShopMatch[1], 10);
    const shopObj = shopsData.find((s) => s.shopId === shopIdNum);
    if (!shopObj) {
      res.statusCode = 400;
      res.end(JSON.stringify({ error: "Invalid shop id" }));
      return;
    }
    let bodyStr = "";
    req.on("data", (chunk) => {
      bodyStr += chunk;
      if (bodyStr.length > 1e6) req.connection.destroy();
    });
    req.on("end", async () => {
      try {
        const json = bodyStr ? JSON.parse(bodyStr) : {};
        // Update NPC
        if (
          json.npcId !== undefined &&
          !isNaN(parseInt(json.npcId, 10)) &&
          parseInt(json.npcId, 10) !== shopObj.npcId
        ) {
          const newNpcId = parseInt(json.npcId, 10);
          await dbPool.query("UPDATE shop SET npc_id = ? WHERE id = ?", [
            newNpcId,
            shopIdNum,
          ]);
          shopObj.npcId = newNpcId;
          const npcInfo = npcTemplateMap[newNpcId] || {
            name: `NPC ${newNpcId}`,
            iconID: -1,
          };
          shopObj.npcName = npcInfo.name;
          shopObj.npcIcon =
            npcInfo.iconID >= 0
              ? `/icon/x4/${npcInfo.iconID}.png`
              : "/icon/x4/3013.png";
        }
        // Update tab names
        if (Array.isArray(json.tabs)) {
          for (const tabData of json.tabs) {
            const idx = parseInt(tabData.index, 10);
            const name = typeof tabData.name === "string" ? tabData.name : null;
            if (!isNaN(idx) && name) {
              const tabObj = shopObj.tabs[idx];
              if (tabObj) {
                const clean = name.replace(/[<>]/g, " ");
                await dbPool.query(
                  "UPDATE tab_shop SET tab_name = ? WHERE shop_id = ? AND tab_index = ?",
                  [clean, shopIdNum, idx]
                );
                tabObj.name = clean;
              }
            }
          }
        }
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ success: true }));
      } catch (e) {
        console.error(e);
        res.statusCode = 500;
        res.end(JSON.stringify({ error: "Failed to update shop" }));
      }
    });
    return;
  }

  // POST /api/send-mail - send items to a player's mailbox
  // Payload: { playerId?: number, playerName?: string, items: [ { temp_id: number, quantity: number, options: [ { id: number, param: number } ] } ] }
  if (method === "POST" && url === "/api/send-mail") {
    let bodyStr = "";
    req.on("data", (chunk) => {
      bodyStr += chunk;
      if (bodyStr.length > 1e6) req.connection.destroy();
    });
    req.on("end", async () => {
      try {
        const payload = JSON.parse(bodyStr || "{}");
        const playerId =
          payload.playerId !== undefined
            ? parseInt(payload.playerId, 10)
            : undefined;
        const playerName =
          typeof payload.playerName === "string"
            ? payload.playerName.trim()
            : undefined;
        const items = Array.isArray(payload.items) ? payload.items : [];
        if (
          (!playerId || isNaN(playerId)) &&
          (!playerName || playerName.length === 0)
        ) {
          res.statusCode = 400;
          res.end(
            JSON.stringify({ error: "playerId hoặc playerName là bắt buộc" })
          );
          return;
        }
        if (!items || items.length === 0) {
          res.statusCode = 400;
          res.end(JSON.stringify({ error: "Danh sách vật phẩm trống" }));
          return;
        }
        // Look up player
        let query, params;
        if (playerId && !isNaN(playerId)) {
          query = "SELECT id, item_mails_box FROM player WHERE id = ? LIMIT 1";
          params = [playerId];
        } else {
          query =
            "SELECT id, item_mails_box FROM player WHERE name = ? LIMIT 1";
          params = [playerName];
        }
        const [rows] = await dbPool.query(query, params);
        if (!rows || rows.length === 0) {
          res.statusCode = 404;
          res.end(JSON.stringify({ error: "Người chơi không tồn tại" }));
          return;
        }
        const playerRow = rows[0];
        // Parse existing mailbox items
        let mailArr;
        try {
          mailArr = playerRow.item_mails_box
            ? JSON.parse(playerRow.item_mails_box)
            : [];
        } catch (_) {
          mailArr = [];
        }
        if (!Array.isArray(mailArr)) mailArr = [];
        // Append new items
        items.forEach((it) => {
          const tempId = parseInt(it.temp_id ?? it.tempId, 10);
          if (isNaN(tempId)) return;
          const qty = parseInt(it.quantity ?? 1, 10);
          const opts = [];
          if (Array.isArray(it.options)) {
            it.options.forEach((op) => {
              const oid = parseInt(op.id, 10);
              const p = parseInt(op.param, 10);
              if (!isNaN(oid) && !isNaN(p)) opts.push([oid, p]);
            });
          }
          // options JSON string
          const optStr = JSON.stringify(opts);
          const dataItem = [tempId, isNaN(qty) ? 1 : qty, optStr, Date.now()];
          mailArr.push(JSON.stringify(dataItem));
        });
        const newMailJson = JSON.stringify(mailArr);
        // Update player mailbox
        await dbPool.query(
          "UPDATE player SET item_mails_box = ? WHERE id = ?",
          [newMailJson, playerRow.id]
        );
        res.setHeader("Content-Type", "application/json");
        res.end(JSON.stringify({ success: true }));
      } catch (e) {
        console.error(e);
        res.statusCode = 500;
        res.end(JSON.stringify({ error: "Failed to send mail" }));
      }
    });
    return;
  }

  // No matching API route
  res.statusCode = 404;
  res.end(JSON.stringify({ error: "Not found" }));
}

/**
 * Create and start the HTTP server. Static files are served from the web
 * directory. API routes are prefixed with /api. On startup we preload
 * all data from the database.
 */
async function start() {
  await initDbPool();
  await loadNpcTemplates();
  await loadItemTemplates();
  await loadItemOptionTemplates();
  // await loadMapsFromDB();
  await loadMaps(); 
  await loadShops();
  await loadPlayers();
  const server = http.createServer((req, res) => {
    if (req.url.startsWith("/api/")) {
      handleApi(req, res);
    } else {
      serveStatic(req, res);
    }
  });
  const port = 3000;
  server.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
  });
}

start().catch((err) => {
  console.error("Failed to start server:", err);
});
