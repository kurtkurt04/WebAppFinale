package com.example.celestialjewels.adapters

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialjewels.R
import com.example.celestialjewels.models.CompleteOrder
import com.example.celestialjewels.models.OrderStatus

class OrderAdapter(private val orderList: List<CompleteOrder>, private val context: Context) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvTotalItems: TextView = itemView.findViewById(R.id.tvTotalItems)
        val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        val tvHint: TextView = itemView.findViewById(R.id.tvHint)
        val orderCard: CardView = itemView.findViewById(R.id.orderCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val completeOrder = orderList[position]
        val order = completeOrder.order
        val items = completeOrder.items

        holder.tvOrderId.text = "Order #${order.orderId}"
        holder.tvOrderDate.text = "Order Date: ${order.orderDate}"
        holder.tvProductName.text = "Product: ${items.firstOrNull()?.productName ?: "N/A"} (and more)"
        holder.tvTotalItems.text = "Items: ${items.size}"
        holder.tvTotalAmount.text = "Total: $${order.totalAmount}"
        holder.tvHint.text = "Tap to view details" // Hint for clickable card

        // Apply color coding based on order status
        val statusColor = when (order.status) {
            OrderStatus.PENDING -> ContextCompat.getColor(context, R.color.status_red)
            OrderStatus.PROCESSING -> ContextCompat.getColor(context, R.color.status_yellow)
            OrderStatus.FOR_CLAIMING -> ContextCompat.getColor(context, R.color.status_green)
            OrderStatus.CLAIMED -> ContextCompat.getColor(context, R.color.status_blue)
        }
        holder.tvOrderStatus.setTextColor(statusColor)
        holder.tvOrderStatus.text = order.status.name

        // Highlight card to indicate it's clickable
        holder.orderCard.setOnClickListener {
            showOrderPopup(completeOrder, holder.orderCard)
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    private fun showOrderPopup(order: CompleteOrder, anchorView: View) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.order_details, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val tvPopupOrderId: TextView = popupView.findViewById(R.id.tvPopupOrderId)
        val tvPopupStatus: TextView = popupView.findViewById(R.id.tvPopupStatus)
        val tvPopupDate: TextView = popupView.findViewById(R.id.tvPopupDate)
        val tvPopupItems: TextView = popupView.findViewById(R.id.tvPopupItems)
        val tvPopupTotal: TextView = popupView.findViewById(R.id.tvPopupTotal)
        val btnClose: Button = popupView.findViewById(R.id.btnClosePopup)

        val orderData = order.order
        val itemsText = order.items.joinToString("\n") { "${it.productName} x${it.quantity} - $${it.totalAmount}" }

        tvPopupOrderId.text = "Order #${orderData.orderId}"
        tvPopupStatus.text = "Status: ${orderData.status.name}"
        tvPopupDate.text = "Order Date: ${orderData.orderDate}"
        tvPopupItems.text = "Items:\n$itemsText"
        tvPopupTotal.text = "Total: $${orderData.totalAmount}"

        btnClose.setOnClickListener { popupWindow.dismiss() }
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }
}