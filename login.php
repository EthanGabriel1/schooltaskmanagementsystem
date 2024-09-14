<?php
header('Content-Type: application/json');
require 'db.php'; // Include MySQLi connection

// Enable error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Get POST data
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($email) || empty($password)) {
    echo json_encode(["success" => 0, "message" => "Email and Password are required."]);
    exit;
}

// Prepare and execute SQL query to get password hash for the email
$stmt = $conn->prepare("SELECT userid, password FROM user_data WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();
$stmt->bind_result($user_id, $password_hash);

if ($stmt->num_rows === 0) {
    echo json_encode(["success" => 0, "message" => "Invalid email or password."]);
    exit;
}

$stmt->fetch();

// Verify password
if (!password_verify($password, $password_hash)) {
    echo json_encode(["success" => 0, "message" => "Incorrect password."]);
    exit;
}

// Generate a token (could be JWT or simple token)
$token = bin2hex(random_bytes(16));

// Respond with success and token
echo json_encode(["success" => 1, "token" => $token]);
?>
