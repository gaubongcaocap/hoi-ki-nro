<?php
#Nguyen Duc Kien - Ngoc Rong Min

$_Logo = 'banner.jpg'; // Thay tên + đuôi của Logo vào đây
$_Domain = 'https://nroLegion.online/';
$_Title = 'Ngọc Rồng Legion - Chơi Là Nghiện';
$_Description = 'Web đăng kí tải game';
$_Keyword = 'Nro Legion, Nro, Nro Lậu, Ngọc Rồng, Ngọc Rồng Online, Chú Bé Rồng';
$_ForgotEmail = 'Email'; // Gmail Chạy Quên Mật Khẩu
$_ForgotPass = 'Password'; // Mật Khẩu Gmail Chạy Quên Mật Khẩu

#Tăng Giá Trị Đổi
$_GiaTri = '1'; // Nạp x1 -> x2 -> x3 (Thẻ Cào)
$_GiaTriAtm = '1'; // Chuyển Khoản x1 -> x2 -> x3
$_danap = '1';

$_TrangThai = '0'; // Hoạt Động = 1, Bảo Trì = 0 (Trạng Thái Nạp Tiền)
$_FixWeb = '0'; // Bảo Trì = 1, Không Bảo Trì = 0
$_AuthLog = '0'; // Bảo Trì = 1, Không Bảo Trì = 0

#Hỗ Trợ
$_Zalo = 'https://zalo.me/';
$_ZaloX1 = '';
$_ZaloX2 = '';


#---------------#
#Downloads
$_Android = 'https://www.mediafire.com/file/1s1s1jixez3yjjf/NroMin%25282%2529.apk/file';
$_Iphone =  'https://www.mediafire.com/file/v1i87tgr6wucgob/NroMin%25282%2529.ipa/file';
$_Windows = 'https://www.mediafire.com/file/pjzk56ei3u9w0ue/NroMin%25282%2529.rar/file';
$_JavaX1 = '';


#Card
$Partner_Key = ''; #dung chua
$Partner_Id = '5154';
$_ApiCard = 'https://thesieutoc.net/'; // Link Đại Lý Thẻ

#Atm - Mbbank
$userloginmbbank_config = 'taikhoan'; // Tài khoản đăng nhập Mbbank của bạn tại https://online.mbbank.com.vn
$passmbbank_config = 'matkhau'; // Mật khẩu đăng nhập Mbbank của bạn tại https://online.mbbank.com.vn
$deviceIdCommon_goc_config = 'ssss'; // Thay cái thông số mà bạn lấy được từ F12 vào đây
$stkmbbank_config = ''; // Số tài khoản Mbbank
$mbbank_name = ''; // Tên Tài khoản Mbbank
$_mbbank = 'Ngân Hàng VCB'; // Ngân hàng quân đội Mbbank
$_Token = ($conn->query("SELECT token FROM cpanel")->fetchColumn()) ?? '';
function CreateToken()
{
    return md5(uniqid(rand(), true));
}

#Chặn truy cập vào xem Json, Dữ Liệu ở Api
function isLocalhost()
{
    $whitelist = array(
        '127.0.0.1',
        '::1'
    );
    return in_array($_SERVER['REMOTE_ADDR'], $whitelist);
}


