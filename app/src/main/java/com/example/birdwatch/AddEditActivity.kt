package com.example.birdwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
class AddEditActivity: AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        nameInput = findViewById(R.id.nameField)
        descriptionInput = findViewById(R.id.descriptionField)
        xInput = findViewById(R.id.xField)
        yInput = findViewById(R.id.yField)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        editingId =
            intent.getLongExtra("id", -1L)
                .takeIf { it != -1L }
        if (editingId != null) {
            findViewById<TextView>(R.id.addEditTitle)
                .text = "Edit Bird"
            loadExistingBird(editingId!!)
        }

        saveButton.setOnClickListener { saveBird() }
        cancelButton.setOnClickListener { finish() }
    }

    private fun loadExistingBird(id: Long) {

        val bird = AppData.birds.findOne(id)

        if (bird == null) {
            Toast.makeText(
                this,
                "Bird not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        nameInput.setText(bird.name)
        descriptionInput.setText(bird.description)
        xInput.setText(bird.x.toString())
        yInput.setText(bird.y.toString())
    }

    private fun saveBird() {

        val name = nameInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (name.isEmpty()) {
            nameInput.error = "Title is required"
            return
        }

        val x = xInput.text.toString().toDoubleOrNull()
        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        val y = yInput.text.toString().toDoubleOrNull()
        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        if (editingId == null) {
            val bird = Bird(
                name = name,
                description = description,
                x = x,
                y = y
            )

            AppData.birds.create(bird)
            Toast.makeText(
                this,
                "Bird created",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            val bird = Bird(
                id = editingId!!,
                name = name,
                description = description,
                x = x,
                y = y
            )

            AppData.birds.update(bird)
            Toast.makeText(
                this,
                "Bird updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }

}