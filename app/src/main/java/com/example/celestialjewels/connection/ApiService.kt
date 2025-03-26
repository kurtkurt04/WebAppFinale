package com.example.celestialjewels.connection


import com.example.celestialjewels.models.Customers
import com.example.celestialjewels.models.OrderSubmission
import com.example.celestialjewels.models.ProductResponse
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type: application/json")

    @POST("register.php")  // Make sure this path is correct!
    fun addCustomer(@Body customer: Customers): Call<ResponseBody>

    @POST("login.php")
    fun login(@Body loginData: Customers): Call<Customers>

    @POST("getUser.php")
    fun getUserProfile(@Body request: Map<String, Int>): Call<Customers>

    @GET("fetchjewelry.php")
    fun fetchProducts(): Call<ProductResponse>

    @Headers("Content-Type: application/json")
    @POST("setOrder.php")
    fun submitOrder(@Body orderData: OrderSubmission): Call<ResponseBody>

    @GET("getOrder.php")
    fun getCustomerOrders(
        @Query("customer_id") customerId: Int
    ): Call<List<Map<String, Any>>>


}
