<?php
include 'Connections.php';
include 'Sessions.php';
include 'Configs.php';

if (($_Login === null && strpos($_SERVER['REQUEST_URI'], '/Users/') !== false) || ($_Login == true && strpos($_SERVER['REQUEST_URI'], '/Auth/') !== false)) {
    echo '<script>window.location.href = "/";</script>';
}

if (isset($_FixWeb) && $_FixWeb == 1) {
    echo "Máy chủ đang bảo trì website. Vui lòng chờ nhé!";
    exit;
}
?>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <meta name="theme-color" content="#000000">
    <meta name="title" content="<?= $_Description ?>">
    <meta name="description" content="<?= $_Description ?>">
    <meta name="keywords" content="<?= $_Keyword ?>">
    <meta name="author" content="<?= $_Description ?>">
    <meta property="og:type" content="website">
    <meta property="og:url" content="index">
    <meta property="og:title" content="Trang Chủ Game Ngọc Rồng Legion - Chơi Là Nghiện">
    <meta property="og:description" content="<?= $_Description ?>">
    <meta property="og:image" content="/Assets/Images/<?= $_Logo ?>">
    <meta property="og:image:alt" content="Tham Gia Ngay">
    <link rel="apple-touch-icon" href="/Assets/Images/<?= $_Logo ?>">
    <link rel="icon" href="/Assets/Images/<?= $_Logo ?>">
    <link rel="shortcut icon" href="#">
    <title><?= $_Title ?></title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.11.5/sweetalert2.all.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/clipboard.js/2.0.8/clipboard.min.js"></script>

    <link href="/Assets/Css/bootstrap.min.css" rel="stylesheet">
    <script src="/Assets/Css/bootstrap.min.js"></script>
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>

</head>
<style>
    .logo {
        max-width: 450px;
        height: auto;
    }

    @media (max-width: 768px) {

        /* Tablet */
        .logo {
            max-width: 300px;
        }
    }

    @media (max-width: 480px) {

        /* Mobile */
        .logo {
            max-width: 200px;
        }
    }
</style>

