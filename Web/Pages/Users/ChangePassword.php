<?php
include '../../Controllers/Header.php';
?>
<div class="container pb-5">
    <div class="col-md-12">
        <form class="form-horizontal" onsubmit="event.preventDefault(); ChangePass();">
            <div class="card">
                <div class="card-body">
                    <h3 class="mt-0 mb-20" style="text-align: center">Đổi mật khẩu</h3>
                    <div class="mb-3">
                        <label class="font-weight-bold">Mật khẩu cũ</label>
                        <input class="form-control" type="password" name="current_password"
                            placeholder="Mật khẩu hiện tại" minlength="3" required>
                    </div>
                    <div class="mb-3">
                        <label class="font-weight-bold">Mật khẩu mới</label>
                        <input class="form-control" type="password" name="newpassword"
                            placeholder="Mật khẩu mới bạn muốn" minlength="3" required>
                    </div>
                    <div class="mb-3">
                        <label class="font-weight-bold">Xác nhận</label>
                        <input class="form-control" type="password" name="newpassword_confirm"
                            placeholder="Xác nhận mật khẩu mới bạn muốn" minlength="3" required>
                    </div>

                    <div style="text-align: center">
                        <button type="submit" style="display: block; margin: 0 auto; border: 2px solid #8BC34A; background-color: #C5E1A5; color: #FFFFFF; padding: 5px 20px; font-size: 16px; border-radius: 5px; cursor: pointer;">ĐỔI MẬT KHẨU</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
    <script>
        function ChangePass() {
            var current_password = document.querySelector("[name=current_password]").value;
            var newpassword = document.querySelector("[name=newpassword]").value;
            var newpassword_confirm = document.querySelector("[name=newpassword_confirm]").value;
            var username = '<?= $_Username ?>';

            var isValid = passwordCheck(current_password, newpassword, newpassword_confirm);
            if (isValid) {
                fetch('/Api/Password', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        current_password: current_password, // Thêm trường này để gửi mật khẩu hiện tại
                        newpassword: newpassword,
                        newpassword_confirm: newpassword_confirm,
                        username: username,
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
        }

        function passwordCheck(current_password, newpassword, newpassword_confirm) {
            var hasSpecialChar = /[ `!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/;
            var hasUpperCase = /[A-Z]/;

            if (hasSpecialChar.test(newpassword) || hasUpperCase.test(newpassword)) {
                showCustomToast('Mật khẩu không được chứa ký tự đặc biệt hoặc chữ hoa');
                return false;
            } else if (newpassword !== newpassword_confirm) {
                showCustomToast('Mật khẩu mới không khớp');
                return false;
            }
            return true;
        }
    </script>
</div>
<?php
include '../../Controllers/Footer.php';
?>