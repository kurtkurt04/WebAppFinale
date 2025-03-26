package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialjewels.R
import com.example.celestialjewels.adapters.JewelryAdapter
import com.example.celestialjewels.connection.RetrofitClient
import com.example.celestialjewels.managers.CartManager
import com.example.celestialjewels.models.Jewelry
import com.example.celestialjewels.models.ProductResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Shop : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var jewelryAdapter: JewelryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        // Load persisted cart items when the activity is created
        CartManager.loadCartItems(this)

        recyclerView = findViewById(R.id.jewelryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        fetchProductsFromDatabase()
        setupCartButton()
        setupBottomNavigation()
    }

    private fun fetchProductsFromDatabase() {
        RetrofitClient.apiService.fetchProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse?.status == "success") {
                        val jewelryList = productResponse.products.map { product ->
                            product.copy(
                                localImageResource = R.drawable.one // Use the new property
                            )
                        }

                        jewelryAdapter = JewelryAdapter(jewelryList, this@Shop)
                        recyclerView.adapter = jewelryAdapter
                    } else {
                        Toast.makeText(this@Shop, "No products found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@Shop, "Failed to fetch products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Log.e("Shop", "Error fetching products", t)
                Toast.makeText(this@Shop, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupCartButton() {
        val cartButton = findViewById<ImageButton>(R.id.CartBtn)
        cartButton.setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
        }
    }

    fun addToCart(jewelry: Jewelry) {
        CartManager.addItem(jewelry)
        // Save cart items immediately after adding
        CartManager.saveCartItems(this)

        // Optional: Show a toast to confirm item added to cart
        Toast.makeText(this, "${jewelry.name} added to cart", Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Highlight the Shop tab
        bottomNavigationView.selectedItemId = R.id.action_notification

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    finish()
                    true
                }
                R.id.action_notification -> {
                    // Prevent reloading the same page
                    false
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

    // Optional: Override onPause to ensure cart is saved
    override fun onPause() {
        super.onPause()
        CartManager.saveCartItems(this)
    }
}