<?php
//require_once 'nduckien/config.php';
//$myfile = fopen("checkthe.txt", "w") or die("Unable to open file!");
//$txt = "Run Checkthe\n";
//fwrite($myfile, $txt);
// $partner_id = '3659221582';
// $partner_key = 'b013b15bb55453887a3af5c725c5e820';
//fclose($myfile);
// echo $partner_id. ' '. $partner_key.'\n';
//$results = fetchRows("SELECT * FROM napthe WHERE status = 0 OR status = 99");
include '../../Controllers/Connections.php';
include '../../Controllers/Sessions.php';
include '../../Controllers/Configs.php';


$results = $conn->prepare("SELECT * FROM napthe WHERE status = 0 OR status = 99");
$results->execute();

foreach ($results as $row) {
        //các thẻ có data status bằng 0 thì gửi request kiểm tra
    // echo  $partner_id;."\n";
    // echo $row['loaithe']. " ".$row['code']." ".$row['seri']." ".$row['amount']." ".$row['tranid']." ".$partner_id." ".$row['callback_sign']."\n";
        $dataPost = array();
        $dataPost['telco'] = $row['telco'];
        $dataPost['code'] = $row['code'];
        $dataPost['serial'] = $row['serial'];
        $dataPost['amount'] = $row['amount'];
        $dataPost['request_id'] = $row['request_id'];
        $dataPost['partner_id'] = $Partner_Id;
        $dataPost['sign'] = $row['callback_sign'];
        $dataPost['command'] = "check";

        $data = http_build_query($dataPost);
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $_ApiCard . 'chargingws/v2');
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        $actual_link = (isset($_SERVER['HTTPS']) ? "https" : "http") . "://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
        curl_setopt($ch, CURLOPT_REFERER, $actual_link);
        $result = curl_exec($ch);
        curl_close($ch);
        echo $result."\n";
        $obj = json_decode($result,true);

    $mySign = $row['callback_sign'];
    $user_nap = $row['user_nap'];

    
        $real_value = $obj['amount'] != null ? $obj['amount'] : 0;
        $price = $row['amount'] != null ? $row['amount'] : 0;
        $Status_Update = $conn->prepare("UPDATE napthe SET status = :status, real_value = :real_value WHERE callback_sign = :callback_sign");
        if ($Status_Update->execute([':status' => $obj['status'], ':real_value' => $obj['amount'] , ':callback_sign' => $mySign])) {
            if ($obj['status'] != null && $obj['status'] == 1) {
                $Account_Update = $conn->prepare("UPDATE account SET ruby = ruby + :price, danap = danap + :price WHERE username = :user_nap");
                if (!$Account_Update->execute([':price' => $price, ':user_nap' => $user_nap])) {
                    $log .= "Lỗi cập nhật account: " . implode(" ", $Account_Update->errorInfo()) . "\n";
                }
            }
        } else {
            $log .= "Không thể cập nhật trạng thái cho code: $code, serial: $serial\n";
        }
        //$log .= $price;

        // _update_value("napthe", array(
        //     'tinhtrang'   => $obj['status'],
        //     'status' => $obj['status'],
        //     'tranid'     => $obj['trans_id'],
        //     //'noidung'      => $obj['message'],
        //     //'real_value'      => $obj['value'],
        //     'amount'       => $obj['amount'],
        // ), "callback_sign = '$mySign'", 1);
        //     //$newPrice = $price * 2;
        //     $newPrice = $price;
        //     $stmt = $conn->prepare("UPDATE account SET money = money + ?, total_money = total_money + ? WHERE username = ?");
        //     //$stmt->execute([$price, $price, $username]);
        //     $stmt->execute([$newPrice, $price, $username]);
        
    //} 
    // else {//loi
    //         _update_value("lichsu_card", array(
    //             'tinhtrang'   => $obj['status'],
    //             'status' => "102",
    //             'noidung'      => $obj['message'],
    //         ), "callback_sign = '$mySign'", 1);
    // }
    // sleep(1);
}
?>