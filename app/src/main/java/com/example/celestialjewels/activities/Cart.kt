package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialjewels.adapters.CartAdapter
import com.example.celestialjewels.models.Jewelry
import com.example.celestialjewels.R
import com.example.celestialjewels.managers.CartManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class Cart : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalText: TextView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<Jewelry>
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Load cart items when the activity is created
        CartManager.loadCartItems(this)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalText = findViewById(R.id.totalText)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize cart items
        cartItems = CartManager.getUniqueItems().toMutableList()

        // Initialize adapter with a function to update total
        cartAdapter = CartAdapter(cartItems) {
            updateTotal()
            // Save cart items whenever the total is updated
            CartManager.saveCartItems(this)
        }
        recyclerView.adapter = cartAdapter

        updateTotal()

        val checkoutButton: Button = findViewById(R.id.btncart1)
        checkoutButton.setOnClickListener {
            navigateToCheckout()
        }

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        refreshCart()
    }

    private fun refreshCart() {
        // Clear and repopulate cart items
        cartItems.clear()
        cartItems.addAll(CartManager.getUniqueItems())

        // Notify adapter of data changes
        cartAdapter.notifyDataSetChanged()

        // Update total
        updateTotal()
    }

    private fun updateTotal() {
        val total = CartManager.getTotalPrice()
        totalText.text = "Total: ₱${String.format("%.2f", total)}"
    }

    private fun navigateToCheckout() {
        val intent = Intent(this, Checkout::class.java)
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.action_notification
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    finish()
                    true
                }
                R.id.action_notification -> {
                    startActivity(Intent(this, Shop::class.java))
                    finish()
                    true
                }
                R.id.action_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}