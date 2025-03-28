package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.celestialjewels.R
import com.example.celestialjewels.connection.ApiService
import com.example.celestialjewels.connection.RetrofitClient
import com.example.celestialjewels.models.Customers
import com.example.celestialjewels.models.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Registration : AppCompatActivity() {
    private lateinit var etusername: EditText
    private lateinit var etpassword: EditText
    private lateinit var etconpassword: EditText
    private lateinit var etphoneNum: EditText
    private lateinit var etemail: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var conPasswordToggle: ImageView
    private val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val registerBttn = findViewById<Button>(R.id.Registration)
        val btnBackToLogin = findViewById<Button>(R.id.btnbacktologin) // Find the button
        etusername = findViewById(R.id.Etusername)
        etpassword = findViewById(R.id.Etpass)
        etconpassword = findViewById(R.id.Etconpass)
        etphoneNum = findViewById(R.id.EtPhoneNum)
        etemail = findViewById(R.id.EtEmail)
        passwordToggle = findViewById(R.id.imageView4)
        conPasswordToggle = findViewById(R.id.imageView7)

        // Set up password toggle
        setupPasswordToggle(etpassword, passwordToggle)
        setupPasswordToggle(etconpassword, conPasswordToggle)

        registerBttn.setOnClickListener {
            submitData()
        }

        // Handle "Already have an account?" button click
        btnBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun setupPasswordToggle(editText: EditText, toggleButton: ImageView) {
        var isPasswordVisible = false

        toggleButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                toggleButton.setImageResource(R.drawable.eye) // Change to open-eye icon
            } else {
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
                toggleButton.setImageResource(R.drawable.hidden) // Change to closed-eye icon
            }
            editText.setSelection(editText.text.length) // Keep cursor at the end
        }
    }

    private fun submitData() {
        val username = etusername.text.toString().trim()
        val password = etpassword.text.toString().trim()
        val confirmPassword = etconpassword.text.toString().trim()
        val phoneNum = etphoneNum.text.toString().trim()
        val email = etemail.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneNum.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!phoneNum.matches(Regex("\\d{11}"))) { // Ensure exactly 11 numeric digits
            Toast.makeText(this, "Phone number must be exactly 11 digits", Toast.LENGTH_SHORT).show()
            return
        }

        val customers = Customers(
            username = username,
            password = password,
            phoneNum = phoneNum,
            email = email
        )

        apiService.addCustomer(customers).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {

//                response.body().status === "failure"
//                response.isSuccessful === response.code() == 200
                if (response.body()?.status != "failure") {
                    Toast.makeText(this@Registration, "Customer added successfully!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Registration, LoginPage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("API_ERROR", "Error Code: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@Registration, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("API_ERROR", "Network Failure: ${t.message}")
                Toast.makeText(this@Registration, "Failed to connect to server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
