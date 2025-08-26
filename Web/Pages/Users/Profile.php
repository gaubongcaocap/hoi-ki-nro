<?php
include '../../Controllers/Header.php';
?>
<div class="container pb-5">
    <div class="row">
        <div class="col-md-12 px-0">
            <div class="card">
                <div class="card-body">
                    <div class="row text-center justify-content-center row-cols-3 row-cols-lg-6 g-1 g-lg-1"
                        data-metatip="true">
                        <div class="col">
                            <a class="btn btn-sm btn-success w-100 fw-semibold active" href="/Users/Profile"
                                style="background-color: #198754;">Thông Tin</a>
                        </div>
                        <div class="col">
                            <a class="btn btn-sm btn-success w-100 fw-semibold active" href="/Users/History"
                                style="background-color: #198754;">Lịch
                                sử GD</a>
                        </div>
                    </div>
                    <table class="table table-border">
                        <tbody>
                            <tr>
                                <th scope="row">ID</th>
                                <th><?= $_Id ?></th>
                            </tr>
                            <tr>
                                <th scope="row">Tài Khoản</th>
                                <th><?= $_Username ?></th>
                            </tr>

                            <tr>
                                <th scope="row">Số Dư</th>
                                <td><b class="text-danger1"><?= number_format($_cashs) ?> cash</b>
                                </td>
                            </tr>
                            <tr>
                                <th scope="row">Tổng Nạp</th>
                                <td><b class="text-danger1"><?= number_format($_Tcashs) ?> cash</b>
                                </td>
                            </tr>
                            <tr>
                                <th scope="row">Trạng Thái</th>
                                <td class="font-weight-bold">
                                    <?php if ($_Status == 0) { ?>
                                        <p style="color: red;">Chưa kích hoạt</p>
                                    <?php } else { ?>
                                        <p style="color: green;">Đã kích hoạt</p>
                                    <?php } ?>
                                </td>

                            </tr>
                            <tr>
                                <th scope="row">Mật khẩu</th>
                                <td>
                                    <a href="/Users/ChangePassword" class="btn btn-outline-info">Đổi mật khẩu</a>
                                </td>
                            </tr>
                            <!-- <tr>
                                <th scope="row">Đăng xuất</th>
                                <td>
                                    <a href="/Auth/Logout" class="btn btn-outline-info"> Đăng xuất </a>
                                </td>
                            </tr> -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<?php
include '../../Controllers/Footer.php';
?>