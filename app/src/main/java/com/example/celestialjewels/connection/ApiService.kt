package com.example.celestialjewels.connection


import com.example.celestialjewels.models.Customers
import com.example.celestialjewels.models.OrderSubmission
import com.example.celestialjewels.models.ProductResponse
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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

    @Headers("Content-Type: application/json")
    @POST("UpdateUser.php")
    fun updateUserDetails(@Body userData: Customers): Call<ResponseBody>


    @FormUrlEncoded
    @POST("sendOtp.php")
    fun sendOTP(
        @Field("email") email: String,
        @Field("action") action: String = "send_otp"
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("sendOtp.php")
    fun verifyOTP(
        @Field("email") email: String,
        @Field("otp") otp: String,
        @Field("action") action: String = "verify_otp"
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("sendOtp.php")
    fun resetPassword(
        @Field("email") email: String,
        @Field("new_password") newPassword: String,
        @Field("action") action: String = "reset_password"
    ): Call<ResponseBody>
}









