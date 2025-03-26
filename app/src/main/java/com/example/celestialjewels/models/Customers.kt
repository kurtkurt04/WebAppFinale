package com.example.celestialjewels.models

import com.google.gson.annotations.SerializedName

data class Customers(
    @SerializedName("customer_id")
    val customerId: Int? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("phone_number")
    val phoneNum: Int,

    @SerializedName("email")
    val email: String
)
