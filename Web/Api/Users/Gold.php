<?php
include_once '../../Controllers/Connections.php';
include_once '../../Controllers/Sessions.php';
include '../../Controllers/Configs.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $postData = json_decode(file_get_contents('php://input'), true);
    if (isset($postData["vnd_amount"]) && isset($postData['gold_amount']) && isset($postData["username"])) {
        // Lấy giá trị của vnd_amount và username từ dữ liệu JSON
        $vnd_amount = $postData["vnd_amount"];
        $gold_amount = $postData["gold_amount"];
        $username = $postData["username"];

        try {
            // Truy vấn cơ sở dữ liệu để lấy số dư hiện tại của người dùng
            $Vnd_New = $conn->prepare("SELECT ruby FROM account WHERE username = :username");
            $Vnd_New->execute(['username' => $username]);
            $Vnd_Old = $Vnd_New->fetchColumn();
            if ($Vnd_Old >= $vnd_amount) {
                $Update_Gold = $conn->prepare("UPDATE account SET thoi_vang = thoi_vang + :thoi_vang WHERE username = :username");
                $Update_Gold->execute(['thoi_vang' => $gold_amount, 'username' => $username]);

                $Update_Vnd = $conn->prepare("UPDATE account SET ruby = ruby - :vnd_amount WHERE username = :username");
                $Update_Vnd->execute(['vnd_amount' => $vnd_amount, 'username' => $username]);
                echo json_encode(["success" => true, "message" => "Đổi thành công $gold_amount Thỏi."]);
            } else {
                echo json_encode(["success" => false, "message" => "Số dư không đủ để thực hiện giao dịch."]);
            }
        } catch (PDOException $e) {
            error_log("PDOException: " . $e->getMessage(), 0);
            echo json_encode(["success" => false, "message" => "Có lỗi xảy ra. Vui lòng thử lại sau."]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Dữ liệu không đủ hoặc không hợp lệ."]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Yêu cầu không hợp lệ."]);
}