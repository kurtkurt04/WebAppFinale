package com.example.celestialjewels.managers

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "CelestialJewelsPref"
    private const val KEY_CUSTOMER_ID = "customer_id"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveCustomerId(context: Context, customerId: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(KEY_CUSTOMER_ID, customerId)
        editor.apply()
    }

    fun getCustomerId(context: Context): Int? {
        val prefs = getSharedPreferences(context)
        return if (prefs.contains(KEY_CUSTOMER_ID)) {
            val customerId = prefs.getInt(KEY_CUSTOMER_ID, -1)
            if (customerId != -1) customerId else null
        } else {
            null
        }
    }

    fun clearCustomerId(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(KEY_CUSTOMER_ID)
        editor.apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getCustomerId(context) != null
    }
}