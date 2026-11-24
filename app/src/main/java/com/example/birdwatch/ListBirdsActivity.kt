package com.example.birdwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class ListBirdsActivity: AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BirdAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_bird_list)
        Timber.d("ListBirdsActivity created")

        recyclerView = findViewById(R.id.birdsRecyclerView)

        adapter = BirdAdapter(
            allBirds = AppData.birds.findAll(),

            listener = object : BirdListener {
                override fun onBirdClick(bird: Bird) {
                    editBird(bird)
                }

                override fun onBirdLongClick(bird: Bird): Boolean {
                    confirmDelete(bird)
                    return true
                }

            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val returnButton = findViewById<Button>(R.id.returnListButton)

        returnButton.setOnClickListener {
            Timber.d("Return button pressed")
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
        Timber.d("Editing bird id=${bird.id}, name=${bird.name}")

        val intent = Intent(this, AddEditActivity::class.java)

        intent.putExtra("id", bird.id)

        startActivity(intent)
    }

    private fun deleteBird(bird: Bird) {
        Timber.i("Deleting bird id=${bird.id}")

        AppData.birds.delete(bird.id)

        adapter.updateBirds(
            AppData.birds.findAll()
        )
    }

    private fun confirmDelete(bird: Bird) {
        Timber.d("Asking to confirm delete of bird id=${bird.id}")
        AlertDialog.Builder(this)
            .setTitle("Delete this bird?")
            .setMessage("\"${bird.name}\" will be removed. This can't be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteBird(bird)
            }                                   // {_, _ ->} Ignores the a,b vals that the setNegativeButton has to add the Timber log
            .setNegativeButton("Cancel") { _, _ -> Timber.d("Delete cancelled for bird id=${bird.id}")}
            .show()
    }

}