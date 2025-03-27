<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
date_default_timezone_set('Asia/Manila');
ini_set('display_errors', 1);

// Database connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "celestialdb";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die(json_encode([
        'status' => 'error', 
        'message' => 'Database connection failed: ' . $conn->connect_error
    ]));
}

// PHPMailer for sending emails
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';

class OTPManager {
    private $conn;

    public function __construct($db_connection) {
        $this->conn = $db_connection;
    }

    public function sendOTP($email) {
        // Validate email
        if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
            return [
                'status' => 'error',
                'message' => 'Invalid email format'
            ];
        }

        // Validate email exists in database
        $stmt = $this->conn->prepare("SELECT * FROM customers_tbl WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows == 0) {
            return [
                'status' => 'error',
                'message' => 'Email not found in our system'
            ];
        }

        // Generate OTP
        $otp = sprintf("%06d", mt_rand(1, 999999));
        
        // Prepare OTP storage
        $stmt = $this->conn->prepare("INSERT INTO otp_tokens (email, otp, expiry) 
                                      VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 30 MINUTE)) 
                                      ON DUPLICATE KEY UPDATE 
                                      otp = ?, 
                                      expiry = DATE_ADD(NOW(), INTERVAL 30 MINUTE)");
        $stmt->bind_param("sss", $email, $otp, $otp);
        
        // Execute OTP storage
        if (!$stmt->execute()) {
            return [
                'status' => 'error',
                'message' => 'Failed to store OTP: ' . $stmt->error
            ];
        }

        // Send Email
        $mail = new PHPMailer(true);
        try {
            // Server settings
            $mail->isSMTP();
            $mail->Host       = 'smtp.gmail.com';
            $mail->SMTPAuth   = true;
            $mail->Username   = 'deverafroilan9@gmail.com';  // Replace with your actual email
            $mail->Password   = 'yylp hnyn nran zcls';     // Replace with your App Password
            $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
            $mail->Port       = 587;

            // Recipients
            $mail->setFrom('deverafroilan9@gmail.com', 'Celestial Jewels');
            $mail->addAddress($email);

            // Content
            $mail->isHTML(true);
            $mail->Subject = 'Your OTP for Password Reset';
            $mail->Body    = "Your OTP is: <b>$otp</b><br>This OTP will expire in 30 minutes.";

            $mail->send();

            return [
                'status' => 'success',
                'message' => 'OTP sent successfully'
            ];
        } catch (Exception $e) {
            return [
                'status' => 'error',
                'message' => "OTP could not be sent. Mailer Error: {$mail->ErrorInfo}"
            ];
        }
    }

    public function verifyOTP($email, $otp) {
        // Validate input
        if (empty($email) || empty($otp)) {
            return [
                'status' => 'error',
                'message' => 'Email and OTP are required'
            ];
        }

        // Prepare statement to check OTP
        $stmt = $this->conn->prepare("SELECT * FROM otp_tokens 
                                      WHERE email = ? 
                                      AND otp = ? 
                                      AND expiry > NOW()");
        $stmt->bind_param("ss", $email, $otp);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            // OTP is valid, delete the used token
            $deleteStmt = $this->conn->prepare("DELETE FROM otp_tokens 
                                                WHERE email = ? AND otp = ?");
            $deleteStmt->bind_param("ss", $email, $otp);
            $deleteStmt->execute();

            return [
                'status' => 'success',
                'message' => 'OTP verified successfully'
            ];
        } else {
            return [
                'status' => 'error',
                'message' => 'Invalid or expired OTP'
            ];
        }
    }

    public function resetPassword($email, $newPassword) {
        // Validate input
        if (empty($email) || empty($newPassword)) {
            return [
                'status' => 'error',
                'message' => 'Email and new password are required'
            ];
        }

        // Validate email exists in database
        $stmt = $this->conn->prepare("SELECT * FROM customers_tbl WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows == 0) {
            return [
                'status' => 'error',
                'message' => 'Email not found in our system'
            ];
        }

        // Prepare statement to update password WITHOUT hashing
        $stmt = $this->conn->prepare("UPDATE customers_tbl 
                                      SET password = ? 
                                      WHERE email = ?");
        $stmt->bind_param("ss", $newPassword, $email);
        
        if ($stmt->execute()) {
            return [
                'status' => 'success',
                'message' => 'Password reset successfully'
            ];
        } else {
            return [
                'status' => 'error',
                'message' => 'Failed to reset password'
            ];
        }
    }
}

// Handle incoming requests
header('Content-Type: application/json');

$otpManager = new OTPManager($conn);

// Check if it's a POST request
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Retrieve action and email
    $action = isset($_POST['action']) ? $_POST['action'] : '';
    $email = isset($_POST['email']) ? $_POST['email'] : '';

    // Route the request
    switch ($action) {
        case 'send_otp':
            $result = $otpManager->sendOTP($email);
            echo json_encode($result);
            break;

        case 'verify_otp':
            $otp = isset($_POST['otp']) ? $_POST['otp'] : '';
            $result = $otpManager->verifyOTP($email, $otp);
            echo json_encode($result);
            break;

        case 'reset_password':
            $newPassword = isset($_POST['new_password']) ? $_POST['new_password'] : '';
            $result = $otpManager->resetPassword($email, $newPassword);
            echo json_encode($result);
            break;

        default:
            echo json_encode([
                'status' => 'error',
                'message' => 'Invalid action'
            ]);
    }
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid request method'
    ]);
}

// Close database connection
$conn->close();
?>