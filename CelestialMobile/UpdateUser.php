<?php
header("Content-Type: application/json");

// Database connection details
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "celestialdb";

// Response array
$response = array();

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    $response['success'] = false;
    $response['message'] = "Connection failed: " . $conn->connect_error;
    echo json_encode($response);
    exit();
}

// Receive JSON input
$json = file_get_contents('php://input');
$data = json_decode($json, true);

// Debug: Log incoming data
error_log("Received data: " . print_r($data, true));

// Validate input with more detailed logging
if (!isset($data['customer_id']) || 
    !isset($data['username']) || 
    !isset($data['email']) || 
    !isset($data['phone_number'])) {
    
    $response['success'] = false;
    $response['message'] = "Missing required fields";
    $response['received_data'] = $data;
    echo json_encode($response);
    $conn->close();
    exit();
}

// Prepare SQL update statement
$stmt = $conn->prepare("UPDATE customers_tbl SET 
    username = ?, 
    email = ?, 
    phone_number = ? 
    WHERE customer_id = ?");

// Bind parameters
$stmt->bind_param(
    "ssii", 
    $data['username'], 
    $data['email'], 
    $data['phone_number'], 
    $data['customer_id']
);

// Execute the update
if ($stmt->execute()) {
    $response['success'] = true;
    $response['message'] = "User details updated successfully";
} else {
    $response['success'] = false;
    $response['message'] = "Failed to update user details: " . $stmt->error;
}

// Close statement and connection
$stmt->close();
$conn->close();

// Send JSON response
echo json_encode($response);
?>