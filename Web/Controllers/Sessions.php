<?php
if (session_status() == PHP_SESSION_NONE) session_start();
date_default_timezone_set('Asia/Ho_Chi_Minh');

$_Login = null;
$_Users = $_SESSION['account'] ?? null;
$_Ip = $_SERVER['REMOTE_ADDR'];

function fetchUserData($conn, $Username) {
    $stmt = $conn->prepare("SELECT * FROM account WHERE username = :username");
    $stmt->bindValue(":username", $Username, PDO::PARAM_STR);
    $stmt->execute();
    $user_arr = $stmt->fetch(PDO::FETCH_ASSOC);
    if (!$user_arr) return null;

    foreach ($user_arr as $key => $value) $user_data[$key] = htmlspecialchars($value ?? '');
    return [
        "_id" => $user_data['id'],
        "_admin" => $user_data['is_admin'],
        "_user" => $user_data['username'],
        "_Pass" => $user_data['password'],
        "_cash" => $user_data['cash'],
        "_tcash" => $user_data['danap'],
        "_status" => $user_data['active']
    ];
}

function sanitizeUserData($data) {
    if ($data === null) return null;
    return is_array($data) ? array_map('sanitizeUserData', $data) : htmlspecialchars($data);
}

if ($_Users) {
    $_Login = "on";
    $user_data = sanitizeUserData(fetchUserData($conn, $_Users));
    $_Admin = $user_data['_admin'];
    $_Id = $user_data['_id'];
    $_Username = $user_data["_user"];
    $_Password = $user_data["_Pass"];
    $_cashs = $user_data["_cash"];
    $_Tcashs = $user_data["_tcash"];
    $_Status = $user_data["_status"];
}

function formatMoney($number) {
    if (!is_numeric($number) || $number === null) return '0 VNĐ';
    $suffix = '';
    if ($number >= 1e12) { $number /= 1e12; $suffix = ' Tỷ'; }
    elseif ($number >= 1e9) { $number /= 1e9; $suffix = ' Tỷ'; }
    elseif ($number >= 1e6) { $number /= 1e6; $suffix = ' Triệu'; }
    elseif ($number >= 1e3) { $number /= 1e3; $suffix = ' K'; }
    return number_format($number) . $suffix;
}

function isValidInput($input) {
    return preg_match('/^[a-zA-Z0-9_]+$/', $input) && strlen($input) <= 255;
}

function validateCaptcha($input, $captchaText) {
    return strtoupper($input) === strtoupper($captchaText);
}

function generateCaptcha($length = 6) {
    $characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    return substr(str_shuffle($characters), 0, $length);
}

if (!isset($_POST["captcha"])) $_SESSION['captcha'] = generateCaptcha();

function checkExistingUsername($conn, $Username) {
    $stmt = $conn->prepare("SELECT COUNT(*) FROM account WHERE username = :username");
    $stmt->bindValue(':username', $Username, PDO::PARAM_STR);
    $stmt->execute();
    return $stmt->fetchColumn() > 0;
}

function checkExistingEmail($conn, $Email) {
    $stmt = $conn->prepare("SELECT COUNT(*) FROM account WHERE email = :email");
    $stmt->bindValue(':email', $Email, PDO::PARAM_STR);
    $stmt->execute();
    return $stmt->fetchColumn() > 0;
}

function insertAccount($conn, $Username, $Password, $IPAddress) {
    $stmt = $conn->prepare("INSERT INTO account (username, password, ip_address) VALUES (:account, :password, :ip_address)");
    $stmt->bindValue(':account', $Username, PDO::PARAM_STR);
    $stmt->bindValue(':password', $Password, PDO::PARAM_STR);
    $stmt->bindValue(':ip_address', $IPAddress, PDO::PARAM_STR);
    return $stmt->execute();
}

function hasIPBeenUsedForReferral($conn, $IP) {
    $stmt = $conn->prepare("SELECT idRef FROM referrals WHERE IP = ?");
    $stmt->bindValue(1, $IP, PDO::PARAM_STR);
    $stmt->execute();
    return $stmt->fetchColumn();
}

function checkReferralAlreadyAwarded($conn, $IP, $idRef) {
    $stmt = $conn->prepare("SELECT COUNT(*) FROM referrals WHERE IP = ? AND idRef = ?");
    $stmt->bindValue(1, $IP, PDO::PARAM_STR);
    $stmt->bindValue(2, $idRef, PDO::PARAM_INT);
    $stmt->execute();
    return $stmt->fetchColumn() > 0;
}

function getReferrerIP($conn, $idRef) {
    $stmt = $conn->prepare("SELECT ip_address FROM account WHERE id = :idRef");
    $stmt->bindValue(':idRef', $idRef, PDO::PARAM_INT);
    $stmt->execute();
    return $stmt->fetchColumn();
}

function updateReferralPoints($conn, $idRef) {
    $stmt = $conn->prepare("UPDATE account SET referral_points = referral_points + 1 WHERE id = ?");
    $stmt->bindValue(1, $idRef, PDO::PARAM_INT);
    $stmt->execute();
}

function logReferral($conn, $IP, $idRef) {
    $stmt = $conn->prepare("INSERT INTO referrals (IP, idRef) VALUES (?, ?)");
    $stmt->bindValue(1, $IP, PDO::PARAM_STR);
    $stmt->bindValue(2, $idRef, PDO::PARAM_INT);
    $stmt->execute();
}
?>
