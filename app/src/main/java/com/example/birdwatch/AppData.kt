package com.example.birdwatch
import android.content.Context
import java.io.File

object AppData {
    lateinit var birds: BirdStore
        private set

    fun initialize(context: Context) {
        if (::birds.isInitialized) return
        val file = File(context.filesDir, "bird_list.json")

        birds = BirdStore(file)
    }
}