package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.celestialjewels.R
import com.example.celestialjewels.connection.RetrofitClient
import com.example.celestialjewels.managers.SessionManager
import com.example.celestialjewels.models.Customers
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        val changePassTextView = findViewById<TextView>(R.id.changedetails)
        val logoutButton = findViewById<TextView>(R.id.logoutButton)
        val shopLocationButton = findViewById<TextView>(R.id.addressButton)

        // User info TextViews
        val userNameTextView = findViewById<TextView>(R.id.userName)
        val userEmailTextView = findViewById<TextView>(R.id.userEmail)
        val userPhoneTextView = findViewById<TextView>(R.id.userPhone)

        // Fetch and display user information
        fetchUserProfile()

        // Set Profile as selected when the activity opens
        bottomNavigationView.selectedItemId = R.id.action_profile

        val btnDelivery = findViewById<MaterialButton>(R.id.btn_order_history)
        btnDelivery.setOnClickListener {
            val intent = Intent(this, activity_toclaim::class.java)
            startActivity(intent)
        }

        // Navigate to Change Password screen
        changePassTextView.setOnClickListener {
            val intent = Intent(this, ChangePass::class.java)
            startActivity(intent)
        }

        // Navigate to Shop Location screen
        shopLocationButton.setOnClickListener {
            val intent = Intent(this, ShopLocation::class.java)
            startActivity(intent)
        }

        // Handle Logout Button Click with Confirmation Popup
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Handle Bottom Navigation
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
                R.id.action_profile -> true // Already in Profile
                else -> false
            }
        }
    }

    private fun fetchUserProfile() {
        val customerId = SessionManager.getCustomerId(this)

        if (customerId != null) {
            // Create a map with the customer ID
            val requestBody = mapOf("customerId" to customerId)

            apiService.getUserProfile(requestBody).enqueue(object : Callback<Customers> {
                override fun onResponse(call: Call<Customers>, response: Response<Customers>) {
                    if (response.isSuccessful) {
                        val userProfile = response.body()

                        findViewById<TextView>(R.id.userName).text = userProfile?.username ?: "Unknown"
                        findViewById<TextView>(R.id.userEmail).text = userProfile?.email ?: "No email"
                        findViewById<TextView>(R.id.userPhone).text = userProfile?.phoneNum?.toString() ?: "No phone number"
                    } else {
                        Toast.makeText(this@Profile, "Failed to fetch profile: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Customers>, t: Throwable) {
                    Toast.makeText(this@Profile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // User not logged in, redirect to login page
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Logout")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { _, _ ->
            // Clear the session
            SessionManager.clearCustomerId(this)

            // Navigate to the Login Page
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            // If "Cancel" is pressed, dismiss the dialog
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}