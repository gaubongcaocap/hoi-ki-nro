<?php
include '../../Controllers/Header.php';
function isValidUsername($Username)
{
    return ctype_alnum($Username);
}

function loginUser($Username, $Password, $conn)
{
    global $_ThongBao, $_AuthLog;

    try {
        if ($_AuthLog == 1) {
            $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Đang bảo trì đăng nhập, vui lòng thử lại sau!</div>';
            return;
        }

        $stmt = $conn->prepare("SELECT * FROM account WHERE username = :Username");
        $stmt->bindParam(':Username', $Username, PDO::PARAM_STR);
        $stmt->execute();
        $select = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($select !== false && $select['password'] == $Password) {
            // Mã hoá đầu ra để tránh XSS khi hiển thị Username
            $_SESSION['account'] = htmlspecialchars($Username, ENT_QUOTES, 'UTF-8');
            $_SESSION['id'] = $select['id'];
            echo '<script>window.location.href = "/";</script>';
            exit();
        } else {
            $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Tên đăng nhập hoặc mật khẩu không hợp lệ, vui lòng kiểm tra lại!</div>';
        }
    } catch (PDOException $e) {
        $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại sau!</div>';
    }
}

if (isset($_POST['username']) && isset($_POST['password'])) {
    $Username = htmlspecialchars(trim($_POST['username']), ENT_QUOTES, 'UTF-8');
    $Password = htmlspecialchars(trim($_POST['password']), ENT_QUOTES, 'UTF-8');

    if (!isValidUsername($Username)) {
        $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Tên đăng nhập chỉ được chứa kí tự và số!</div>';
    } else {
        loginUser($Username, $Password, $conn);
    }
} elseif (isset($_POST['submit'])) {
    $_ThongBao = '<div class="text-danger pb-2 font-weight-bold">Vui lòng nhập tên đăng nhập và mật khẩu!</div>';
}
?>
<div class="container pb-5">
    <form class="form-signin" method="post">
        <div class="card">
            <div class="card-body">
                <?php
                if (!empty($_ThongBao)) {
                    echo $_ThongBao;
                }
                ?>
                <h1 class="h3 mb-3 font-weight-normal text-center">Đăng nhập</h1>
                <div id="thongbao"></div>
                <div class="form-group mb-2">
                    <label class="sr-only">Tài khoản</label>
                    <input type="text" class="form-control" placeholder="Tài khoản" required="" name="username">
                </div>
                <div class="form-group mb-2">
                    <label class="sr-only">Mật khẩu</label>
                    <input type="password" class="form-control" placeholder="Mật khẩu" required="" name="password">
                </div>
                <div class="checkbox mb-3">
                    <label>
                        <input type="checkbox" class="form-checkbox" name="remember" value="forever"> Nhớ đăng nhập
                    </label>
                </div>
                <div class="form-group text-center">
                    <button class="btn btn-info btn-block text-white" type="submit" id="Login">Đăng nhập</button>
                </div>
                <div class="text-center pt-2">
                    Bạn chưa có tài khoản <a href="/Auth/Register">Đăng ký ngay</a>
                </div>
                

            </div>
        </div>
    </form>
    <style>
        .form-signin {
            width: 100%;
            max-width: 400px;
            padding: 15px 0;
            margin: 0 auto;
        }
    </style>
</div>
<?php include '../../Controllers/Footer.php'; ?>