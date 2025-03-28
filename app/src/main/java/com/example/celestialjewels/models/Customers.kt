package com.example.celestialjewels.models

import com.google.gson.annotations.SerializedName

data class RegisterResponse (
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("customer_id")
    val customerId: Int?
)

data class Customers(
    @SerializedName("customer_id")
    val customerId: Int? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("phone_number")
    val phoneNum: String,

    @SerializedName("email")
    val email: String
)
