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

// Retrieve tasks
$sql = "SELECT id, heading, description FROM tasks";
$result = $conn->query($sql);

$tasks = array();
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $tasks[] = $row;
    }
}

// Return tasks as JSON
echo json_encode($tasks);

$conn->close();
?>
