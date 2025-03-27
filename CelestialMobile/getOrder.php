    <?php
    // Database connection
    require_once 'config.php';

    // Get customer ID from query parameter
    $customer_id = isset($_GET['customer_id']) ? intval($_GET['customer_id']) : 0;

    // Validate customer ID
    if ($customer_id <= 0) {
        die(json_encode(['error' => 'Invalid customer ID']));
    }

    // Fetch user's orders with product details
    $query = "
        SELECT 
            o.order_id, 
            o.order_date, 
            o.total_amount, 
            o.status,
            p.name AS product_name,
            oi.quantity,
            oi.unit_price
        FROM 
            orders o
        JOIN 
            order_items oi ON o.order_id = oi.order_id
        JOIN 
            products p ON oi.product_id = p.id
        WHERE 
            o.customer_id = ?
        ORDER BY 
            o.order_date DESC
    ";

    $stmt = $conn->prepare($query);
    $stmt->bind_param("i", $customer_id);
    $stmt->execute();
    $result = $stmt->get_result();

    // Group orders by order_id
    $orders = [];
    while ($row = $result->fetch_assoc()) {
        $order_id = $row['order_id'];
        if (!isset($orders[$order_id])) {
            $orders[$order_id] = [
                'order_date' => $row['order_date'],
                'total_amount' => $row['total_amount'],
                'status' => $row['status'],
                'items' => []
            ];
        }
        
        $orders[$order_id]['items'][] = [
            'product_name' => $row['product_name'],
            'quantity' => $row['quantity'],
            'unit_price' => $row['unit_price']
        ];
    }

    // Prepare response
    $response = [];
    foreach ($orders as $order_id => $order) {
        $order_items = [];
        foreach ($order['items'] as $item) {
            $order_items[] = [
                'product_name' => $item['product_name'],
                'quantity' => $item['quantity'],
                'unit_price' => $item['unit_price'],
                'subtotal' => $item['quantity'] * $item['unit_price']
            ];
        }
        
        $response[] = [
            'order_id' => $order_id,
            'order_date' => $order['order_date'],
            'total_amount' => $order['total_amount'],
            'status' => $order['status'],
            'items' => $order_items
        ];
    }

    // Return JSON response
    header('Content-Type: application/json');
    echo json_encode($response);

    // Close database connection
    $stmt->close();
    $conn->close();
    ?>