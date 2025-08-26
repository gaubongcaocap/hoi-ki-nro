<?php
session_start();
require_once ('../../Controllers/Connections.php');
require_once ('../../Controllers/Configs.php');

include ('ApiMB.php');

// Kiểm tra xem key có được cung cấp không
if (!isset($_GET['users']) || $_GET['users'] !== $userloginmbbank_config) {
    // Nếu key không hợp lệ, có thể redirect hoặc hiển thị thông báo lỗi
    exit('Không tìm thấy key! Không thể truy cập.');
}

$mbbank = new MBBANK;
$userloginmbbank = $userloginmbbank_config;
$passmbbank = $passmbbank_config;
$stkmbbank = $stkmbbank_config;
$deviceId = $deviceIdCommon_goc_config;

// Kiểm tra dữ liệu đầu vào
if (empty($userloginmbbank) || empty($passmbbank) || empty($stkmbbank)) {
    exit('Vui lòng điền tài khoản đăng nhập, mật khẩu và số tài khoản');
}

$mbbank->user = $userloginmbbank;
$mbbank->pass = $passmbbank;
$time = time();

// Bypass captcha
$text_captcha = $mbbank->bypass_captcha_web2m('413145b2f6d981e32d0ee69a56b0e839');
$login = json_decode($mbbank->login($text_captcha), true);

// Kiểm tra lỗi login
if (isset($login['result']['message']) && ($login['result']['message'] == "Capcha code is invalid" || $login['result']['message'] == 'Customer is invalid')) {
    exit('Captcha không chính xác hoặc thông tin không chính xác');
}

// Sử dụng Prepared Statements để tránh SQL Injection
$nduckien = $conn->prepare("SELECT userlogin FROM cpanel WHERE userlogin = ?");
$nduckien->execute([$userloginmbbank]);
$existingAccount = $nduckien->fetch();

if ($existingAccount) {
    // Update thông tin tài khoản nếu đã tồn tại
    $nduckien = $conn->prepare("UPDATE cpanel SET stk = ?, name = ?, password = ?, sessionId = ?, deviceId = ?, token = ?, time = ? WHERE userlogin = ?");
    $nduckien->execute([$stkmbbank, $login['cust']['nm'] ?? '', $passmbbank, $login['sessionId'] ?? '', $deviceId, CreateToken(), $time, $userloginmbbank]);

    if ($nduckien->rowCount() > 0) {
        exit('Cập nhật tài khoản thành công');
    } else {
        exit('Lỗi khi cập nhật tài khoản');
    }
} else {
    // Thêm mới hoặc cập nhật tài khoản nếu không tồn tại
    $nduckien = $conn->prepare("INSERT INTO cpanel (userlogin, stk, name, password, sessionId, deviceId, token, time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE stk = VALUES(stk), name = VALUES(name), password = VALUES(password),
        sessionId = VALUES(sessionId), deviceId = VALUES(deviceId), token = VALUES(token), time = VALUES(time)");

    $nduckien->execute([$userloginmbbank, $stkmbbank, $login['cust']['nm'] ?? '', $passmbbank, $login['sessionId'] ?? '', $deviceId, CreateToken(), $time]);

    if ($nduckien->rowCount() > 0) {
        exit('Thêm mới/cập nhật tài khoản thành công');
    } else {
        exit('Lỗi khi thêm mới/cập nhật tài khoản');
    }
}