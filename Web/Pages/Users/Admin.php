<?php
include '../../Controllers/Header.php';

// chỉ cho phép tài khoản có admin = 1 truy cập
if ($_Admin != 1) {
    echo '<script>window.location.href="../home"</script>';
    exit;
}

// Khởi tạo biến $_alert
$_alert = '';

// Xử lý khi form được submit
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Lấy dữ liệu từ form
    $username = $_POST["username"];
    $cash = intval($_POST["cash"]);

    // Kiểm tra xem có tài khoản nào khớp với tên đăng nhập không
    $sql_check = "SELECT * FROM account WHERE username = :username";
    $statement_check = $conn->prepare($sql_check);
    $statement_check->bindParam(':username', $username, PDO::PARAM_STR);
    $statement_check->execute();

    if ($statement_check->rowCount() == 0) {
        $_alert = '<div class="alert alert-danger">Lỗi: Tài khoản không tồn tại!</div>';
    } else {
        $row = $statement_check->fetch();
        if ($row["ban"] == 1) {
            $_alert = '<div class="alert alert-danger">Lỗi: Tài khoản đã bị vi phạm và không thể cộng tiền!</div>';
        } else {
            // Cập nhật tiền
            $sql_update = "UPDATE account SET cash = cash + :cash, danap = danap + :cash WHERE username = :username";
            $statement_update = $conn->prepare($sql_update);
            $statement_update->bindParam(':cash', $cash, PDO::PARAM_INT);
            $statement_update->bindParam(':username', $username, PDO::PARAM_STR);

            if ($statement_update->execute()) {
                $_alert = '<div class="alert alert-success">Cộng tiền thành công!</div>';
            } else {
                $_alert = '<div class="alert alert-warning">Lỗi: Kết nối đến máy chủ</div>';
            }
        }
    }
}
?>
<div class="container pb-5">
    <div class="col-md-12">
        <div class="card">
            <div class="card-body">
                <h4>Cộng Tiền</h4>
                <br>
                <?php echo $_alert; ?>
                <form method="POST" class="payment-form">
                    <div class="mb-3">
                        <label class="text-black">Tên Tài Khoản:</label>
                        <input type="username" class="form-control" name="username" id="username"
                            placeholder="Nhập tên tài khoản" required autocomplete="username">
                    </div>
                    <div class="mb-3">
                        <label class="text-black">Số Tiền:</label>
                        <input type="cash" class="form-control" name="cash" id="cash" placeholder="Nhập số tiền cần cộng"
                            required autocomplete="cash">
                    </div>
                    <button type="submit"
                        style="display: block; margin: 0 auto; border: 2px solid #8BC34A; background-color: #C5E1A5; color: #FFFFFF; padding: 5px 20px; font-size: 16px; border-radius: 5px; cursor: pointer;">
                        Thực Hiện
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<div class="container pb-5">
    <div class="col-md-12">
        <div class="card">
            <div class="card-body">
                <h4>Lịch Sử ATM</h4>
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>ND</th>
                                <th>Thời Gian</th>
                                <th>Số Tiền</th>
                                <th>Trạng Thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php
                            // Truy vấn cơ sở dữ liệu với LIMIT 10 và sắp xếp theo thời gian giảm dần
                            $sql = "SELECT * FROM atm_lichsu ORDER BY thoigian DESC LIMIT 10"; // Lấy 10 bản ghi gần đây nhất
                            $statement = $conn->prepare($sql);
                            $statement->execute();
                            $transactions = $statement->fetchAll(PDO::FETCH_ASSOC);

                            // Hiển thị dữ liệu trong bảng
                            foreach ($transactions as $transaction) {
                                $status = '';
                                switch ($transaction['status']) {
                                    case 1:
                                        $status = '<span style="background-color: #28a745; color: white; padding: 5px 10px; border-radius: 10px;">Đã thanh toán</span>';
                                        break;
                                    case 2:
                                        $status = '<span style="background-color: #dc3545; color: white; padding: 5px 10px; border-radius: 10px;">Chưa thanh toán</span>';
                                        break;
                                    default:
                                        $status = '<span style="background-color: #ffc107; color: #212529; padding: 5px 10px; border-radius: 10px;">Đang xử lý</span>';
                                }

                                echo "<tr>";
                                echo "<td style='white-space: nowrap;'>" . $transaction['id'] . "</td>";
                                echo "<td style='white-space: nowrap;'>" . $transaction['user_nap'] . "</td>";
                                echo "<td style='white-space: nowrap;'>" . $transaction['thoigian'] . "</td>";
                                // Sử dụng number_format để định dạng số tiền và thêm "đ" sau
                                echo "<td style='white-space: nowrap;'>" . number_format($transaction['sotien']) . "đ</td>";
                                echo "<td style='white-space: nowrap;'>" . $status . "</td>";
                                echo "</tr>";
                            }

                            ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container pb-5">
    <div class="col-md-12">
        <div class="card">
            <div class="card-body">
                <h4>Tổng Doanh Thu</h4>
                <?php
                // Tính tổng doanh thu hôm nay với status == 1
                $sql_today = "SELECT SUM(sotien) AS tong_doanh_thu FROM atm_lichsu WHERE DATE(STR_TO_DATE(thoigian, '%d/%m/%Y %H:%i:%s')) = CURDATE() AND status = 1";
                $statement_today = $conn->prepare($sql_today);
                $statement_today->execute();
                $today_result = $statement_today->fetch(PDO::FETCH_ASSOC);
                $today_revenue = $today_result['tong_doanh_thu'];

                // Tính tổng doanh thu tuần này với status == 1
                $sql_week = "SELECT SUM(sotien) AS tong_doanh_thu FROM atm_lichsu WHERE YEARWEEK(STR_TO_DATE(thoigian, '%d/%m/%Y %H:%i:%s'), 1) = YEARWEEK(CURDATE(), 1) AND status = 1";
                $statement_week = $conn->prepare($sql_week);
                $statement_week->execute();
                $week_result = $statement_week->fetch(PDO::FETCH_ASSOC);
                $week_revenue = $week_result['tong_doanh_thu'];

                // Tính tổng doanh thu tháng này với status == 1
                $sql_month = "SELECT SUM(sotien) AS tong_doanh_thu FROM atm_lichsu WHERE MONTH(STR_TO_DATE(thoigian, '%d/%m/%Y %H:%i:%s')) = MONTH(CURDATE()) AND YEAR(STR_TO_DATE(thoigian, '%d/%m/%Y %H:%i:%s')) = YEAR(CURDATE()) AND status = 1";
                $statement_month = $conn->prepare($sql_month);
                $statement_month->execute();
                $month_result = $statement_month->fetch(PDO::FETCH_ASSOC);
                $month_revenue = $month_result['tong_doanh_thu'];
                ?>


                <div class="row">
                    <div class="col-md-4">
                        <div class="card text-white bg-success mb-3">
                            <div class="card-header">Doanh Thu Hôm Nay</div>
                            <div class="card-body">
                                <h5 class="card-title"><?php echo number_format($today_revenue) . "đ"; ?></h5>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-white bg-primary mb-3">
                            <div class="card-header">Doanh Thu Tuần Này</div>
                            <div class="card-body">
                                <h5 class="card-title"><?php echo number_format($week_revenue) . "đ"; ?></h5>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card text-white bg-warning mb-3">
                            <div class="card-header">Doanh Thu Tháng Này</div>
                            <div class="card-body">
                                <h5 class="card-title"><?php echo number_format($month_revenue) . "đ"; ?></h5>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<?php
include '../../Controllers/Footer.php';
?>