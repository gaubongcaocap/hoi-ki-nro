<?php
include '../../Controllers/Connections.php';
include '../../Controllers/Sessions.php';
include '../../Controllers/Configs.php';

// Hàm activateAccount nhận các giá trị từ phía client và xử lý chúng
function activateAccount($status, $vnds, $username, $conn)
{
    // Kiểm tra trạng thái tài khoản và đảm bảo rằng nó không phải là '1'
    if ($status == '1') {
        return array(
            'success' => false,
            'message' => 'Tài khoản của bạn đã được kích hoạt!'
        );
    }

    // Kiểm tra trạng thái tài khoản có hợp lệ hay không
    if ($status != '0' && $status != '-1') {
        return array(
            'success' => false,
            'message' => 'Trạng thái tài khoản không hợp lệ!'
        );
    }

    // Kiểm tra số tiền có đủ không
    if ($cashs < 5000) {
        return array(
            'success' => false,
            'message' => 'Bạn không đủ 5.000 Vnd. Vui lòng nạp thêm tiền vào tài khoản để ' . ($status == '0' ? 'kích hoạt nhé!' : 'mở lại tài khoản!')
        );
    }

    // Cập nhật tài khoản trong cơ sở dữ liệu
    $stmt = $conn->prepare('UPDATE account SET active = 1, ruby = :vnd WHERE username = :username');
    $stmt->bindValue(':vnd', $cashs - 5000);
    $stmt->bindValue(':username', $username);
    if ($stmt->execute() && $stmt->rowCount() > 0) {
        return array(
            'success' => true,
            'message' => ($status == '-1') ? 'Mở khóa tài khoản thành công. Bây giờ bạn đã có thể đăng nhập vào game!' : 'Kích hoạt tài khoản thành công. Bây giờ bạn đã có thể đăng nhập vào game!'
        );
    } else {
        return array(
            'success' => false,
            'message' => 'Có lỗi gì đó xảy ra. Vui lòng liên hệ Admin!'
        );
    }
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $postData = json_decode(file_get_contents('php://input'), true);
    $response = activateAccount($postData['status'], $postData['vnds '], $postData['username'], $conn);
    header('Content-Type: application/json');
    echo json_encode($response);
}