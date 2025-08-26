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
    </style>
</head>

<body>
    <div class="snowEffect">
        <canvas width="969" height="969"></canvas>
    </div>
    <div class="card">
        <div class="card-body">
            <div class="card-title h5">Trang chủ</div>

            <div class="mt-4">
                <!-- Bài đăng 1 -->
                <div class="post-item d-flex align-items-center my-2 justify-content-between">
                    <div class="post-image">
                        <img src="/Assets/Images/dfwfpf8-98f685fe-34f0-4aa3-a409-1f8fae08bb2f.gif" alt="Avatar">
                    </div>
                    <div class="ant-list-item-meta">
                        <div class="text-container">
                            <a href="\Pages\Others\Sukien.php" style="color: #0d6efd; font-weight: 700;">Hướng dẫn cơ
                                bản</a>
                            <p class="text-muted font-weight-bold">Đăng bởi <span class="text-danger fw-bold">Admin</span></p>
                        </div>
                    </div>
                </div>

                <!-- Bài đăng 2 -->
                <div class="post-item d-flex align-items-center my-2 justify-content-between">
                    <div class="post-image">
                        <img src="/Assets/Images/dfov4st-05d4dd59-299f-48e0-a657-f6e6f1b6049e.gif" alt="Avatar">
                    </div>
                    <div class="ant-list-item-meta">
                        <div class="text-container">
                            <a href="/Others/Top" style="color: #0d6efd; font-weight: 700;">Bảng xếp hạng</a>
                            <p class="text-muted font-weight-bold">Đăng bởi <span class="text-danger fw-bold">Admin</span></p>
                        </div>
                    </div>
                </div>
            </div>
<!-- Bài đăng 3 -->
<div class="post-item d-flex align-items-center my-2 justify-content-between">
    <div class="post-image">
        <img src="/Assets/Images/Nrolegion.png" alt="Avatar">
    </div>
    <div class="ant-list-item-meta">
        <div class="text-container">
            <a href="/Pages/Others/ModsGuide.php" style="color: #0d6efd; font-weight: 700;">Hướng dẫn sử dụng Mod</a>
            <p class="text-muted font-weight-bold">Đăng bởi <span class="text-danger fw-bold">Admin</span></p>
        </div>
    </div>
</div>

            <hr>

<div>
<div class="title">
    <h2 style=" font-weight: bold;">Giới Thiệu</h2>
</div>
<div class="content-text" style="font-size: 16px; line-height: 1.6; color: #333;">
    <p>
        <strong>Ngọc Rồng Legion</strong> là một tựa <b>game nhập vai</b>, lấy cảm hứng từ bộ truyện tranh huyền thoại 
        <b>7 Viên Ngọc Rồng</b>. Trò chơi đưa bạn vào một thế giới đầy thử thách, nơi bạn có thể hóa thân thành 
        những chiến binh mạnh mẽ thuộc ba hành tinh:
    </p>
    <ul>
        <li><b>Saiyan</b> – Chiến binh sở hữu sức chống chịu cao và kĩ năng biến khỉ.</li>
        <li><b>Namek</b> – Sở hữu khả năng đánh cực nhanh và mạnh.</li>
        <li><b>Trái Đất</b> – Sở hữu chiêu thức Kamejoko cực mạnh và khả năng khống chế đối thủ.</li>
    </ul>
    <p>
        Trong hành trình của mình, bạn sẽ <b>rèn luyện sức mạnh</b>, <b>thu thập ngọc rồng</b> và <b>đối đầu với những thế lực hùng mạnh</b> 
        để bảo vệ Trái đất.
    </p>

    <h3 style=" font-weight: bold;">Lối Chơi Hấp Dẫn</h3>
    <ul>
        <li><b>Hệ thống nhân vật đa dạng:</b> Lựa chọn giữa ba hành tinh với kỹ năng riêng biệt.</li>
        <li><b>Chiến đấu đỉnh cao:</b> Học và nâng cấp các kỹ năng huyền thoại như Kamejoko, Thái Dương Hạ San, Kaioken...</li>
        <li><b>Nhiệm vụ theo cốt truyện:</b> Đồng hành cùng nhân vật, tham gia vào những trận chiến kinh điển.</li>
        <li><b>Hệ thống PvP & PvE:</b> Đối đầu với người chơi khác hoặc săn boss để nhận phần thưởng hiếm.</li>
    </ul>

    <h3 style=" font-weight: bold;">Tính Năng Đặc Sắc</h3>
    <ul>
        <li><b>Đồ họa 2D mượt mà</b>, nhẹ nhàng nhưng không kém phần sống động.</li>
        <li><b>Kết nối cộng đồng:</b> Bang hội, giao dịch và săn boss cùng bạn bè.</li>
        <li><b>Trang bị phong phú:</b> Tùy chỉnh nhân vật với hàng loạt vật phẩm đặc biệt.</li>
        <li><b>Sự kiện hấp dẫn:</b> Tham gia các hoạt động, phó bản để nhận phần thưởng giá trị.</li>
    </ul>
</div>




                    <p style='color:red;text-align:center;margin-bottom: -10px;'>Cơ Bản</p>
                    <br>
                    <p style="text-align:center">
                        <img alt="" src="../Assets/Images/gif_maphongba.gif">
                        <img alt="" src="../Assets/Images/gif_gif_Saiyain.gif">
                        <img alt="" src="../Assets/Images/gif_supber_kame.gif">
                    </p>
                    <p style='color:red;text-align:center;margin-bottom: -10px;'>VIP</p>
                    <br>
                    <p style="text-align:center">
                        <img alt="" src="../Assets/Images/gif_maphongba_VIP.gif">
                        <img alt="" src="../Assets/Images/gif_gif_Saiyain_VIP.gif">
                        <img alt="" src="../Assets/Images/gif_supber_kame_VIP.gif">
                    </p>
                    <p style="text-align:center"><a href="https://ngocrongonline.com/?c=skill">Xem th&ecirc;m</a></p>
                </div>
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