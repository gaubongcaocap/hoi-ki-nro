<?php
include_once '../../Controllers/Header.php';
?>

<div class="card">
    <div class="card-body">
        <div class="text-center fw-semibold fs-5">Đổi Thỏi Vàng(<span class="text-danger">Khuyến Mãi
                X<?= $_danap ?></span>)</div>
        <div class="d-flex justify-content-center">
            <div class="col-md-8">
                <div id="selectedAmountMessage" class="mt-3 text-center"></div>
                <div id="rechargeMethods"
                    class="row text-center justify-content-center row-cols-2 row-cols-lg-3 g-2 g-lg-2 my-1 mb-2"></div>
                <div class="text-center mt-4">
                    <button id="Quydoi" type="button" class="w-50 rounded-3 btn btn-primary btn-sm">Xác nhận</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        var rechargeData = [
            { amount: 10000, gold: 50 },
            { amount: 20000, gold: 100 },
            { amount: 30000, gold: 150 },
            { amount: 50000, gold: 250 },
            { amount: 100000, gold: 500 },
            { amount: 200000, gold: 1000 },
            { amount: 500000, gold: 2500 },
            { amount: 1000000, gold: 5000 },
            { amount: 2000000, gold: 10000 }
        ];

        var rechargeMethodsContainer = document.getElementById("rechargeMethods");
        var selectedItem = null;

        rechargeData.forEach(function (item) {
            var rechargeItem = document.createElement("div");
            rechargeItem.classList.add("col");
            rechargeItem.innerHTML = `
            <div class="w-100 fw-semibold cursor-pointer recharge-method-item" style="height: 90px;">
                <div class="text-primary">${formatAmount(item.amount)} VNĐ</div>
                <div class="center-text text-dark"><span>Nhận</span></div>
                <div class="text-danger">${formatAmount(item.gold)} Thỏi</div>
            </div>`;
            rechargeItem.addEventListener("click", function () {
                selectedItem = item;
                document.getElementById("selectedAmountMessage").innerHTML = "<b>Bạn đang chọn mốc: " + formatAmount(item.amount) + " VNĐ</b> | <b>Nhận được: " + formatAmount(item.gold) + " Thỏi";
            });
            rechargeMethodsContainer.appendChild(rechargeItem);
        });

        function formatAmount(amount) {
            return amount.toLocaleString('en-US');
        }

        document.getElementById("Quydoi").addEventListener("click", function () {
            var username = '<?= $_Username; ?>'; // Sử dụng cú pháp PHP này để chèn giá trị của biến $_Username vào trong mã JavaScript

            if (selectedItem && username) {

                fetch('/Api/Gold', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        vnd_amount: selectedItem.amount,
                        gold_amount: selectedItem.gold,
                        username: username
                    })
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            showCustomToast(data.message, 'success');
                        } else {
                            showCustomToast(data.message, 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showCustomToast('Có lỗi xảy ra. Vui lòng thử lại sau.', 'error');
                    });
            }
        });
    });
</script>

<?php
include_once '../../Controllers/Footer.php';
?>