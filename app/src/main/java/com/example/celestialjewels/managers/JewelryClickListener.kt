package com.example.celestialjewels.managers

import com.example.celestialjewels.models.Jewelry

interface JewelryClickListener {
    fun onJewelryClick(jewelry: Jewelry)
    fun onAddToCartClick(jewelry: Jewelry)
}