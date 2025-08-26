<?php
include '../../Controllers/Header.php';
function convertNumberToText($number) {
    if ($number >= 1000000000) return round($number / 1000000000, 2) . ' tỉ';
    if ($number >= 1000000) return round($number / 1000000, 2) . ' triệu';
    if ($number >= 1000) return round($number / 1000, 2) . ' nghìn';
    return $number;
}
?>

<style>
    .table, .fw-bold, .main, .top-1 { color: #fff; }
    .top-1 { color: #ffffff !important; }
    .text-danger { color: #ffffff !important; }
</style>

<div class="main">
<div class="card">
    <div class="card-body">
        <div class="card-body py-4">
            <div class="text-center text-danger h3">BẢNG XẾP HẠNG</div>
            <div class="d-flex justify-content-center">
                <div class="btn-group">
                    <button type="button" class="btn btn-outline-success fw-semibold active" style="border:1px solid #9e09e8; color: #9e09e8; background-color: #f3e6f5;" id="caothu" data-target="caothu1">Sức Mạnh</button>
                    <button type="button" class="btn btn-outline-success fw-semibold" style="border:1px solid #ff0000; color: #fff; background-color: #ffcccc;" id="luong" data-target="luong1">Top Nạp</button>
                    <button type="button" class="btn btn-outline-success fw-semibold" style="border:1px solid #00aaff; color: #fff; background-color: #cceeff;" id="nhiemvu" data-target="nhiemvu1">Nhiệm Vụ</button>
                </div>
            </div>

            <?php
            $query = "SELECT player.name AS character_name, account.danap FROM account LEFT JOIN player ON account.id = player.account_id ORDER BY account.danap DESC LIMIT 20";
            $stmt = $conn->prepare($query);
            $stmt->execute();
            if ($stmt->rowCount() > 0) {
                echo '<table class="table table-striped table-hover table-bordered table-responsive mt-3 text-center" id="luong1"><thead><tr><th>TOP</th><th>Nhân vật</th><th>Tổng Nạp</th></tr></thead><tbody>';
                $stt = 1;
                while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                    $character_name = !empty($row['character_name']) ? $row['character_name'] : 'Chưa tạo nhân vật';
                    $formatted_danap = convertNumberToText($row['danap']);
                    $row_class = ($stt == 1) ? 'top-1 fw-bold' : 'text-danger fw-bold';
                    echo "<tr><td class='{$row_class}'>{$stt}</td><td class='{$row_class}'>{$character_name}</td><td class='{$row_class}'>{$formatted_danap}</td></tr>";
                    $stt++;
                }
                echo '</tbody></table>';
            } else {
                echo '<p>Không có dữ liệu để hiển thị</p>';
            }
            ?>

            <table class="table table-striped table-hover table-bordered table-responsive mt-3 text-center" id="caothu1">
                <thead><tr><th>TOP</th><th>Nhân vật</th><th>Sức Mạnh</th></tr></thead>
                <tbody>
                    <?php
                    $query = "SELECT name, data_point FROM player";
                    $stmt = $conn->prepare($query);
                    $stmt->execute();
                    $players = [];
                    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                        $data_point = explode(',', $row['data_point']);
                        $power = isset($data_point[1]) ? (int)$data_point[1] : 0;
                        $players[] = ['name' => htmlspecialchars($row['name']), 'power' => $power];
                    }
                    usort($players, fn($a, $b) => $b['power'] - $a['power']);
                    $stt = 1;
                    foreach (array_slice($players, 0, 50) as $player) {
                        $row_class = ($stt == 1) ? 'top-1 fw-bold' : 'text-danger fw-bold';
                        $formatted_power = convertNumberToText($player['power']);
                        echo "<tr><td class='{$row_class}'>{$stt}</td><td class='{$row_class}'>{$player['name']}</td><td class='{$row_class}'>{$formatted_power}</td></tr>";
                        $stt++;
                    }
                    ?>
                </tbody>
            </table>

            <table class="table table-striped table-hover table-bordered table-responsive mt-3 text-center" id="nhiemvu1">
                <thead><tr><th>TOP</th><th>Nhân vật</th><th>Nhiệm Vụ Chính</th><th>Nhiệm Vụ Phụ</th></tr></thead>
                <tbody>
                    <?php
                    $query = "SELECT name, data_task FROM player";
                    $stmt = $conn->prepare($query);
                    $stmt->execute();
                    $tasks = [];
                    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                        $data_task = json_decode($row['data_task'], true);
                        $tasks[] = [
                            'name' => htmlspecialchars($row['name']),
                            'main_task_points' => isset($data_task[0]) ? (int)$data_task[0] : 0,
                            'sub_task_points' => isset($data_task[2]) ? (int)$data_task[2] : 0
                        ];
                    }
                    usort($tasks, fn($a, $b) => ($b['main_task_points'] - $a['main_task_points']) ?: ($b['sub_task_points'] - $a['sub_task_points']));
                    $stt = 1;
                    foreach (array_slice($tasks, 0, 50) as $task) {
                        $row_class = ($stt == 1) ? 'top-1 fw-bold' : 'text-danger fw-bold';
                        echo "<tr><td class='{$row_class}'>{$stt}</td><td class='{$row_class}'>{$task['name']}</td><td class='{$row_class}'>{$task['main_task_points']}</td><td class='{$row_class}'>{$task['sub_task_points']}</td></tr>";
                        $stt++;
                    }
                    ?>
                </tbody>
            </table>
        </div>
    </div>
</div>
</div>

<script>
    const tables = { caothu: "caothu1", luong: "luong1", nhiemvu: "nhiemvu1" };
    Object.keys(tables).forEach(id => document.getElementById(id).addEventListener("click", function() {
        document.querySelectorAll('.btn-group button').forEach(btn => btn.classList.remove('active'));
        this.classList.add('active');
        Object.values(tables).forEach(tableId => document.getElementById(tableId).style.display = "none");
        document.getElementById(tables[id]).style.display = "table";
    }));
</script>

<?php include '../../Controllers/Footer.php'; ?>
