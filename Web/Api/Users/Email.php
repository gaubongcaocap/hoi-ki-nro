<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require '../../vendor/autoload.php'; // Đường dẫn đến file autoload.php của PHPMailer
include '../../Controllers/Connections.php';
include '../../Controllers/Configs.php';
include '../../Controllers/Sessions.php';

// Xử lý yêu cầu POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Nhận dữ liệu từ request POST
    $postData = json_decode(file_get_contents('php://input'), true);
    $email = filter_var($postData['email'], FILTER_VALIDATE_EMAIL);

    // Kiểm tra email có hợp lệ không
    if (!$email) {
        echo json_encode(["success" => false, "message" => "Email không hợp lệ"]);
    } else {
        // Kiểm tra xem email có tồn tại trong cơ sở dữ liệu không
        $accountExists = checkExistingEmail($conn, $email);

        if (!$accountExists) {
            echo json_encode(["success" => false, "message" => "Email không tồn tại trong hệ thống"]);
        } else {
            // Tạo mã xác minh ngẫu nhiên
            $verificationCode = mt_rand(100000, 999999);

            // Lưu mã xác minh vào session
            $_SESSION['verification_code'] = $verificationCode;
            $_SESSION['email'] = $email;

            // Gửi email xác minh
            try {
                $mail = new PHPMailer(true);

                $mail->isSMTP();
                $mail->Host = 'smtp.gmail.com';
                $mail->SMTPAuth = true;
                $mail->Username = $_ForgotEmail;
                $mail->Password = $_ForgotPass;
                $mail->SMTPSecure = 'tls';
                $mail->Port = 587;

                $mail->setFrom($_ForgotEmail, $_ServerName);
                $mail->addAddress($email);

                $mail->isHTML(true);
                $mail->CharSet = 'UTF-8';
                $mail->Subject = 'Quên Mật Khẩu [ Nguyen Duc Kien ]';
                $mail->Body = '
                    <html>
                    <head>
                        <title>Xác nhận mật khẩu mới</title>
                    </head>
                    <body style="font-family: Arial, sans-serif;">
                        <p>Kính gửi quý khách hàng,</p>
                        <span>Dưới đây là mã xác minh mới của bạn: </span><b>' . $verificationCode . '</b>
                        <p>Vui lòng không tiết lộ mã này cho bất kỳ ai khác!</p>
                        <p>Xin cảm ơn bạn đã tin dùng dịch vụ của chúng tôi.</p>
                        <span>Trân trọng, <b>Nguyễn Đức Kiên</b></span>
                    </body>
                    </html>
                ';

                $mail->send();

                // Cập nhật mật khẩu mới vào cơ sở dữ liệu
                $query = "UPDATE account SET password = :verificationCode WHERE email = :email";
                $statement = $conn->prepare($query);
                $statement->bindParam(':verificationCode', $verificationCode, PDO::PARAM_STR);
                $statement->bindParam(':email', $email, PDO::PARAM_STR);
                $statement->execute();

                echo json_encode(["success" => true, "message" => "Đã gửi mật khẩu mới về Email: " . $email]);
            } catch (Exception $e) {
                echo json_encode(["success" => false, "message" => "Có lỗi xảy ra khi gửi email. Vui lòng thử lại sau."]);
            }
        }
    }
}