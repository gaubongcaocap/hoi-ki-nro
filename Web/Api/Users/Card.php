<?php
include '../../Controllers/Connections.php';
include '../../Controllers/Sessions.php';
include '../../Controllers/Configs.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Xử lý yêu cầu từ form nạp thẻ
    $postData = json_decode(file_get_contents('php://input'), true);
    $response = array();

    // Kiểm tra trạng thái bảo trì
    if ($_TrangThai == 1) {
        $response = array(
            'success' => false,
            'message' => 'Đang bảo trì nạp thẻ, vui lòng thử lại sau!'
        );
    } elseif (empty($postData['telco']) || empty($postData['amount']) || empty($postData['serial']) || empty($postData['code'])) {
        $response = array(
            'success' => false,
            'message' => 'Vui lòng nhập đầy đủ thông tin!'
        );
    } else {
        // Thông tin từ client
        $telco = filter_var($postData['telco'], FILTER_SANITIZE_STRING);
        $serial = filter_var($postData['serial'], FILTER_SANITIZE_STRING);
        $code = filter_var($postData['code'], FILTER_SANITIZE_STRING);
        $amount = filter_var($postData['amount'], FILTER_VALIDATE_INT);
        $username = $_SESSION['username'] ?? 'guest'; // Lấy username từ session hoặc mặc định là 'guest'

        // Tạo ID giao dịch ngẫu nhiên
        $request_id = rand(100000000, 999999999);

        // Gửi yêu cầu tới API
        $dataPost = array(
            'request_id' => $request_id,
            'code' => $code,
            'partner_id' => $Partner_Id,
            'serial' => $serial,
            'telco' => $telco,
            'amount' => $amount,
            'command' => 'charging',
            'sign' => md5($Partner_Key . $code . $serial)
        );

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $_ApiCard . 'chargingws/v2');
        curl_setopt($ch, CURLOPT_POST, 1);
        curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($dataPost));
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        $result = curl_exec($ch);
        curl_close($ch);

        // Xử lý kết quả từ API
        $obj = json_decode($result);
        if ($obj && $obj->status == 99) {
            // Lưu thông tin vào cơ sở dữ liệu
            $stmt = $conn->prepare("INSERT INTO napthe (request_id, user_nap, telco, serial, code, amount, status, created_at)
                                    VALUES (:request_id, :user_nap, :telco, :serial, :code, :amount, 99, NOW())");
            $stmt->bindParam(':request_id', $request_id);
            $stmt->bindParam(':user_nap', $username);
            $stmt->bindParam(':telco', $telco);
            $stmt->bindParam(':serial', $serial);
            $stmt->bindParam(':code', $code);
            $stmt->bindParam(':amount', $amount);

            if ($stmt->execute()) {
                $response = array(
                    'success' => true,
                    'message' => 'Nạp thành công, vui lòng đợi máy chủ duyệt!'
                );
            } else {
                $response = array(
                    'success' => false,
                    'message' => 'Lỗi khi lưu dữ liệu vào máy chủ!'
                );
            }
        } else {
            $response = array(
                'success' => false,
                'message' => $obj->message ?? 'Thẻ nạp không hợp lệ!'
            );
        }
    }

    // Trả về JSON
    header('Content-Type: application/json');
    echo json_encode($response);
    exit;
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nạp Thẻ</title>
    <link rel="stylesheet" href="path/to/your/css.css"> <!-- Đường dẫn CSS -->
</head>
<body>
    <form id="napTheForm">
        <label for="telco">Nhà mạng:</label>
        <select id="telco" name="telco" required>
            <option value="VIETTEL">VIETTEL</option>
            <option value="MOBIFONE">MOBIFONE</option>
            <option value="VINAPHONE">VINAPHONE</option>
        </select>

        <label for="amount">Mệnh giá:</label>
        <select id="amount" name="amount" required>
            <option value="10000">10,000đ</option>
            <option value="50000">50,000đ</option>
            <option value="100000">100,000đ</option>
        </select>

        <label for="serial">Serial:</label>
        <input type="text" id="serial" name="serial" required />

        <label for="code">Mã thẻ:</label>
        <input type="text" id="code" name="code" required />

        <button id="submitButton" type="submit">Thực hiện</button>
    </form>

    <div id="response"></div>

    <script>
        document.getElementById("napTheForm").addEventListener("submit", function (event) {
            event.preventDefault(); // Ngăn form reload trang

            const telco = document.getElementById("telco").value;
            const amount = document.getElementById("amount").value;
            const serial = document.getElementById("serial").value;
            const code = document.getElementById("code").value;

            const payload = { telco, amount, serial, code };

            fetch("card.php", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            })
                .then((response) => response.json())
                .then((data) => {
                    if (data.success) {
                        alert(data.message); // Thông báo nếu thành công
                        document.getElementById("napTheForm").reset();
                    } else {
                        alert("Lỗi: " + data.message); // Thông báo nếu thất bại
                    }
                })
                .catch((error) => {
                    console.error("Error:", error);
                    alert("Đã xảy ra lỗi khi gửi yêu cầu.");
                });
        });
    </script>
</body>
</html>
