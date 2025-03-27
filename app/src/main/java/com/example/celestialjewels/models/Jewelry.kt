package com.example.celestialjewels.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import com.example.celestialjewels.R
import com.google.gson.annotations.SerializedName

data class Jewelry(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("selling_price") val price: Double,
    @SerializedName("image_path") val base64Image: String, // Base64 encoded image
    @SerializedName("stocks") val stocks: Int,
    @SerializedName("size") val size: String,
    var quantity: Int = 1,
    var localImageResource: Int = R.drawable.one
) : Parcelable {
    fun getImageBitmap(): Bitmap? {
        return try {
            // Check if base64Image is not empty
            if (base64Image.isNotEmpty()) {
                // Directly decode the Base64 string
                val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Jewelry", "Error decoding image: ${e.message}", e)
            null
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(base64Image)
        parcel.writeInt(stocks)
        parcel.writeString(size)
        parcel.writeInt(quantity)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Jewelry> {
        override fun createFromParcel(parcel: Parcel): Jewelry = Jewelry(parcel)
        override fun newArray(size: Int): Array<Jewelry?> = arrayOfNulls(size)
    }
}

// Response wrapper for products
data class ProductResponse(
    val status: String,
    val products: List<Jewelry>
)