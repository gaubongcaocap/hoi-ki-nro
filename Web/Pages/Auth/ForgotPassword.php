<?php
include '../../Controllers/Header.php';
?>
<div class="container pb-5">
    <div class="col-md-12">
        <form id="form" method="POST" class="form-horizontal">
            <div class="card">
                <div class="card-body">
                    <h3 class="mt-0 mb-20" style="text-align: center">Lấy lại mật khẩu</h3>
                    <div id="thongbao"></div>
                    <div class="form-group mb-3">
                        <label class="font-weight-bold">Email:</label>
                        <input class="form-control" type="email" name="email" placeholder="Nhập email của bạn" required>
                    </div>
                    <div class="form-group" style="text-align: center">
                        <button class="btn btn-info btn-block text-white" type="button" onclick="sendEmail()">LẤY MẬT
                            KHẨU</button>
                    </div>
                </div>
            </div>
        </form>
        <script>
            function isValidEmail(email) {
                var regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                return regex.test(email);
            }

            function sendEmail() {
                var email = document.querySelector("[name=email]").value;
                if (!email || !email.trim()) {
                    showCustomToast('Vui lòng nhập địa chỉ email.', 'error');
                    return;
                }
                if (!isValidEmail(email)) {
                    showCustomToast('Địa chỉ email không hợp lệ.', 'error');
                    return;
                }

                fetch('/Api/Email', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        email: email
                    })
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            showCustomToast(data.message, 'success');
                            setTimeout(function () {
                                location.reload();
                            }, 2000);
                        } else {
                            showCustomToast(data.message, 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showCustomToast('Có lỗi xảy ra. Vui lòng thử lại sau.', 'error');
                    });
            }

            function showCustomToast(message, type) {
                var toast = document.getElementById('customToast');
                toast.innerText = message;
                toast.style.display = 'block';
                toast.classList.remove('alert', 'alert-success', 'alert-danger'); // Xóa các lớp hiện có trước khi thêm lớp mới
                if (type === 'success') {
                    toast.classList.add('alert', 'alert-success');
                } else {
                    toast.classList.add('alert', 'alert-danger');
                }

                // Tự đóng thông báo sau 3 giây
                setTimeout(function () {
                    toast.style.display = 'none';
                }, 3000);
            }
        </script>
    </div>
</div>
<style>
    .form-horizontal {
        width: 100%;
        max-width: 400px;
        padding: 15px 0;
        margin: 0 auto;
    }
</style>
<?php include '../../Controllers/Footer.php'; ?>