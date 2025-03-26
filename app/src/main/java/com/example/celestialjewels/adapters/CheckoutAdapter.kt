package com.example.celestialjewels.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.celestialjewels.R
import com.example.celestialjewels.models.Jewelry

class CheckoutAdapter(private val cartItems: List<Jewelry>) :
    RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {

    inner class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.jewelryImage)
        val nameText: TextView = itemView.findViewById(R.id.jewelryName)
        val priceText: TextView = itemView.findViewById(R.id.jewelryPrice)
        val quantityText: TextView = itemView.findViewById(R.id.itemQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        return CheckoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        val currentItem = cartItems[position]

        // Set image from Base64
        val bitmap = currentItem.getImageBitmap()
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap)
        } else {
            // Fallback to placeholder if image conversion fails
            holder.imageView.setImageResource(R.drawable.placeholder)
        }

        holder.nameText.text = currentItem.name
        holder.priceText.text = "₱${String.format("%.2f", currentItem.price)}"
        holder.quantityText.text = "Qty: ${currentItem.quantity}"
    }

    override fun getItemCount() = cartItems.size
}