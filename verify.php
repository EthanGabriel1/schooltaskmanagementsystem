<?php
require 'db.php'; // Include MySQLi connection

$response = ['success' => 0, 'message' => ''];

// Get the verification code from POST
$verification_code = $_POST['verification_code'] ?? '';
$email = $_POST['email'] ?? '';

// Validate input
if (empty($verification_code)) {
    $response['message'] = 'Verification code is required.';
    echo json_encode($response);
    exit;
}

// Check if the verification code is valid in the unverified_users table
$stmt = $conn->prepare("SELECT * FROM unverified_users WHERE email = ? AND verification_code = ?");
$stmt->bind_param("ss", $email, $verification_code);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();

    // Move user to user_data
    $stmt = $conn->prepare("INSERT INTO user_data (email, password, name, phone_num) VALUES (?, ?, ?, ?)");
    $stmt->bind_param("ssss", $user['email'], $user['password'], $user['name'], $user['phone_num']);
    
    if ($stmt->execute()) {
        // Delete from unverified_users
        $stmt = $conn->prepare("DELETE FROM unverified_users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();

        $response['success'] = 1;
        $response['message'] = 'Email verified successfully.';
    } else {
        $response['message'] = 'Verification failed. Please try again.';
    }
} else {
    $response['message'] = 'Invalid verification code or email.';
}

echo json_encode($response);
?>
