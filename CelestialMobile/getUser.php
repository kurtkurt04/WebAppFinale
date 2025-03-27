<?php
header("Content-Type: application/json");
include 'config.php';

// Get the raw POST data
$json = file_get_contents('php://input');
$data = json_decode($json, true);

// Check if customerId is received correctly
if (!isset($data['customerId'])) {
    http_response_code(400);
    echo json_encode(["error" => "Customer ID not provided"]);
    exit;
}

// Extract customer ID
$customerId = $data['customerId'];

// Prepare SQL to fetch user details
$sql = "SELECT username, email, phone_number FROM customers_tbl WHERE customer_Id = ?";
$stmt = $conn->prepare($sql);

if ($stmt === false) {
    http_response_code(500);
    echo json_encode(["error" => "Prepare statement failed: " . $conn->error]);
    exit;
}

$stmt->bind_param("i", $customerId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    echo json_encode($user);
} else {
    http_response_code(404);
    echo json_encode(["error" => "User not found"]);
}

$stmt->close();
$conn->close();
?>