package com.wecancity.robusta_weather_app.networking
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * Checks the Internet connection and performs an action if it's active.
 */
class NetworkStatusChecker(private val connectivityManager: ConnectivityManager?) {

  inline fun performIfConnectedToInternet(action: () -> Unit) {
    if (hasInternetConnection()) {
      action()
    }
  }

  fun hasInternetConnection(): Boolean {
    val network = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      connectivityManager?.activeNetwork ?: return false
    } else {
      TODO("VERSION.SDK_INT < M")
    }
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
  }
}
