package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.celestialjewels.models.Customers
import com.example.celestialjewels.R
import com.example.celestialjewels.connection.RetrofitClient
import com.example.celestialjewels.managers.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : AppCompatActivity() {
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameField = findViewById<EditText>(R.id.Etusername)
        val passwordField = findViewById<EditText>(R.id.Etpass)
        val loginBttn = findViewById<Button>(R.id.Login)
        val forgotPasswordBttn = findViewById<TextView>(R.id.fgtpass)
        val registerBttn = findViewById<TextView>(R.id.Register)

        // Handle login button click
        loginBttn.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a Customers object to send to the PHP script
            val loginData = Customers(
                username = username,
                password = password,
                phoneNum = 0,  // Placeholder value
                email = ""     // Placeholder value
            )

            // Make the login request using Retrofit
            apiService.login(loginData).enqueue(object : Callback<Customers> {
                override fun onResponse(call: Call<Customers>, response: Response<Customers>) {
                    if (response.isSuccessful) {
                        val customer = response.body()
                        if (customer?.customerId != null) {
                            // Save customer ID to SessionManager
                            SessionManager.saveCustomerId(this@LoginPage, customer.customerId)

                            // Login success, navigate to HomePage
                            val intent = Intent(this@LoginPage, HomePage::class.java)
                            startActivity(intent)
                            finish() // Close login activity
                        } else {
                            // Login failed, show error message
                            Toast.makeText(this@LoginPage, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Server error
                        Toast.makeText(this@LoginPage, "Server error. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Customers>, t: Throwable) {
                    Toast.makeText(this@LoginPage, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Navigate to Forgot Password page
        forgotPasswordBttn.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Registration page
        registerBttn.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }
}