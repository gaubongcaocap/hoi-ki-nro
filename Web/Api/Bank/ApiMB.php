<?php
class MBBANK
{

    public $user = '';
    public $pass = '';
    public $deviceIdCommon_goc = '';
    public function bypass_captcha_web2m($key_captcha)
    {
        $get_captcha = $this->get_captcha();
        $img_base64 = json_decode($get_captcha, true)['imageString'];

        $curl = curl_init();
        $dataPost = array(
            "api_key" => $key_captcha,
            "img_base64" => $img_base64,
        );
        curl_setopt_array(
            $curl,
            array(
                CURLOPT_URL => 'https://ecaptcha.sieuthicode.net/api/captcha/mbbank',
                CURLOPT_RETURNTRANSFER => true,
                CURLOPT_ENCODING => '',
                CURLOPT_MAXREDIRS => 10,
                CURLOPT_TIMEOUT => 0,
                CURLOPT_FOLLOWLOCATION => true,
                CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
                CURLOPT_CUSTOMREQUEST => 'POST',
                CURLOPT_POSTFIELDS => $dataPost,
            )
        );

        $response = curl_exec($curl);

        curl_close($curl);

        $responseData = json_decode($response, true);
        if (isset($responseData['data']['captcha'])) {
            return $responseData['data']['captcha'];
        } else {
            return null; // Handle the case when the key "data" or "captcha" is not present
        }
    }
    public function login($captcha)
    {
        $header = array(
            'Accept: application/json, text/plain, */*',
            'Accept-Encoding: gzip, deflate, br',
            'Accept-Language: en-US,en;q=0.9,vi;q=0.8',
            'Authorization: Basic QURNSU46QURNSU4=',
            'Connection: keep-alive',
            'Content-Type: application/json; charset=UTF-8',
            'elastic-apm-traceparent: 00-17620ad87b0b1e04da1d1cf8e8d8c287-bfd8deead47f0f3c-01',
            'Host: online.mbbank.com.vn',
            'Origin: https://online.mbbank.com.vn',
            'Referer: https://online.mbbank.com.vn/pl/login?logout=1',
            'sec-ch-ua: "Chromium";v="104", " Not A;Brand";v="99", "Google Chrome";v="104"',
            'sec-ch-ua-mobile: ?0',
            'sec-ch-ua-platform: "Windows"',
            'Sec-Fetch-Dest: empty',
            'Sec-Fetch-Mode: cors',
            'Sec-Fetch-Site: same-origin',
            'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36',
            'X-Request-Id: c32643bf7bc53262013c298f74e7eee4-2024011212491730',
        );
        $Action = 'https://online.mbbank.com.vn/api/retail_web/internetbanking/doLogin';
        $Data = '{
            "password" : "' . md5($this->pass) . '",
            "refNo" : "' . md5($this->user) . '-' . date("Ymd") . '11534975",
            "sessionId" : null,
            "userId" : "' . strtolower($this->user) . '",
            "captcha" : "' . $captcha . '",
            "deviceIdCommon" : "' . $this->deviceIdCommon_goc . '"
          }';
        $result = $this->CURL2($Action, $header, $Data);
        return $result;
    }
    public function get_captcha()
    {
        $header = array(
            'Accept: application/json, text/plain, */*',
            'Accept-Encoding: gzip, deflate, br',
            'Accept-Language: en-US,en;q=0.9,vi;q=0.8',
            'Authorization: Basic QURNSU46QURNSU4=',
            'Connection: keep-alive',
            'Content-Type: application/json; charset=UTF-8',
            'elastic-apm-traceparent: 00-17620ad87b0b1e04da1d1cf8e8d8c287-bfd8deead47f0f3c-01',
            'Host: online.mbbank.com.vn',
            'Origin: https://online.mbbank.com.vn',
            'Referer: https://online.mbbank.com.vn/pl/login?logout=1',
            'sec-ch-ua: "Chromium";v="104", " Not A;Brand";v="99", "Google Chrome";v="104"',
            'sec-ch-ua-mobile: ?0',
            'sec-ch-ua-platform: "Windows"',
            'Sec-Fetch-Dest: empty',
            'Sec-Fetch-Mode: cors',
            'Sec-Fetch-Site: same-origin',
            'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36',
            'X-Request-Id: 7ed665bc35bb47f19b263447bd1cc180-2022090511445886',
        );
        $Action = 'https://online.mbbank.com.vn/retail-web-internetbankingms/getCaptchaImage';
        $action2 = 'https://online.mbbank.com.vn/api/retail-web-internetbankingms/getCaptchaImage';
        $Data = '{
            "refNo" : "' . date("Ymd") . '11534975",
            "deviceIdCommon" : "' . $this->deviceIdCommon_goc . '",
            "sessionId" : ""
          }';
        $result = $this->CURL2($Action, $header, $Data);
        return $result;
    }

    public function get_lsgd($user, $session_id, $deviceId, $account, $day)
    {
        $url = "https://online.mbbank.com.vn/api/retail-transactionms/transactionms/get-account-transaction-history";

        $payload = array(
            "accountNo" => $account,
            "fromDate" => date("d/m/Y", strtotime("$day days ago")),
            "toDate" => date("d/m/Y"),
            "sessionId" => $session_id,
            "refNo" => $user . "-" . date("Ymdhms") . "14",
            "deviceIdCommon" => $deviceId
        );

        $headers = array(
            "Accept: application/json, text/plain, */*",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: vi-VN,vi;q=0.9",
            "Authorization: Basic RU1CUkVUQUlMV0VCOlNEMjM0ZGZnMzQlI0BGR0AzNHNmc2RmNDU4NDNm",
            "Content-Type: application/json; charset=UTF-8",
            "Deviceid: '.$deviceId.'",
            "Host: online.mbbank.com.vn",
            "Origin: https://online.mbbank.com.vn",
            "Referer: https://online.mbbank.com.vn/information-account/source-account",
            "Refno: " . $user . "-" . date("Ymdhms") . "14",
            "Sec-Ch-Ua: \"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"",
            "Sec-Ch-Ua-Mobile: ?0",
            "Sec-Ch-Ua-Platform: \"Windows\"",
            "Sec-Fetch-Dest: empty",
            "Sec-Fetch-Mode: cors",
            "Sec-Fetch-Site: same-origin",
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "X-Request-Id: " . $user . "-" . date("Ymdhms") . "14"
        );

        $ch = curl_init($url);

        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

        $response = curl_exec($ch);

        if (curl_errno($ch)) {
            echo 'Curl error: ' . curl_error($ch);
        }

        curl_close($ch);
        return $response;
    }




    public function get_balance($account, $session_id, $deviceId)
    {
        $header = array(
            'Accept: application/json, text/plain, */*',
            'Accept-Encoding: gzip, deflate, br',
            'Accept-Language: en-US,en;q=0.9,vi;q=0.8',
            'Authorization: Basic QURNSU46QURNSU4=',
            'Connection: keep-alive',
            'Content-Type: application/json; charset=UTF-8',
            'elastic-apm-traceparent: 00-17620ad87b0b1e04da1d1cf8e8d8c287-bfd8deead47f0f3c-01',
            'Host: online.mbbank.com.vn',
            'Origin: https://online.mbbank.com.vn',
            'Referer: https://online.mbbank.com.vn/information-account/source-account',
            'sec-ch-ua: "Chromium";v="104", " Not A;Brand";v="99", "Google Chrome";v="104"',
            'sec-ch-ua-mobile: ?0',
            'sec-ch-ua-platform: "Windows"',
            'Sec-Fetch-Dest: empty',
            'Sec-Fetch-Mode: cors',
            'Sec-Fetch-Site: same-origin',
            'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36',
            'X-Request-Id: 7ed665bc35bb47f19b263447bd1cc180-2022090511445886',
        );
        $Action = 'https://online.mbbank.com.vn/retail-web-accountms/getBalance';
        $Data = '{
            "refNo" : "' . $account . '-2023090511534518",
            "sessionId" : "' . $session_id . '",
            "deviceIdCommon" : "' . $deviceId . '"
          }';
        $result = $this->CURL2($Action, $header, $Data);
        return $result;
    }
    public function CURL2($Action, $header, $data)
    {
        $curl = curl_init();
        $opt = array(
            CURLOPT_URL => $Action,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_POST => empty($data) ? false : true,
            CURLOPT_POSTFIELDS => $data,
            CURLOPT_CUSTOMREQUEST => empty($data) ? 'GET' : 'POST',
            CURLOPT_HTTPHEADER => $header,
            CURLOPT_ENCODING => "",
            CURLOPT_HEADER => false,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_2,
            CURLOPT_TIMEOUT => 5,
        );
        curl_setopt_array($curl, $opt);
        $body = curl_exec($curl);

        return $body;
    }

    public function generateImei()
    {
        return $this->generateRandomString(8) . '-' . $this->generateRandomString(4) . '-' . $this->generateRandomString(4) . '-' . $this->generateRandomString(4) . '-' . $this->get_time_request();
    }

    public function generateRandomString($length = 20)
    {
        $characters = '0123456789abcdef';
        $charactersLength = strlen($characters);
        $randomString = '';
        for ($i = 0; $i < $length; $i++) {
            $randomString .= $characters[rand(0, $charactersLength - 1)];
        }
        return $randomString;
    }
    public function get_TOKEN()
    {
        return $this->generateRandomString(39);
    }
    public function get_time_request()
    {
        $d = getdate();
        $today = $d['hours'] . $d['minutes'] . $d['seconds'];
        $day = date('Y') . date('m') . date('d');
        return $day . $today;
    }
}