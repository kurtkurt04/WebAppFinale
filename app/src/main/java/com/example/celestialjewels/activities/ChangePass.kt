package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.celestialjewels.R
import com.example.celestialjewels.connection.RetrofitClient
import com.example.celestialjewels.managers.SessionManager
import com.example.celestialjewels.models.Customers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePass : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNum: EditText
    private lateinit var submitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)

        // Initialize Views
        etUsername = findViewById(R.id.Etusername)
        etEmail = findViewById(R.id.EtEmail)
        etPhoneNum = findViewById(R.id.EtPhoneNum)
        submitBtn = findViewById(R.id.passBackbtn)

        // Prefill existing user details
        loadUserDetails()

        // Submit Button - Update User Details
        submitBtn.setOnClickListener {
            validateAndUpdateUserDetails()
        }
    }

    private fun loadUserDetails() {
        // Retrieve user details from intent or previous activity
        val username = intent.getStringExtra("USERNAME") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""
        val phoneNum = intent.getStringExtra("PHONE_NUMBER") ?: ""

        etUsername.setText(username)
        etEmail.setText(email)
        etPhoneNum.setText(phoneNum)
    }

    private fun validateAndUpdateUserDetails() {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phoneNum = etPhoneNum.text.toString().trim()

        // Basic validation
        when {
            username.isEmpty() -> {
                etUsername.error = "Username cannot be empty"
                return
            }
            email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Invalid email address"
                return
            }
            phoneNum.isEmpty() || phoneNum.length <11 -> {
                etPhoneNum.error = "Invalid phone number"
                return
            }
            else -> performUserUpdate(username, email, phoneNum)
        }
    }

    private fun performUserUpdate(username: String, email: String, phoneNum: String) {
        // Get current user's ID from SessionManager
        val customerId = SessionManager.getCustomerId(this)

        if (customerId == null) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Create Customers object for update
        val updatedUser = Customers(
            customerId = customerId,
            username = username,
            email = email,
            phoneNum = phoneNum,
            password = "" // Not updating password here
        )

        // Call API to update user details
        RetrofitClient.apiService.updateUserDetails(updatedUser)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        try {
                            val jsonResponse = JSONObject(response.body()?.string())

                            if (jsonResponse.getBoolean("success")) {
                                // Show success dialog
                                showSuccessDialog(username, email, phoneNum)
                            } else {
                                // Show error from server
                                Toast.makeText(
                                    this@ChangePass,
                                    jsonResponse.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@ChangePass,
                                "Error parsing response: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ChangePass,
                            "Update failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@ChangePass,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showSuccessDialog(username: String, email: String, phoneNum: String) {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage("User details updated successfully!")
            .setPositiveButton("OK") { _, _ ->
                // Create intent to return updated details
                val returnIntent = Intent(this, Profile::class.java)
                returnIntent.putExtra("USERNAME", username)
                returnIntent.putExtra("EMAIL", email)
                returnIntent.putExtra("PHONE_NUMBER", phoneNum)
                startActivity(returnIntent)
                finish()
            }
            .show()
    }
}