package com.example.celestialjewels.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.celestialjewels.R
import com.google.android.material.button.MaterialButton

class ShopLocation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoplocation)


        findViewById<MaterialButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        findViewById<MaterialButton>(R.id.openMapsButton).setOnClickListener {
            val latitude = 16.0662167

            val longitude = 120.4092208
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(Celestial+Jewels)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }
}