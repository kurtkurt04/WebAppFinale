package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialjewels.R
import com.example.celestialjewels.adapters.CartAdapter
import com.example.celestialjewels.managers.CartManager
import com.example.celestialjewels.models.Jewelry
import com.google.android.material.bottomnavigation.BottomNavigationView

class Cart : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var checkoutButton: Button
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<Jewelry>
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Load cart items when the activity is created
        CartManager.loadCartItems(this)

        recyclerView = findViewById(R.id.cartRecyclerView)
        checkoutButton = findViewById(R.id.btncart1)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize cart items
        cartItems = CartManager.getUniqueItems().toMutableList()

        // Initialize adapter with a function to update total
        cartAdapter = CartAdapter(cartItems, this) {
            // Callback after item changes
            CartManager.saveCartItems(this)
        }
        recyclerView.adapter = cartAdapter

        // Setup checkout button click listener
        checkoutButton.setOnClickListener {
            navigateToCheckout()
        }

        setupBottomNavigation()
    }

    private fun navigateToCheckout() {
        // Check if cart is empty before navigating
        if (cartItems.isEmpty()) {
            // Show a toast message if cart is empty
            Toast.makeText(this, "Your cart is empty. Please add items to checkout.", Toast.LENGTH_SHORT).show()
            return
        }

        // If cart is not empty, proceed to checkout
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
                R.id.OrHistory -> {
                    startActivity(Intent(this, activity_toclaim::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}