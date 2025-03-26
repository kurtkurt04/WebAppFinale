package com.example.celestialjewels.models

import com.google.gson.annotations.SerializedName



data class Orders(
    @SerializedName("order_id")
    val orderId: Int? = null,

    @SerializedName("customer_id")
    val customerId: Int,

    @SerializedName("order_date")
    val orderDate: String? = null,

    @SerializedName("total_amount")
    val totalAmount: Double,

    @SerializedName("status")
    val status: OrderStatus = OrderStatus.PENDING
)


enum class OrderStatus {
    @SerializedName("Pending")
    PENDING,

    @SerializedName("Processing")
    PROCESSING,

    @SerializedName("For Claiming")
    FOR_CLAIMING,

    @SerializedName("Claimed")
    CLAIMED
}


data class OrderItems(
    @SerializedName("order_item_id")
    val orderItemId: Int? = null,

    @SerializedName("order_id")
    val orderId: Int,

    @SerializedName("product_id")
    val productId: Int,

    @SerializedName("product_name")  // Add this line to match PHP response
    val productName: String? = null,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("unit_price")
    val unitPrice: Double,

    @SerializedName("total_amount")
    val totalAmount: Double

)
// Complete order with items

data class CompleteOrder(
val order: Orders,
 val items: List<OrderItems>
)

data class OrderSubmission(
    val customer_id: Int,
    val total_amount: Double,
    val cart_items: List<OrderItemSubmission>
)

data class OrderItemSubmission(
    val product_id: Int,
    val quantity: Int,
    val unit_price: Double
)