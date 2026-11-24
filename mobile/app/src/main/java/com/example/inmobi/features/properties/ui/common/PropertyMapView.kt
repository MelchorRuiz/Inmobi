package com.example.inmobi.features.properties.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.inmobi.core.location.LocationProvider
import com.example.inmobi.features.properties.domain.Property
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun PropertyMapView(
    properties: List<Property>,
    onPropertyClick: (Property) -> Unit,
    userLocation: Pair<Double, Double>?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val propertiesWithCoords = properties.filter { it.latitude != null && it.longitude != null }

    val mapView = remember {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        Configuration.getInstance().userAgentValue = "Inmobi/1.0 (Android; https://inmobi.app)"
        MapView(context).apply {
            setTileSource(osmFranceTileSource())
            setMultiTouchControls(true)
            controller.setZoom(LocationProvider.DEFAULT_ZOOM)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            overlays.add(CopyrightOverlay(context))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { mapView },
        update = { view ->
            view.overlays.clear()

            val markerIcon = createPropertyMarkerIcon(context)

            propertiesWithCoords.forEach { property ->
                val geoPoint = GeoPoint(property.latitude!!, property.longitude!!)
                val marker = Marker(view)
                marker.position = geoPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.icon = markerIcon
                marker.title = property.listingTitle
                marker.snippet = formatPrice(property.price, property.currency, property.operationName)
                marker.setOnMarkerClickListener { _, _ ->
                    onPropertyClick(property)
                    true
                }
                view.overlays.add(marker)
            }

            val center = determineCenter(userLocation, propertiesWithCoords)
            view.controller.setCenter(center)

            if (propertiesWithCoords.size > 1 && userLocation == null) {
                val box = BoundingBox.fromGeoPoints(
                    propertiesWithCoords.map { GeoPoint(it.latitude!!, it.longitude!!) }
                )
                view.post {
                    view.zoomToBoundingBox(box.increaseByScale(1.3f), false)
                }
            }
        }
    )
}

private fun osmFranceTileSource() = XYTileSource(
    "OSM France",
    0,
    19,
    256,
    ".png",
    arrayOf(
        "https://a.tile.openstreetmap.fr/osmfr/",
        "https://b.tile.openstreetmap.fr/osmfr/",
        "https://c.tile.openstreetmap.fr/osmfr/"
    ),
    "(c) OpenStreetMap contributors",
    org.osmdroid.tileprovider.tilesource.TileSourcePolicy(
        2,
        org.osmdroid.tileprovider.tilesource.TileSourcePolicy.FLAG_NO_BULK
                or org.osmdroid.tileprovider.tilesource.TileSourcePolicy.FLAG_NO_PREVENTIVE
                or org.osmdroid.tileprovider.tilesource.TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                or org.osmdroid.tileprovider.tilesource.TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
    )
)

private fun determineCenter(
    userLocation: Pair<Double, Double>?,
    properties: List<Property>
): GeoPoint {
    if (userLocation != null) {
        return GeoPoint(userLocation.first, userLocation.second)
    }
    if (properties.isNotEmpty()) {
        val first = properties.first()
        if (first.latitude != null && first.longitude != null) {
            return GeoPoint(first.latitude, first.longitude)
        }
    }
    return GeoPoint(
        LocationProvider.DEFAULT_CENTER.first,
        LocationProvider.DEFAULT_CENTER.second
    )
}

private fun createPropertyMarkerIcon(context: Context): BitmapDrawable {
    val size = 96
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.parseColor("#3E9B94")
        style = Paint.Style.FILL
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, circlePaint)

    val houseDrawable = androidx.core.content.ContextCompat.getDrawable(context, com.example.inmobi.R.drawable.ic_house)
    houseDrawable?.let {
        val iconSize = (size * 0.65).toInt()
        val offset = (size - iconSize) / 2
        it.setBounds(offset, offset, offset + iconSize, offset + iconSize)
        it.setTint(AndroidColor.WHITE)
        it.draw(canvas)
    }

    return BitmapDrawable(context.resources, bitmap)
}
