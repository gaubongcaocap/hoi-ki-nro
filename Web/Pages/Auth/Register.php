<?php
session_start();
include '../../Controllers/Header.php';

$Username = ''; $Password = ''; $RePassword = ''; $IP = $_SERVER['REMOTE_ADDR'];
$idRef = isset($_GET['idRef']) ? intval($_GET['idRef']) : 0;

// Tạo mã captcha nếu chưa có trong session
if (!isset($_SESSION['captcha_code'])) {
    $_SESSION['captcha_code'] = substr(str_shuffle("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"), 0, 6);
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $userCaptcha = isset($_POST['captcha']) ? trim($_POST['captcha']) : '';

    if (!isset($_SESSION['captcha_code']) || $userCaptcha !== $_SESSION['captcha_code']) {
        $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Mã xác minh không đúng. Vui lòng thử lại.</div>';
        $_SESSION['captcha_code'] = substr(str_shuffle("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"), 0, 6);
    } else {
        $Username = htmlspecialchars(trim($_POST["username"]));
        $Password = htmlspecialchars(trim($_POST["password"]));
        $RePassword = htmlspecialchars(trim($_POST["RePasswordword"]));

        if ($_AuthLog == 1) {
            $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Đang bảo trì đăng nhập, vui lòng thử lại sau!</div>';
            return;
        }
        if (!isValidInput($Username) || !isValidInput($Password)) {
            $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Tên đăng nhập và mật khẩu không được chứa kí tự đặc biệt.</div>';
        } elseif ($Password !== $RePassword) {
            $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Mật khẩu nhập lại không khớp!</div>';
        } else {
            if (checkExistingUsername($conn, $Username)) {
                $_ThongBao = "<div class='text-danger pb-2 font-weight-bold'>Tài khoản đã tồn tại.</div>";
            } else {
                if (insertAccount($conn, $Username, $Password, $IP)) {
                    $_ThongBao = '<div class="text-success pb-2 font-weight-bold">Đăng kí thành công!!</div>';

                    if ($idRef > 0) {
                        $existingReferrerID = hasIPBeenUsedForReferral($conn, $IP);
                        if ($existingReferrerID === false || $existingReferrerID == $idRef) {
                            if (!checkReferralAlreadyAwarded($conn, $IP, $idRef)) {
                                $refIP = getReferrerIP($conn, $idRef);
                                if ($refIP !== $IP) {
                                    updateReferralPoints($conn, $idRef);
                                    logReferral($conn, $IP, $idRef);
                                }
                            }
                        }
                    }
                    unset($_SESSION['captcha_code']);
                    $_SESSION['captcha_code'] = substr(str_shuffle("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"), 0, 6);
                } else {
                    $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Đăng ký thất bại.</div>';
                }
            }
        }
    }
}
?>

<div class="container pb-5">
    <form class="form-signin" method="post">
        <div class="card">
            <div class="card-body">
                <h1 class="h3 font-weight-normal text-center">Đăng ký</h1>
                <?php if (!empty($_ThongBao)) { echo $_ThongBao; } ?>
                <div class="form-group mb-2">
                    <label>Tài khoản</label>
                    <input type="text" class="form-control" placeholder="Tài khoản" required="" name="username" value="" minlength="3">
                </div>
                <div class="form-group mb-2">
                    <label>Mật khẩu</label>
                    <input type="password" class="form-control" placeholder="Mật khẩu" required="" name="password" minlength="3">
                </div>
                <div class="form-group mb-2">
                    <label>Xác nhận mật khẩu</label>
                    <input type="password" class="form-control" placeholder="Xác nhận mật khẩu" required="" name="RePasswordword" minlength="3">
                </div>
                <div class="form-group mb-2">
                    <label>Xác minh:</label>
                    <input type="text" class="form-control d-inline-block w-50" placeholder="Nhập captcha ..." required="" name="captcha">
                    <span class="captcha-text"><?php echo isset($_SESSION['captcha_code']) ? $_SESSION['captcha_code'] : ''; ?></span>
                </div>
                <div class="form-group text-center">
                    <button id="Register" class="btn btn-info btn-block text-white" type="submit">Đăng ký</button>
                </div>
                <div class="text-center pt-2">
                    Bạn đã có tài khoản <a href="/Auth/Login">Đăng nhập ngay</a>
                </div>
            </div>
        </div>
    </form>
    <style>
        .form-signin { width: 100%; max-width: 400px; padding: 15px; margin: 0 auto; }
        .captcha-text { font-weight: bold; color: #333; background-color: #f2f2f2; padding: 5px 10px; border-radius: 5px; margin-left: 10px; }
    </style>
</div>

<?php include '../../Controllers/Footer.php'; ?>
