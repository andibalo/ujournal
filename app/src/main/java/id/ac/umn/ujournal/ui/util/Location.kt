package id.ac.umn.ujournal.ui.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import java.io.IOException
import java.util.Locale

fun getAddressFromLatLong(
    lat: Double,
    lon: Double,
    context: Context,
    useDeprecated: Boolean = false
): Address? {
    var address: Address? = null
    val geocoder = Geocoder(context, Locale.getDefault())

    if(useDeprecated || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                address = list[0]
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return address
    }

    geocoder.getFromLocation(lat, lon, 1) { list ->
        if (list.size != 0) {
            address = list[0]
        }
    }

    return address
}