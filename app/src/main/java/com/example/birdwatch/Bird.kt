package com.example.birdwatch

import android.R
data class Bird(
    var id: Long = 0L,
    var name: String = "",
    var description: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0,
    var imageUri: String? = null    //photo address as text, or null if no photo
) {
    fun validate() {
        require(name.isNotBlank()) {
            "Name must not be empty."
        }

        require(x.isFinite() && x in -90.0..90.0) {
            "Latitude must be between -90 and 90."
        }

        require(y.isFinite() && y in -180.0..180.0) {
            "Longitude must be between -180 and 180."
        }
    }
}