<?php
include '../../Controllers/Connections.php'; // Kết nối cơ sở dữ liệu
include '../../Controllers/Sessions.php';   // Quản lý session
include '../../Controllers/Configs.php';    // Các cấu hình khác

if (isset($_Username)) {
    try {
        // 1. Tính toán phân trang
        $itemsPerPage = 5; // Số bản ghi mỗi trang
        $currentPage = isset($_GET['page']) ? max(1, intval($_GET['page'])) : 1;
        $offset = ($currentPage - 1) * $itemsPerPage;

        // 2. Truy vấn cơ sở dữ liệu
        $stmt = $conn->prepare("SELECT * FROM `napthe` WHERE user_nap = :username ORDER BY request_id DESC LIMIT :offset, :limit");
        $stmt->bindParam(":username", $_Username, PDO::PARAM_STR);
        $stmt->bindValue(":offset", $offset, PDO::PARAM_INT);
        $stmt->bindValue(":limit", $itemsPerPage, PDO::PARAM_INT);
        $stmt->execute();
        $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // 3. Hiển thị bảng dữ liệu
        if (count($result) > 0) {
            echo '<div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead>
                            <tr>
                                <th style="text-align: center;">ID</th>
                                <th style="text-align: center;">Trạng Thái</th>
                                <th style="text-align: center;">Loại Thẻ</th>
                                <th style="text-align: center;">Mã Thẻ</th>
                                <th style="text-align: center;">Seri</th>
                                <th style="text-align: center;">Mệnh Giá</th>
                                <th style="text-align: center;">Thời Gian</th>
                            </tr>
                        </thead>
                        <tbody>';

            foreach ($result as $row) {
                // Xác định trạng thái thẻ
                $statusText = match ($row['status']) {
                    1 => '<span style="background-color: #28a745; color: white; padding: 5px 10px; border-radius: 10px;">Thẻ đúng</span>',
                    2 => '<span style="background-color: #dc3545; color: white; padding: 5px 10px; border-radius: 10px;">Thẻ sai</span>',
                    3 => '<span style="background-color: #dc3545; color: white; padding: 5px 10px; border-radius: 10px;">Thẻ lỗi</span>',
                    default => '<span style="background-color: #ffc107; color: #212529; padding: 5px 10px; border-radius: 10px;">Chờ Duyệt</span>',
                };

                echo "<tr>
                        <td style='text-align: center;'>{$row['request_id']}</td>
                        <td style='text-align: center;'>$statusText</td>
                        <td style='text-align: center;'>{$row['telco']}</td>
                        <td style='text-align: center;'>" . htmlspecialchars($row['code']) . "</td>
                        <td style='text-align: center;'>" . htmlspecialchars($row['serial']) . "</td>
                        <td style='text-align: center;'>" . number_format($row['amount']) . "đ</td>
                        <td style='text-align: center;'>" . date('H:i d/m/Y', strtotime($row['created_at'])) . "</td>
                    </tr>";
            }

            echo '    </tbody>
                    </table>
                  </div>';

            // 4. Phân trang
            $countStmt = $conn->prepare("SELECT COUNT(*) FROM `napthe` WHERE user_nap = :username");
            $countStmt->bindParam(":username", $_Username, PDO::PARAM_STR);
            $countStmt->execute();
            $totalItems = $countStmt->fetchColumn();
            $totalPages = ceil($totalItems / $itemsPerPage);

            if ($totalPages > 1) {
                echo '<nav><ul class="pagination justify-content-center">';
                for ($i = 1; $i <= $totalPages; $i++) {
                    $active = ($i == $currentPage) ? 'active' : '';
                    echo "<li class='page-item $active'><a class='page-link' href='?page=$i'>$i</a></li>";
                }
                echo '</ul></nav>';
            }
        } else {
            echo '<div class="text-center">Không có dữ liệu lịch sử nạp thẻ.</div>';
        }
    } catch (PDOException $e) {
        // Xử lý lỗi
        error_log($e->getMessage());
        echo '<div class="text-center text-danger">Lỗi khi tải lịch sử nạp thẻ. Vui lòng thử lại sau.</div>';
    }
} else {
    echo '<div class="text-center">Bạn cần đăng nhập để xem lịch sử nạp thẻ.</div>';
}
