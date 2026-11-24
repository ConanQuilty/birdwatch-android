package com.example.birdwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts

class AddEditActivity: AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private lateinit var imagePreview: ImageView
    private var selectedImageUri: Uri? = null

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

        imagePreview = findViewById(R.id.imageViewAdd)
        val imagePicker =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    // Keep access to the document URI for later use, when supported.
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {
                        // Some providers may not grant persistable access.
                    }

                    selectedImageUri = uri
                    imagePreview.setImageURI(uri)
                }
            }
        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        editingId =
            intent.getLongExtra("id", -1L)
                .takeIf { it != -1L }
        if (editingId != null) {
            Timber.d("AddEditActivity opened in ADD mode")
            findViewById<TextView>(R.id.addEditTitle)
                .text = "Edit Bird"
            loadExistingBird(editingId!!)
        } else {
            Timber.d("AddEditActivity opened in EDIT mode for bird id=$editingId")
        }

        saveButton.setOnClickListener { saveBird() }
        cancelButton.setOnClickListener {
            Timber.d("Add/edit cancelled")
            finish()
        }
    }

    private fun loadExistingBird(id: Long) {

        val bird = AppData.birds.findOne(id)

        if (bird == null) {
            Timber.w("Tried to edit bird id=$id but it wasn't found")
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
        selectedImageUri = bird.imageUri?.let { Uri.parse(it) }

        if (selectedImageUri != null) {
            imagePreview.setImageURI(selectedImageUri)
        } else {
            imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)  // placeholder
        }
    }

    private fun saveBird() {

        val name = nameInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (name.isEmpty()) {
            Timber.w("Validation failed: name is empty")
            nameInput.error = "Title is required"
            return
        }

        val x = xInput.text.toString().toDoubleOrNull()
        if (x == null) {
            Timber.w("Validation failed: X is not a number")
            xInput.error = "Enter a valid number"
            return
        }

        val y = yInput.text.toString().toDoubleOrNull()
        if (y == null) {
            Timber.w("Validation failed: Y is not a number")
            yInput.error = "Enter a valid number"
            return
        }

        try {
            val bird = Bird(
                name = name,
                description = description,
                x = x,
                y = y,
                imageUri = selectedImageUri?.toString()
            )

            bird.validate()

            if (editingId == null) {
                AppData.birds.create(bird)
                Timber.i("Created bird id=${bird.id}, name=${bird.name}")
                Toast.makeText(this, "Bird created", Toast.LENGTH_SHORT).show()
            } else {
                AppData.birds.update(bird)
                Timber.i("Updated bird id=${bird.id}")
                Toast.makeText(this, "Bird updated", Toast.LENGTH_SHORT).show()
            }

            finish()

        } catch (e: IllegalArgumentException) {

            Timber.w("Validation failed: ${e.message}")
            Toast.makeText(
                this,
                e.message ?: "Please check the entered values.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}