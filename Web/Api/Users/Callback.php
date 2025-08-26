<?php
require_once '../../Controllers/Connections.php';
require_once '../../Controllers/Configs.php';
define('POINTS_PER_TOPUP', 1);

$jsonBody = json_decode(file_get_contents('php://input'));
$log = "";

function writeToLog($message)
{
    file_put_contents("nduckien.log", $message . " | " . date("Y-m-d H:i:s") . "\n", FILE_APPEND);
}

if (isset($jsonBody->callback_sign) && $jsonBody->callback_sign == md5($Partner_Key . $jsonBody->code . $jsonBody->serial)) {
    $code = $jsonBody->code;
    $serial = $jsonBody->serial;

    $stmt = $conn->prepare("SELECT * FROM napthe WHERE code = :code AND serial = :serial");
    $stmt->execute([':code' => $code, ':serial' => $serial]);

    if ($stmt->rowCount() > 0) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $user_nap = $row['user_nap'];
        $price = $row['amount'] * $_GiaTri;

        $Status_Update = $conn->prepare("UPDATE napthe SET status = :status WHERE code = :code AND serial = :serial");
        if ($Status_Update->execute([':status' => $jsonBody->status, ':code' => $code, ':serial' => $serial])) {
            if ($jsonBody->status == 1) {
                $Account_Update = $conn->prepare("UPDATE account SET ruby = ruby + :price, danap = danap + :price WHERE username = :user_nap");
                if (!$Account_Update->execute([':price' => $price, ':user_nap' => $user_nap])) {
                    $log .= "Lỗi cập nhật account: " . implode(" ", $Account_Update->errorInfo()) . "\n";
                }
            }
        } else {
            $log .= "Không thể cập nhật trạng thái cho code: $code, serial: $serial\n";
        }
    } else {
        $log .= "Không tìm thấy thẻ code: $code, serial: $serial\n";
    }
} else {
    $log .= "Key của bạn không hợp lệ hoặc bị khoá\n";
}

$message = !empty($log) ? $log : match ($jsonBody->status) {
    1 => "Thành Công (User: $user_nap | ruby: " . formatMoney($price) . ", danap: " . formatMoney($price),
    2 => "Thẻ sai (User: $user_nap)",
    3 => "Thẻ lỗi (User: $user_nap)",
    default => ''
};
writeToLog($message);