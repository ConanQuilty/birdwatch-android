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
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar

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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
        setSupportActionBar(toolbar)

        nameInput = findViewById(R.id.nameField)
        descriptionInput = findViewById(R.id.descriptionField)
        xInput = findViewById(R.id.xField)
        yInput = findViewById(R.id.yField)

        val saveButton = findViewById<Button>(R.id.saveButton)
        var selectedLatitude: Double? = null
        var selectedLongitude: Double? = null

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

        val mapPicker =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data ?: return@registerForActivityResult
                    selectedLatitude = data.getDoubleExtra("latitude", 0.0)
                    selectedLongitude = data.getDoubleExtra("longitude", 0.0)

                    selectedLatitude?.let {
                        xInput.setText(it.toString())
                    }

                    selectedLongitude?.let {
                        yInput.setText(it.toString())
                    }
                }
            }

        findViewById<Button>(R.id.buttonLoc).setOnClickListener {
            val mapIntent = Intent(this, MapPickerActivity::class.java)

            xInput.text.toString().toDoubleOrNull()
                ?.takeIf { it in -90.0..90.0 }
                ?.let { mapIntent.putExtra("latitude", it) }

            yInput.text.toString().toDoubleOrNull()
                ?.takeIf { it in -180.0..180.0 }
                ?.let { mapIntent.putExtra("longitude", it) }

            mapIntent.putExtra("title", nameInput.text.toString().trim())   // used in 7b

            mapPicker.launch(mapIntent)
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
                id = editingId ?: 0L,
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
                val updated = AppData.birds.update(bird)
                if (updated) {
                    Timber.i("Updated bird id=${bird.id}")
                    Toast.makeText(this, "Bird updated", Toast.LENGTH_SHORT).show()
                } else {
                    Timber.w("Update failed: bird id=${bird.id} not found")
                    Toast.makeText(this, "Couldn't find that bird to update", Toast.LENGTH_SHORT).show()
                    return
                }
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

    // Load the shared Back menu onto this screen's toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_back, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Back tapped: close this screen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_back -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}