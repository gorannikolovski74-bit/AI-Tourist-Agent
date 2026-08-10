package com.goran.aitouristagent.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Opens a "nearby X" search in whatever maps app is installed — no API key needed
 * (ANDROID_APP_ARCHITECTURE.md §2.1). Falls back to a browser Maps search if no
 * maps app can handle the geo: URI.
 */
fun openNearbySearch(context: Context, query: String) {
    val geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(query))
    val geoIntent = Intent(Intent.ACTION_VIEW, geoUri)
    try {
        context.startActivity(geoIntent)
    } catch (_: ActivityNotFoundException) {
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query))
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}
