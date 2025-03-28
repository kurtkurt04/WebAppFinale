package com.example.celestialjewels.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialjewels.R
import com.example.celestialjewels.adapters.OrderAdapter
import com.example.celestialjewels.connection.ApiService
import com.example.celestialjewels.connection.RetrofitClient
import com.example.celestialjewels.managers.SessionManager
import com.example.celestialjewels.models.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_toclaim : AppCompatActivity() {
    private lateinit var rvToClaim: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var spinnerOrderStatus: Spinner
    private var originalOrders: List<CompleteOrder> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_toclaim)

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        rvToClaim = findViewById(R.id.rvToClaim)
        rvToClaim.layoutManager = LinearLayoutManager(this)

        // Initialize Status Spinner
        spinnerOrderStatus = findViewById(R.id.spinnerOrderStatus)
        setupStatusSpinner()

        // Fetch and display orders
        val customerId = SessionManager.getCustomerId(this)
        customerId?.let {
            fetchOrders(it)
        }

        setupBottomNavigation()
    }

    private fun setupStatusSpinner() {
        val statusOptions = arrayOf("All", "Pending", "Processing", "For Claiming", "Claimed")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOrderStatus.adapter = adapter

        spinnerOrderStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = statusOptions[position]
                filterOrders(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun filterOrders(status: String) {
        val filteredOrders = when (status) {
            "All" -> originalOrders
            "Pending" -> originalOrders.filter { it.order.status == OrderStatus.PENDING }
            "Processing" -> originalOrders.filter { it.order.status == OrderStatus.PROCESSING }
            "For Claiming" -> originalOrders.filter { it.order.status == OrderStatus.FOR_CLAIMING }
            "Claimed" -> originalOrders.filter { it.order.status == OrderStatus.CLAIMED }
            else -> originalOrders
        }

        updateOrdersList(filteredOrders)
    }
    private fun fetchOrders(customerId: Int) {
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)
        val call = apiService.getCustomerOrders(customerId)

        call.enqueue(object : Callback<List<Map<String, Any>>> {
            override fun onResponse(
                call: Call<List<Map<String, Any>>>,
                response: Response<List<Map<String, Any>>>
            ) {
                if (response.isSuccessful) {
                    val ordersData = response.body()
                    val completeOrders = processOrdersData(ordersData ?: emptyList())
                    originalOrders = completeOrders
                    updateOrdersList(completeOrders)
                } else {
                    Log.e("FetchOrders", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                Log.e("FetchOrders", "Network error: ${t.message}")
            }
        })
    }





    private fun updateOrdersList(orders: List<CompleteOrder>) {
        if (orders.isEmpty()) {
            findViewById<TextView>(R.id.tvEmptyState).visibility = View.VISIBLE
            rvToClaim.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.tvEmptyState).visibility = View.GONE
            rvToClaim.visibility = View.VISIBLE

            orderAdapter = OrderAdapter(orders, this)
            rvToClaim.adapter = orderAdapter
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.OrHistory

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
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

                R.id.action_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    finish()
                    true
                }
                R.id.OrHistory -> {
                    // Current activity, do nothing
                    true
                }

                else -> false
            }
        }
    }




    private fun processOrdersData(ordersData: List<Map<String, Any>>): List<CompleteOrder> {
        return ordersData.map { orderMap ->
            val orderId = (orderMap["order_id"] as? Number)?.toInt() ?: 0
            val orderDate = orderMap["order_date"] as? String ?: ""
            val apiTotalAmount = (orderMap["total_amount"] as? Number)?.toDouble() ?: 0.0

            val status = when (orderMap["status"] as? String) {
                "Pending" -> OrderStatus.PENDING
                "Processing" -> OrderStatus.PROCESSING
                "For Claiming" -> OrderStatus.FOR_CLAIMING
                "Claimed" -> OrderStatus.CLAIMED
                else -> OrderStatus.PENDING
            }

            val items = (orderMap["items"] as? List<Map<String, Any>>)?.map { itemMap ->
                val productId = (itemMap["product_id"] as? Number)?.toInt() ?: 0
                val productName = itemMap["product_name"] as? String ?: "Unknown Product"
                val quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 1
                val unitPrice = itemMap["unit_price"].toString().toDoubleOrNull() ?: 0.0
                val totalAmount = (itemMap["total_amount"] as? Number)?.toDouble()
                    ?: (quantity * unitPrice)

                OrderItems(
                    orderItemId = null,
                    orderId = orderId,
                    productId = productId,
                    productName = productName,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalAmount = totalAmount
                )
            } ?: emptyList()

            val calculatedTotal = items.sumOf { it.totalAmount }.takeIf { it > 0.0 }
                ?: apiTotalAmount

            CompleteOrder(
                order = Orders(
                    orderId = orderId,
                    customerId = SessionManager.getCustomerId(this) ?: 0,
                    orderDate = orderDate,
                    totalAmount = calculatedTotal,
                    status = status
                ),
                items = items
            )
        }
    }
}