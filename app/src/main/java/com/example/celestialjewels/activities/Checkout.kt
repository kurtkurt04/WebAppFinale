package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialjewels.R
import com.example.celestialjewels.adapters.CheckoutAdapter
import com.example.celestialjewels.connection.RetrofitClient
import com.example.celestialjewels.managers.CartManager
import com.example.celestialjewels.managers.SessionManager
import com.example.celestialjewels.models.Jewelry
import com.example.celestialjewels.models.OrderItemSubmission
import com.example.celestialjewels.models.OrderSubmission
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Checkout : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var confirmButton: Button
    private lateinit var checkoutAdapter: CheckoutAdapter
    private lateinit var cartItems: List<Jewelry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Initialize views
        totalPriceTextView = findViewById(R.id.totalPrice)
        confirmButton = findViewById(R.id.confirmOrderButton)
        recyclerView = findViewById(R.id.checkoutRecyclerView)

        // Get cart items from CartManager
        cartItems = CartManager.getUniqueItems()

        // Set total price
        val totalPrice = CartManager.getTotalPrice()
        totalPriceTextView.text = "Total: ₱${String.format("%.2f", totalPrice)}"

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        checkoutAdapter = CheckoutAdapter(cartItems)
        recyclerView.adapter = checkoutAdapter

        confirmButton.setOnClickListener {
            submitOrder()
        }
    }

    private fun submitOrder() {
        // Check if user is logged in
        val customerId = SessionManager.getCustomerId(this)
        if (customerId == null) {
            Toast.makeText(this, "Please log in to place an order", Toast.LENGTH_SHORT).show()
            // Redirect to login activity
            startActivity(Intent(this, LoginPage::class.java))
            return
        }

        // Prepare order items for submission
        val orderItems = cartItems.map { jewelry ->
            OrderItemSubmission(
                product_id = jewelry.id,
                quantity = jewelry.quantity,
                unit_price = jewelry.price
            )
        }

        // Create order submission data
        val orderSubmission = OrderSubmission(
            customer_id = customerId,  // Use the retrieved customer ID
            total_amount = CartManager.getTotalPrice(),
            cart_items = orderItems
        )

        // Call API to submit order
        val apiService = RetrofitClient.apiService
        apiService.submitOrder(orderSubmission).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Clear the cart after successful order
                    CartManager.clearCart(this@Checkout)

                    // Navigate to order success page
                    startActivity(Intent(this@Checkout, OrderSuccess::class.java))
                    finish()
                } else {
                    // Handle error
                    Toast.makeText(
                        this@Checkout,
                        "Order submission failed: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle network error
                Toast.makeText(
                    this@Checkout,
                    "Network error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}