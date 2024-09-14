<?php
$servername = "localhost";
$username = "u843230181_taskmanagement";
$password = "Taskmanage123456";
$dbname = "u843230181_TaskManagement";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$heading = $_POST['heading'];
$description = $_POST['description'];

// Insert task
$sql = "INSERT INTO tasks (heading, description) VALUES ('$heading', '$description')";

if ($conn->query($sql) === TRUE) {
    echo "New task added successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
