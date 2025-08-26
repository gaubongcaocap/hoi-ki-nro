<?php
$databaseConfig = [
    'ip' => "localhost",
    'dbname' => "nro1",
    'user' => "root",
    'pass' => ""
];

// Set time zone
date_default_timezone_set('Asia/Ho_Chi_Minh');

// Function to establish a database connection
function getDatabaseConnection($connConfig)
{
    try {
        $dsn = "mysql:host={$connConfig['ip']};dbname={$connConfig['dbname']};charset=utf8mb4";
        $conn = new PDO($dsn, $connConfig['user'], $connConfig['pass']);
        $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $conn;
    } catch (PDOException $e) {
        // Handle the exception in a more graceful way
        die("Connection failed: " . $e->getMessage());
    }
}

$conn = getDatabaseConnection($databaseConfig);