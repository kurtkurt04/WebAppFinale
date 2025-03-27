<?php
// Include the database connection file
include "config.php";

// Get the raw POST data (which is in JSON format)
$json_data = file_get_contents('php://input');
error_log("Received JSON: " . $json_data); // Log received data

// Decode the JSON data
$data = json_decode($json_data, true);

if ($data === null) {
    error_log("JSON decoding failed: " . json_last_error_msg());
}

// Validate input data
if (
    isset($data['username']) && 
    isset($data['password']) && 
    isset($data['phone_number']) &&  
    isset($data['email'])
) {
    // Sanitize and validate inputs
    $username = $conn->real_escape_string($data['username']);
    $password = $conn->real_escape_string($data['password']); // No hashing applied
    $phone_number = $conn->real_escape_string($data['phone_number']);
    $email = $conn->real_escape_string($data['email']);

    // Check if username or email already exists
    $check_query = "SELECT * FROM customers_tbl WHERE username = '$username' OR email = '$email'";
    $check_result = $conn->query($check_query);

    if ($check_result->num_rows > 0) {
        $response = ["status" => "failure", "message" => "Username or email already exists"];
    } else {
        // Insert new customer
        $insert_query = "INSERT INTO customers_tbl (username, password, phone_number, email) 
                         VALUES ('$username', '$password', '$phone_number', '$email')";

        if ($conn->query($insert_query)) {
            $customer_id = $conn->insert_id;
            $response = ["status" => "success", "message" => "Customer registered successfully", "customer_id" => $customer_id];
        } else {
            $response = ["status" => "failure", "message" => "Registration failed: " . $conn->error];
        }
    }
} else {
    error_log("Received data but missing required fields.");
    $response = ["status" => "failure", "message" => "Missing required registration fields"];
}

// Send JSON response
header('Content-Type: application/json');
echo json_encode($response);

// Close the connection
$conn->close();

?>
