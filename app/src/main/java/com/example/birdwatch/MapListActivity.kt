package com.example.birdwatch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

class MapListActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgPlace: ImageView
    var selectedBird: Bird? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_list)

        Configuration.getInstance().userAgentValue = "Birdwatch-ConanQuilty/1.0 (github.com/ConanQuilty/birdwatch-android)"

        // Initialise views
        mapView = findViewById(R.id.mapList)
        txtTitle = findViewById(R.id.mapTitleText)
        txtDescription = findViewById(R.id.mapDescText)
        imgPlace = findViewById(R.id.mapImageView)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = title
        setSupportActionBar(toolbar)


        findViewById<Button>(R.id.mapEditButton).setOnClickListener {
            val bird = selectedBird ?: return@setOnClickListener
            val intent = Intent(this, AddEditActivity::class.java)
            intent.putExtra("id", bird.id)
            startActivity(intent)
        }

        // Configure OpenStreetMap
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Show map attribution
        mapView.overlays.add(CopyrightOverlay(this))

        loadBirds()
    }

    private fun loadBirds() {

        val birds = AppData.birds.findAll()

        if (birds.isEmpty()) {
            txtTitle.text = "No birds saved yet"
            return
        }

        birds.forEach { bird ->

            val position = GeoPoint(
                bird.x,
                bird.y
            )

            val marker = Marker(mapView)

            marker.position = position
            marker.title = bird.name
            marker.snippet = bird.description

            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            // Handle marker selection
            marker.setOnMarkerClickListener { clickedMarker, _ ->

                displayBird(bird)

                // Return true to consume the click
                true
            }

            mapView.overlays.add(marker)
        }

        // Centre map on first placemark
        if (birds.size == 1) {
            mapView.controller.setZoom(15.0)
            mapView.controller.setCenter(GeoPoint(birds[0].x, birds[0].y))
        } else {
            val box = BoundingBox.fromGeoPoints(birds.map { GeoPoint(it.x, it.y) })
            mapView.post {
                mapView.zoomToBoundingBox(box.increaseByScale(1.3f), false)
            }
        }

        mapView.invalidate()
    }

    private fun displayBird(bird: Bird) {

        selectedBird = bird

        txtTitle.text = bird.name

        txtDescription.text = bird.description

        if (bird.imageUri != null) {
            imgPlace.setImageURI(
                Uri.parse(bird.imageUri)
            )
        } else {
            imgPlace.setImageDrawable(null)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        mapView.onDetach()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()

        mapView.tileProvider.clearTileCache()
        mapView.invalidate()
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

