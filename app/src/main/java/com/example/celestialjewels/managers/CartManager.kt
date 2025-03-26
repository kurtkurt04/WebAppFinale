package com.example.celestialjewels.managers

import android.content.Context
import com.example.celestialjewels.models.Jewelry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {
    private val cartItems = mutableListOf<Jewelry>()

    fun init(context: Context) {
        loadCartItems(context)
    }

    fun addItem(newItem: Jewelry) {
        val existingItem = cartItems.find { it.id == newItem.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            val itemToAdd = newItem.copy(quantity = 1)
            cartItems.add(itemToAdd)
        }
    }

    fun getUniqueItems(): List<Jewelry> = cartItems.toList()

    fun clearCart(context: Context) {
        cartItems.clear()

        // Clear saved cart data from SharedPreferences
        val prefs = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove("cart_items")
        editor.apply()
    }


    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.price * it.quantity }
    }

    fun removeItem(item: Jewelry) {
        cartItems.removeAll { it.id == item.id }
    }

    // Save cart items to SharedPreferences
    fun saveCartItems(context: Context) {
        val prefs = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Convert cart items to JSON string
        val gson = Gson()
        val cartItemsJson = gson.toJson(cartItems)

        editor.putString("cart_items", cartItemsJson)
        editor.apply()
    }

    // Load cart items from SharedPreferences
    fun loadCartItems(context: Context) {
        val prefs = context.getSharedPreferences("CartPrefs", Context.MODE_PRIVATE)
        val cartItemsJson = prefs.getString("cart_items", null)

        if (cartItemsJson != null) {
            val gson = Gson()
            val type = object : TypeToken<MutableList<Jewelry>>() {}.type
            val savedCartItems: MutableList<Jewelry> = gson.fromJson(cartItemsJson, type)

            // Clear existing items and add saved items
            cartItems.clear()
            cartItems.addAll(savedCartItems)
        }
    }
}