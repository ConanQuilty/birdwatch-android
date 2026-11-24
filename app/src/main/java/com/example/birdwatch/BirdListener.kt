package com.example.birdwatch


interface BirdListener {

    fun onBirdClick(bird: Bird)

    fun onBirdLongClick(bird: Bird): Boolean
}