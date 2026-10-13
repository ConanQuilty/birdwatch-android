package com.example.birdwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListBirdsActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BirdAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bird_list)

        recyclerView = findViewById(R.id.birdsRecyclerView)

        adapter = BirdAdapter(
            allBirds = AppData.birds.findAll(),

            onEdit = { bird ->
                editBird(bird)
            },

            onDelete = { bird ->
                deleteBird(bird)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val returnButton = findViewById<Button>(R.id.returnListButton)

        returnButton.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {

            adapter.updateBirds(
                AppData.birds.findAll()
            )
        }
    }

    private fun editBird(bird: Bird) {

        val intent = Intent(this, AddEditActivity::class.java)

        intent.putExtra("id", bird.id)

        startActivity(intent)
    }

    private fun deleteBird(bird: Bird) {

        AppData.birds.delete(bird.id)

        adapter.updateBirds(
            AppData.birds.findAll()
        )
    }

}