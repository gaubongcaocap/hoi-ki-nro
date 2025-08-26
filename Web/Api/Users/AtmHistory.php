<?php
include '../../Controllers/Connections.php';
include '../../Controllers/Sessions.php';
include '../../Controllers/Configs.php';

if (isset($_Id)) {
    $itemsPerPage = 5;
    $stmt = $conn->prepare("SELECT * FROM atm_lichsu WHERE user_nap = :id ORDER BY thoigian DESC LIMIT $itemsPerPage");

    $stmt->bindParam(":id", $_Id);
    $stmt->execute();
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
    if (count($result) > 0) {
        echo '<table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th style="text-align: center;">ID</th>
                        <th style="text-align: center;">Trạng Thái</th>
                        <th style="text-align: center;">Số Tiền</th>
                        <th style="text-align: center;">Mã Giao Dịch</th>
                        <th style="text-align: center;">Ngày Tháng</th>
                    </tr>
                </thead>
                <tbody>';
        foreach ($result as $index => $row) {
            $count = $index + 1;
            $status = '';
            switch ($row['status']) {
                case 1:
                    $status = '<span style="background-color: #28a745; color: white; padding: 5px 10px; border-radius: 10px;">Đã thanh toán</span>';
                    break;
                case 2:
                    $status = '<span style="background-color: #dc3545; color: white; padding: 5px 10px; border-radius: 10px;">Chưa thanh toán</span>';
                    break;
                default:
                $status = '<span style="background-color: #ffc107; color: #212529; padding: 5px 10px; border-radius: 10px;">Đang xử lý</span>';
            }

            // Chuyển đổi số tiền sang định dạng 50,000đ
            $formatted_amount = number_format($row['sotien']) . 'đ';

            echo '
                    <tr class="border-solid border border-transparent border-b-orange-300">
                        <td style="text-align: center; white-space: nowrap;">' . $count . '</td>
                        <td style="text-align: center; white-space: nowrap;">' . $status . '</td>
                        <td style="text-align: center; white-space: nowrap;">' . $formatted_amount . '</td>
                        <td style="text-align: center; white-space: nowrap;">' . $row['magiaodich'] . '</td>
                        <td style="text-align: center; white-space: nowrap;">' . $row['thoigian'] . '</td>
                    </tr>';
        }
        echo '</tbody></table>';
    } else {
        echo '<div class="text-center" style="white-space: nowrap;">Không có dữ liệu lịch sử giao dịch ATM.</div>';
    }
} else {
    echo '<div class="text-center" style="white-space: nowrap;">Không tìm thấy tên người dùng trong bảng account.</div>';
}
?>
