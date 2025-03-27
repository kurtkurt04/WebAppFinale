package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.celestialjewels.R
import com.example.celestialjewels.connection.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity() {
    // UI Components
    private lateinit var emailEditText: EditText
    private lateinit var otpEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var requestOtpButton: Button
    private lateinit var verifyOtpButton: Button
    private lateinit var resetPasswordButton: Button
    private lateinit var progressBar: ProgressBar

    // State Variables
    private var currentEmail = ""
    private var currentState = State.EMAIL_INPUT

    // Enum to manage UI states
    enum class State {
        EMAIL_INPUT,
        OTP_VERIFICATION,
        PASSWORD_RESET
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password2)

        // Initialize Views
        initializeViews()

        // Set up Click Listeners
        setupClickListeners()

        // Set up Password Visibility Toggle
        setupPasswordToggle() // 🔹 CALL THIS FUNCTION

        // Initial UI State
        updateUIState(State.EMAIL_INPUT)
    }


    private fun initializeViews() {
        emailEditText = findViewById(R.id.editTextEmail)
        otpEditText = findViewById(R.id.editTextOTP)
        newPasswordEditText = findViewById(R.id.editTextNewPassword)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword)
        requestOtpButton = findViewById(R.id.buttonRequestOTP)
        verifyOtpButton = findViewById(R.id.buttonVerifyOTP)
        resetPasswordButton = findViewById(R.id.buttonResetPassword)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        requestOtpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (validateEmail(email)) {
                sendOTP(email)
            }
        }
        findViewById<Button>(R.id.backbutton).setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish() // Ensures the current activity is closed
        }

        verifyOtpButton.setOnClickListener {
            val otp = otpEditText.text.toString().trim()

            if (validateOTP(otp)) {
                verifyOTP(currentEmail, otp)
            }
        }

        resetPasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (validatePasswords(newPassword, confirmPassword)) {
                resetPassword(currentEmail, newPassword)
            }
        }
    }

    private fun sendOTP(email: String) {
        showLoading(true)
        RetrofitClient.apiService.sendOTP(email = email).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)
                if (response.isSuccessful) {
                    try {
                        val responseString = response.body()?.string()
                        val jsonResponse = org.json.JSONObject(responseString)

                        if (jsonResponse.getString("status") == "success") {
                            currentEmail = email
                            updateUIState(State.OTP_VERIFICATION)
                            Toast.makeText(this@ForgotPasswordActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ForgotPasswordActivity, "Parsing error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@ForgotPasswordActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyOTP(email: String, otp: String) {
        showLoading(true)
        RetrofitClient.apiService.verifyOTP(email = email, otp = otp).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)
                if (response.isSuccessful) {
                    try {
                        val responseString = response.body()?.string()
                        val jsonResponse = org.json.JSONObject(responseString)

                        if (jsonResponse.getString("status") == "success") {
                            updateUIState(State.PASSWORD_RESET)
                            Toast.makeText(this@ForgotPasswordActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ForgotPasswordActivity, "Parsing error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@ForgotPasswordActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resetPassword(email: String, newPassword: String) {
        showLoading(true)
        RetrofitClient.apiService.resetPassword(email = email, newPassword = newPassword).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                showLoading(false)
                if (response.isSuccessful) {
                    try {
                        val responseString = response.body()?.string()
                        val jsonResponse = org.json.JSONObject(responseString)

                        if (jsonResponse.getString("status") == "success") {
                            Toast.makeText(this@ForgotPasswordActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                            finish() // Close the activity after successful password reset
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, jsonResponse.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@ForgotPasswordActivity, "Parsing error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Server error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@ForgotPasswordActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUIState(state: State) {
        currentState = state
        when (state) {
            State.EMAIL_INPUT -> {
                emailEditText.visibility = View.VISIBLE
                otpEditText.visibility = View.GONE
                newPasswordEditText.visibility = View.GONE
                confirmPasswordEditText.visibility = View.GONE

                requestOtpButton.visibility = View.VISIBLE
                verifyOtpButton.visibility = View.GONE
                resetPasswordButton.visibility = View.GONE
            }
            State.OTP_VERIFICATION -> {
                emailEditText.visibility = View.GONE
                otpEditText.visibility = View.VISIBLE
                newPasswordEditText.visibility = View.GONE
                confirmPasswordEditText.visibility = View.GONE

                requestOtpButton.visibility = View.GONE
                verifyOtpButton.visibility = View.VISIBLE
                resetPasswordButton.visibility = View.GONE
            }
            State.PASSWORD_RESET -> {
                emailEditText.visibility = View.GONE
                otpEditText.visibility = View.GONE
                newPasswordEditText.visibility = View.VISIBLE
                confirmPasswordEditText.visibility = View.VISIBLE

                requestOtpButton.visibility = View.GONE
                verifyOtpButton.visibility = View.GONE
                resetPasswordButton.visibility = View.VISIBLE
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Disable buttons while loading
        requestOtpButton.isEnabled = !isLoading
        verifyOtpButton.isEnabled = !isLoading
        resetPasswordButton.isEnabled = !isLoading
    }

    // Validation Methods
    private fun validateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            emailEditText.error = "Email cannot be empty"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email format"
            false
        } else {
            true
        }
    }

    private fun validateOTP(otp: String): Boolean {
        return if (otp.isEmpty()) {
            otpEditText.error = "OTP cannot be empty"
            false
        } else if (otp.length != 6) {
            otpEditText.error = "OTP must be 6 digits"
            false
        } else {
            true
        }
    }

    private fun validatePasswords(newPassword: String, confirmPassword: String): Boolean {
        return when {
            newPassword.isEmpty() -> {
                newPasswordEditText.error = "Password cannot be empty"
                false
            }
            newPassword.length < 6 -> {
                newPasswordEditText.error = "Password must be at least 6 characters"
                false
            }
            confirmPassword.isEmpty() -> {
                confirmPasswordEditText.error = "Confirm password cannot be empty"
                false
            }
            newPassword != confirmPassword -> {
                confirmPasswordEditText.error = "Passwords do not match"
                false
            }
            else -> true
        }
    }
    private fun setupPasswordToggle() {
        val eyeIconNewPassword = findViewById<ImageView>(R.id.imageView5)
        val eyeIconConfirmPassword = findViewById<ImageView>(R.id.imageView6)

        var isPasswordVisible = false
        var isConfirmPasswordVisible = false

        eyeIconNewPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(newPasswordEditText, isPasswordVisible, eyeIconNewPassword)
        }

        eyeIconConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(confirmPasswordEditText, isConfirmPasswordVisible, eyeIconConfirmPassword)
        }
    }

    private fun togglePasswordVisibility(editText: EditText, isVisible: Boolean, eyeIcon: ImageView) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            eyeIcon.setImageResource(R.drawable.eye) // Change to open eye icon
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            eyeIcon.setImageResource(R.drawable.hidden) // Change to closed eye icon
        }
        editText.setSelection(editText.text.length) // Maintain cursor position
    }


}