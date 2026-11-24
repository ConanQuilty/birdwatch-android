package com.example.birdwatch

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import androidx.core.content.ContextCompat

class MapPickerActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null
    private var selectedTitle = "Selected location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue =
            "Birdwatch-ConanQuilty/1.0 (github.com/ConanQuilty/birdwatch-android)"
        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val lat = intent.getDoubleExtra("latitude", 52.245954)
        val lon = intent.getDoubleExtra("longitude", -7.139539)
        val start = GeoPoint(lat, lon)

        intent.getStringExtra("title")
            ?.takeIf { it.isNotBlank() }
            ?.let { selectedTitle = it }

        map.controller.setZoom(18.0)
        map.controller.setCenter(start)
        setSelectedPoint(start)
        showExistingMarkers()

        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point)
                return true
            }

            override fun longPressHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point)
                return true
            }
        }

        //Move the map to the back so you can interact with pins
        map.overlays.add(0, MapEventsOverlay(receiver))

        findViewById<Button>(R.id.confirmLocationButton).setOnClickListener {
            val point = selectedPoint ?: return@setOnClickListener
            val result = intent
                .putExtra("latitude", point.latitude)
                .putExtra("longitude", point.longitude)

            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    private fun setSelectedPoint(point: GeoPoint) {
        selectedPoint = point

        if (marker == null) {
            marker = Marker(map).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            map.overlays.add(marker)
        }

        marker?.position = point
        marker?.title = selectedTitle
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }

    private fun showExistingMarkers() {
        val birds = AppData.birds.findAll()

        birds.forEach { bird ->
            val point = GeoPoint(
                bird.x,
                bird.y
            )

            val marker = Marker(map).apply {
                position = point
                title = bird.name
                snippet = bird.description
                icon = ContextCompat.getDrawable(this@MapPickerActivity, R.drawable.ic_bird_marker)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }

            map.overlays.add(marker)
        }

        map.invalidate()
    }
}