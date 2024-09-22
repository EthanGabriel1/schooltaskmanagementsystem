<?php
require 'db.php'; // Ensure this contains your MySQLi connection

// Enable error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$response = ['success' => 0, 'message' => ''];

// Sanitize inputs
$email = strtolower(filter_var($_POST['email'] ?? '', FILTER_SANITIZE_EMAIL));
$password = $_POST['password'] ?? '';
$name = htmlspecialchars($_POST['name'] ?? '');

// Validate input
if (!filter_var($email, FILTER_VALIDATE_EMAIL) || empty($password) || empty($name)){
    $response['message'] = 'Invalid input. Please check your details.';
    echo json_encode($response);
    exit;
}

// Check if email already exists in user_data
$stmt = $conn->prepare("SELECT email FROM user_data WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    $response['message'] = 'Email already registered.';
    echo json_encode($response);
    exit;
}

// Check if email exists in unverified_users
$stmt = $conn->prepare("SELECT email FROM unverified_users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    $response['message'] = 'Email pending verification.';
    echo json_encode($response);
    exit;
}

// Generate a random verification code
$verification_code = (string) random_int(1000, 9999); // Cast to string explicitly

// Hash the password
$hashedPassword = password_hash($password, PASSWORD_BCRYPT);

// Prepare and insert the new user into unverified_users
$stmt = $conn->prepare("INSERT INTO unverified_users (email, password, name, verification_code) VALUES (?, ?, ?, ?)");
$stmt->bind_param("ssss", $email, $hashedPassword, $name, $verification_code);

if ($stmt->execute()) {
    // PHPMailer setup to send the verification email
    try {
        // Create a new PHPMailer instance
        $mail = new PHPMailer(true);

        // SMTP configuration
        $mail->isSMTP();
        $mail->Host = 'smtp.gmail.com'; // Set the SMTP server to send through
        $mail->SMTPAuth = true;
        $mail->Username = 'ethangabrielrolloque@gmail.com'; // Gmail username
        $mail->Password = 'fuwqmutjrckjtzsr'; // Gmail app-specific password
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS; // Enable TLS encryption
        $mail->Port = 587; // TCP port to connect to

        // Email settings
        $mail->setFrom('ethangabrielrolloque@gmail.com', 'SchoolTaskManagementSystem'); // Sender's email and name
        $mail->addAddress($email); // Add recipient's email

        // Email content
        $mail->isHTML(true); // Set email format to HTML
        $mail->Subject = 'Email Verification Code';
        $mail->Body = "Your verification code is: <b>$verification_code</b>";

        // Send the email
        $mail->send();

        // Respond with success
        $response['success'] = 1;
        $response['message'] = 'Registration successful. Please verify your email.';
    } catch (Exception $e) {
        $response['message'] = 'Mail could not be sent. Error: ' . $mail->ErrorInfo;
    }
} else {
    $response['message'] = 'Registration failed. Please try again.';
}

// Output response
echo json_encode($response);
?>
