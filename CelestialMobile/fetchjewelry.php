<?php
// Database configuration
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "celestialdb";

// Create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);

// Check connection
if (!$conn) {
    die(json_encode([
        'status' => 'error',
        'message' => 'Connection failed: ' . mysqli_connect_error()
    ]));
}

// Set headers for JSON response
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

// SQL query to fetch all products
$sql = "SELECT id, name, selling_price, image_path, stocks, size FROM products";
$result = mysqli_query($conn, $sql);

// Check if any products exist
if (mysqli_num_rows($result) > 0) {
    // Fetch products into an array
    $products = [];
    while ($row = mysqli_fetch_assoc($result)) {
        // Convert BLOB to Base64
        $row['image_path'] = base64_encode($row['image_path']);
        $products[] = $row;
    }

    // Return successful response
    echo json_encode([
        'status' => 'success',
        'products' => $products
    ]);
} else {
    // No products found
    echo json_encode([
        'status' => 'error',
        'message' => 'No products found'
    ]);
}

// Close connection
mysqli_close($conn);
?>