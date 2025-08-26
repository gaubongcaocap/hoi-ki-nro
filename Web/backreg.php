<?php
include '../../Controllers/Header.php';
$Username = '';
$Password = '';
$RePassword = '';
$IP = $_SERVER['REMOTE_ADDR']; // Lấy địa chỉ IP của người dùng
$idRef = isset($_GET['idRef']) ? intval($_GET['idRef']) : 0; // Lấy idRef từ URL nếu có

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $recaptchaResponse = $_POST['g-recaptcha-response'];
    $secretKey = "6LcNLYAqAAAAAHjmdU7jQ7A4S6A8ygWqe0JKof8I";  // Sử dụng secret key mới
    $verifyUrl = "https://www.google.com/recaptcha/api/siteverify?secret={$secretKey}&response={$recaptchaResponse}&remoteip={$IP}";

    $response = file_get_contents($verifyUrl);
    $responseKeys = json_decode($response, true);

    if (intval($responseKeys["success"]) !== 1) {
        $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Vui lòng xác thực reCAPTCHA.</div>';
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
                    $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Đăng kí thành công!!</div>';

                    // Kiểm tra và cập nhật điểm giới thiệu
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
                <?php if (!empty($_ThongBao)) {
                    echo $_ThongBao;
                } ?>
                <div class="form-group mb-2">
                    <label>Tài khoản</label>
                    <input type="text" class="form-control" placeholder="Tài khoản" required="" name="username" value=""
                        minlength="3">
                </div>
                <div class="form-group mb-2">
                    <label>Mật khẩu</label>
                    <input type="password" class="form-control" placeholder="Mật khẩu" required="" name="password"
                        minlength="3">
                </div>
                <div class="form-group mb-2">
                    <label>Xác nhận mật khẩu</label>
                    <input type="password" class="form-control" placeholder="Xác nhận mật khẩu" required=""
                        name="RePasswordword" minlength="3">
                </div>

                <div class="form-group text-center">
                    <!-- Thay thế site key mới -->
                    <div class="g-recaptcha" data-sitekey="6LcNLYAqAAAAAOqhd_f44KuNlTlVDAFQ6pleSkl6"></div>
                    <button id="Register" class="btn btn-info btn-block text-white" type="submit">Đăng ký</button>
                </div>
            
                <div class="text-center pt-2">
                    Bạn đã có tài khoản <a href="/Auth/Login">Đăng nhập ngay</a>
                </div>

            </div>
        </div>
    </form>
    <style>
        .form-signin {
            width: 100%;
            max-width: 400px;
            padding: 15px;
            margin: 0 auto;
        }
    </style>
</div>
<?php include '../../Controllers/Footer.php'; ?>
