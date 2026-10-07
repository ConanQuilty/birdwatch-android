package com.example.birdwatch

import android.R

data class Bird (
    var id: Long = 0L,
    var name: String = "",
    var description: String = "",
    var x: Double = 0.0,
    var y: Double = 0.0
)