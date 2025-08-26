<?php
require_once '../../Controllers/Connections.php';
require_once '../../Controllers/Configs.php';

define('POINTS_PER_TOPUP', 1);

$apiUrl = 'https://api.web2m.com/historyapiacbv3/Khanhdzz1/15338301/97FE6E41-1672-3EE1-31DE-1DED3D979F0A';

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $apiUrl);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$response = curl_exec($ch);

if (curl_errno($ch)) {
    echo 'cURL error: ' . curl_error($ch);
    curl_close($ch);
    exit;
}
curl_close($ch);

$data = json_decode($response, true);
if (json_last_error() !== JSON_ERROR_NONE) {
    echo 'JSON decode error: ' . json_last_error_msg();
    exit;
}
if (!isset($data['transactions']) || empty($data['transactions'])) {
    echo 'Không có dữ liệu giao dịch';
    exit;
}

foreach ($data['transactions'] as $cron) {
    $amount = $cron['amount'];
    $description = $cron['description'];
    $type = $cron['type'];
    $transactionID = $cron['transactionID'];
    $transactionDate = $cron['transactionDate'];
    $receiverAccountName = "Ngân Hàng Quân Đội - MBBANK";
    $accountNo = "Không có dữ liệu";
    $bankName = "Ngọc Rồng Light";
    
    if (preg_match('/naptien\s+(\d+)/', $description, $matches)) {
        $id = preg_replace('/\D/', '', $matches[1]);
    }
    $username = $id ?? NULL;
    
    if ($type == 'IN' && $username) {
        if ($amount >= 3000 && !isTransactionExist($transactionID, $conn)) {
            updateAccountBalance($username, $amount, $conn);
            insertTransactionHistory($username, $transactionID, $transactionDate, $amount, $receiverAccountName, $accountNo, $bankName, $conn);
            insertCheckTransaction($transactionID, $conn);
            echo 'Xử lý thành công giao dịch ' . $transactionID;
        }
    }
}

function isTransactionExist($refNo, $conn)
{
    $checkTransactionSql = "SELECT tranid FROM atm_check WHERE tranid = ?";
    $stmt = $conn->prepare($checkTransactionSql);
    $stmt->execute([$refNo]);
    return $stmt->rowCount() > 0;
}

function updateAccountBalance($username, $amount, $conn)
{
    $additionalPercentage = 0; 

    if ($amount >= 10000 && $amount < 200000) {
        $additionalPercentage = 10;
    } elseif ($amount >= 200000 && $amount < 1000000) {
        $additionalPercentage = 10;
    } elseif ($amount >= 1000000 && $amount < 2000000) {
        $additionalPercentage = 10;
    } elseif ($amount >= 5000000 && $amount < 10000000) {
        $additionalPercentage = 10;
    } elseif ($amount >= 10000000) {
        $additionalPercentage = 10;
    }

    $additionalAmount = $amount * ($additionalPercentage / 100);
    $totalAmount = $amount + $additionalAmount;

    $updateAccountSql = "UPDATE account SET ruby = ruby + ?, danap = danap + ? WHERE id = ?";
    $stmt = $conn->prepare($updateAccountSql);
    $stmt->execute([$totalAmount, $totalAmount, $username]);
}

function insertTransactionHistory($username, $refNo, $transactionDate, $amount, $receiverAccountName, $accountNo, $bankName, $conn)
{
    $insertTransactionSql = "INSERT INTO atm_lichsu (user_nap, magiaodich, thoigian, sotien, status, benAccountName, accountNo, bankName) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($insertTransactionSql);
    $stmt->execute([$username, $refNo, $transactionDate, $amount, 1, $receiverAccountName, $accountNo, $bankName]);
}

function insertCheckTransaction($refNo, $conn)
{
    $insertCheckSql = "INSERT INTO atm_check (tranid) VALUES (?)";
    $stmt = $conn->prepare($insertCheckSql);
    $stmt->execute([$refNo]);
}
?>
