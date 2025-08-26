<?php
// Include phần Header
include 'Controllers/Header.php';
?>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <link href="/Assets/Css/bootstrap.min.css" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="/Assets/Js/ThreeCanvas.js" type="text/javascript"></script>
    <script src="/Assets/Js/Snow3d.js" type="text/javascript"></script>
    <script src="/Assets/Js/animation.js?v4" type="text/javascript"></script>
    <title>Trang Chủ</title>
    <link rel="stylesheet" href="path_to_your_styles.css"> <!-- Đường dẫn tới file CSS nếu có -->
    <style>
        .text-container {
            margin-left: 40px;
        }

        .content-text {
            color: #fff;
        }

        .textpost {
            color: #ccc;
        }

        .snowEffect {
            display: block;
            margin: 0 auto;
            width: 100%;
            height: 100%;
            position: absolute;
            top: 0;
            left: 0;
            z-index: -1;
        }
		.bktne {
    display: block;
    margin: auto;
    width: 800px; /* Chiều rộng hình ảnh */
    height: 900px; /* Chiều cao hình ảnh */
    object-fit: cover; /* Đảm bảo hình ảnh không bị méo */
}
    </style>
</head>

<body>
    <div class="snowEffect">
        <canvas width="969" height="969"></canvas>
    </div>
    <div class="card">
        <div class="card-body">
<div style="text-align: center; font-family: Arial, sans-serif;">
    <p style="font-size: 18px; font-weight: bold; color: black;">Quét mã QR sau để nạp tiền:</p>
    <p style="font-size: 16px; color: black;"><em>Lưu ý:</em> Ấn vào mục tài khoản hoặc qua NPC Bardock để kiểm tra ID người chơi và ghi nội dung giao dịch như sau:</p>
    <p style="font-size: 18px; font-weight: bold; color: black;">LEGION + (ID người chơi)</p>
    <p style="font-size: 16px; color: black;">Ví dụ: <strong>LEGION 1234</strong></p>
    <div>
        <img class="bktne" src="/Assets/Images/" style="margin-top: 10px; border-radius: 5px;">
    </div>
</div>

    </div>

    <!-- Tích hợp block.js -->
    <script src="block.js"></script>

    <!-- Phần Footer -->
    <?php
    include 'Controllers/Footer.php';
    ?>
</body>

</html>