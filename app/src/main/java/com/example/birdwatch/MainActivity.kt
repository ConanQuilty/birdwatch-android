package com.example.birdwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listBirdsButton = findViewById<Button>(R.id.listButton)
        val addBirdButton = findViewById<Button>(R.id.addButton)

        listBirdsButton.setOnClickListener {
            Timber.d("List birds button pressed")
            startActivity(
                Intent(this, ListBirdsActivity::class.java)
            )
        }

        addBirdButton.setOnClickListener {
            Timber.d("Add bird button pressed")
            startActivity(
                Intent(this, AddEditActivity::class.java)
            )
        }

        AppData.initialize(this)
    }
}