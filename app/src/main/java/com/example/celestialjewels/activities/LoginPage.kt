package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameField = findViewById<EditText>(R.id.Etusername)
        val passwordField = findViewById<EditText>(R.id.Etpass)
        val eyeToggle = findViewById<ImageView>(R.id.eyeToggle)
        val loginBttn = findViewById<Button>(R.id.Login)
        val forgotPasswordBttn = findViewById<TextView>(R.id.fgtpass)
        val registerBttn = findViewById<TextView>(R.id.Register)

        // Toggle password visibility
        eyeToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                eyeToggle.setImageResource(R.drawable.eye) // Change to open eye icon
            } else {
                passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                eyeToggle.setImageResource(R.drawable.hidden) // Change to closed eye icon
            }
            passwordField.setSelection(passwordField.text.length) // Keep cursor at the end
        }

        // Handle login button click
        loginBttn.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginData = Customers(
                username = username,
                password = password,
                phoneNum = " ",  // Placeholder value
                email = ""     // Placeholder value
            )

            apiService.login(loginData).enqueue(object : Callback<Customers> {
                override fun onResponse(call: Call<Customers>, response: Response<Customers>) {
                    if (response.isSuccessful) {
                        val customer = response.body()
                        if (customer?.customerId != null) {
                            SessionManager.saveCustomerId(this@LoginPage, customer.customerId)
                            val intent = Intent(this@LoginPage, HomePage::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginPage, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
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