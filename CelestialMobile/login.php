<?php
// Include the database connection file
include "config.php";

// Get the raw POST data (which is in JSON format)
$json_data = file_get_contents('php://input');

// Decode the JSON data into an associative array
$data = json_decode($json_data, true);

// Check if the data was decoded correctly
if (isset($data['username']) && isset($data['password'])) {
    $user_username = $conn->real_escape_string($data['username']);
    $user_password = $conn->real_escape_string($data['password']);

    // SQL query to find a matching user in the customers_tbl
    $query = "SELECT customer_id, username, phone_number, email FROM customers_tbl 
              WHERE username = '$user_username' AND password = '$user_password'";
    $result = $conn->query($query);

    // Check if the user exists
    if ($result->num_rows > 0) {
        // Fetch the user details
        $user = $result->fetch_assoc();
        
        // Login successful - return user details including customer_id
        $response = array(
            "status" => "success", 
            "message" => "Login successful",
            "customer_id" => (int)$user['customer_id'],
            "username" => $user['username'],
            "phone_number" => $user['phone_number'],
            "email" => $user['email']
        );
    } else {
        // Login failed
        $response = array("status" => "failure", "message" => "Invalid username or password");
    }

    // Send the response back to the Android app
    echo json_encode($response);
} else {
    // If username or password is not provided
    echo json_encode(array("status" => "failure", "message" => "Username or password not provided"));
}

// Close the connection
$conn->close();
?>