<body class="page-layout-color">
    <div id="root">
        <div class="container">
            <div class="main">
                <div class="text-center card">
                    <div class="card-body">
                        <div>
                            <a href="/"><img class="logo" alt="Logo" src="/Assets/Images/NROLEGION.GIF"></a>
                        </div>

                        <div
                            class="row text-center justify-content-center row-cols-2 row-cols-lg-6 g-2 g-lg-2 my-1 mb-2">
                            <?php if ($_Login === null) { ?>
                                <div class="col">
                                    <div class="px-2">
                                        <a class="btn btn-menu btn-success w-100 fw-semibold" href="/Auth/Login">Đăng
                                            nhập</a>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="px-2">
                                        <a class="btn btn-menu btn-success w-100 fw-semibold" href="/Auth/Register">Đăng
                                            ký</a>
                                    </div>
                                </div>
                                <a class="btn btn-menu btn-success px-3 py-1" href="/Others/Downloads">Tải Game</a>
                            <?php } else { ?>
                                <div class="col">
                                    <div class="px-2">
                                        <a class="btn btn-menu btn-success w-100 fw-semibold" href="/Users/Profile"
                                            style="white-space: nowrap;">
                                            <?php echo $_Username ?>
                                        </a>
                                    </div>
                                </div>
                                <div class="col">
                                    <div class="px-2">
                                        <a class="btn btn-menu btn-success w-100 fw-semibold" href="/Auth/Logout">Đăng
                                            Xuất</a>
                                    </div>
                                </div>
                                <?php if ($_Admin == 1) { ?>
                                    <div class="col">
                                        <div class="px-2">
                                            <a class="btn btn-menu btn-success w-100 fw-semibold" href="/Users/Admin">Admin</a>
                                        </div>
                                    </div>
                                    <a class="btn btn-menu btn-success px-3 py-1" href="/Others/Downloads">Tải Game</a>
                                <?php } ?>
                            <?php } ?>
                        </div>
                    </div>
                </div>

                <div class="mb-2">
                    <div class="row text-center justify-content-center row-cols-2 row-cols-lg-6 g-2 g-lg-2 mt-1">
                        <div class="col">
                            <div class="px-2"><a class="btn btn-menu btn-success w-100 fw-semibold active"
                                    href="/">Trang
                                    chủ</a></div>
                        </div>
                        <div class="col">
                            <div class="px-2"><a class="btn btn-menu btn-success w-100 fw-semibold false"
                                    href="Bkt.php">Nạp Tiền</a></div>
                        </div>

                        <div class="col">
                            <div class="px-2"><a class="btn btn-menu btn-success w-100 fw-semibold false"
                                    href="https://zalo.me/g/pivbvl549">Box Zalo 1</a></div>
                        </div>
                        <div class="modal fade" id="myModal" tabindex="-1" aria-labelledby="exampleModalLabel"
                            aria-hidden="true">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="exampleModalLabel"></h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="my-2">
                                            <a href="<?= $_Zalo ?>"
                                                class="btn btn-menu btn-danger w-100 fw-semibold my-1">Box
                                                Zalo</a>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <script>
                    document.getElementById("banner").addEventListener("click", function () {
                        window.location.href = "/"; // Chuyển hướng đến trang chủ khi nhấp vào hình ảnh
                    });
                </script>

                <script>
                    $(document).ready(function () {
                        // Chỉ thực hiện các hành động khi trang là trang chính ("/", Home)
                        <?php if ($_Login === null) { ?>
                            if (window.location.pathname === '/') {
                                $('#serverModal').modal('show');

                                $(document).click(function (event) {
                                    if ($(event.target).hasClass('modal')) {
                                        $('#serverModal').modal('hide');
                                    }
                                });

                                $('#serverModal a').click(function () {
                                    $('#serverModal').modal('hide');
                                });
                            }
                        <?php } ?>
                    });
                </script>

                <style>
                    #myModal .modal-content {
                        background-color: #FCE3C6 !important;
                    }

                    #myModal a.btn.btn-menu.btn-danger.w-100.fw-semibold.my-1 {
                        border-radius: 10px;
                        background-color: #8f34f5 !important;
                        color: #ffffff !important;
                        box-shadow: none !important;
                    }

                    /* Hiệu ứng nhấp nháy 7 màu cho chữ */
                    @keyframes rainbow {

                        14% {
                            color: orange;
                        }

                        28% {
                            color: yellow;
                        }

                        48% {
                            color: pink;
                        }

                        84% {
                            color: violet;
                        }

                    }

                    .rainbow-text {
                        animation: rainbow 3s infinite;
                    }

                    /* Hiệu ứng border 7 màu chạy xung quanh nút */
                    @keyframes rainbow-border {
                        0% {
                            border-color: red;
                        }

                        14% {
                            border-color: orange;
                        }

                        28% {
                            border-color: yellow;
                        }

                        42% {
                            border-color: green;
                        }

                        56% {
                            border-color: blue;
                        }

                        70% {
                            border-color: indigo;
                        }

                        84% {
                            border-color: violet;
                        }

                        100% {
                            border-color: red;
                        }
                    }

                    .rainbow-border {
                        animation: rainbow-border 1s infinite;
                    }
                </style>
                <style>
                    .ant-list-item-meta {
                        display: flex;
                        flex: 1 1;
                        align-items: flex-start;
                        max-width: 100%;
                    }

                    .ant-list-item-meta-content {
                        flex: 1 0;
                        width: 0;
                        color: rgba(0, 0, 0, .85);
                    }

                    .ant-list-item-meta-title {
                        margin-bottom: 4px;
                        color: rgba(0, 0, 0, .85);
                        font-size: 14px;
                        line-height: 1.5715;
                    }

                    .ant-list-item-meta-description {
                        color: rgba(0, 0, 0, .45);
                        font-size: 14px;
                        line-height: 1.5715;
                    }
                </style>