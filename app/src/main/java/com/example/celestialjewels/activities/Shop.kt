package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    // Declare variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var jewelryAdapter: JewelryAdapter
    private lateinit var searchView: SearchView

    // List to store all jewelry items
    private var fullJewelryList: List<Jewelry> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        // Initialize components
        recyclerView = findViewById(R.id.jewelryRecyclerView)
        searchView = findViewById(R.id.searchView)

        // Set up RecyclerView layout
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Load cart items
        CartManager.loadCartItems(this)

        // Fetch products and set up search
        fetchProducts()
        setupSearchView()
        setupCartButton()
        setupBottomNavigation()
    }

    private fun fetchProducts() {
        RetrofitClient.apiService.fetchProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    val productResponse = response.body()
                    if (productResponse?.status == "success") {
                        // Store full list of jewelry items
                        fullJewelryList = productResponse.products.map { product ->
                            product.copy(
                                localImageResource = R.drawable.one
                            )
                        }

                        // Set up initial adapter
                        jewelryAdapter = JewelryAdapter(fullJewelryList, this@Shop)
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

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // This method is called when the user submits the search
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Perform search when submit button is pressed
                query?.let { searchJewelry(it) }
                return true
            }

            // This method is called every time the text changes
            override fun onQueryTextChange(newText: String?): Boolean {
                // Perform search as user types
                newText?.let { searchJewelry(it) }
                return true
            }
        })
    }

    private fun searchJewelry(searchText: String) {
        // Filter the full list of jewelry based on search text
        val filteredList = fullJewelryList.filter { jewelry ->
            // Search by name (you can add more fields if needed)
            jewelry.name.contains(searchText, ignoreCase = true)
        }

        // Update the adapter with filtered list
        jewelryAdapter = JewelryAdapter(filteredList, this)
        recyclerView.adapter = jewelryAdapter

        // Show message if no items found
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No jewelry found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCartButton() {
        val cartButton = findViewById<ImageButton>(R.id.CartBtn)
        cartButton.setOnClickListener {
            startActivity(Intent(this, Cart::class.java))
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.action_notification

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    finish()
                    true
                }
                R.id.action_notification -> false
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

    override fun onPause() {
        super.onPause()
        CartManager.saveCartItems(this)
    }
}