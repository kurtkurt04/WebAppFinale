package com.example.celestialjewels.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.celestialjewels.R

class OrderSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        val backButton = findViewById<Button>(R.id.backToHomeButton)
        backButton.setOnClickListener {
            finish() // Close activity and go back
        }
    }
}
