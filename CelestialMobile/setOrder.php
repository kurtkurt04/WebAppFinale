<?php
// Database Connection
$servername = "localhost";
$username = "root"; // Change if needed
$password = ""; // Change if needed
$dbname = "celestialdb"; // Your database name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode([
        'status' => 'error', 
        'message' => 'Connection failed: ' . $conn->connect_error
    ]));
}

// Receive JSON data from Android Kotlin
$json_data = file_get_contents('php://input');
$order_data = json_decode($json_data, true);

// Validate incoming data
if (!$order_data) {
    die(json_encode([
        'status' => 'error', 
        'message' => 'Invalid JSON data'
    ]));
}

// Extract order details
$customer_id = $order_data['customer_id'];
$total_amount = $order_data['total_amount'];
$cart_items = $order_data['cart_items'];

// Start transaction
$conn->begin_transaction();

try {
    // Insert main order (removed payment_method)
    $order_status = 'Pending';
    $order_query = "INSERT INTO orders (customer_id, total_amount, status) 
                    VALUES (?, ?, ?)";
    $stmt_order = $conn->prepare($order_query);
    $stmt_order->bind_param("ids", $customer_id, $total_amount, $order_status);
    
    if (!$stmt_order->execute()) {
        throw new Exception("Failed to create order: " . $stmt_order->error);
    }
    
    // Get the last inserted order ID
    $order_id = $conn->insert_id;

    // Insert order items
    $item_query = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) 
                   VALUES (?, ?, ?, ?)";
    $stmt_items = $conn->prepare($item_query);

    foreach ($cart_items as $item) {
        $product_id = $item['product_id'];
        $quantity = $item['quantity'];
        $unit_price = $item['unit_price'];

        $stmt_items->bind_param("iids", $order_id, $product_id, $quantity, $unit_price);
        
        if (!$stmt_items->execute()) {
            throw new Exception("Failed to insert order item: " . $stmt_items->error);
        }
    }

    // Commit transaction
    $conn->commit();

    // Prepare success response
    $response = [
        'status' => 'success',
        'message' => 'Order processed successfully',
        'order_id' => $order_id
    ];

    echo json_encode($response);

} catch (Exception $e) {
    // Rollback transaction on error
    $conn->rollback();

    // Prepare error response
    $response = [
        'status' => 'error',
        'message' => $e->getMessage()
    ];

    echo json_encode($response);
}

// Close statements and connection
$stmt_order->close();
$stmt_items->close();
$conn->close();
?>