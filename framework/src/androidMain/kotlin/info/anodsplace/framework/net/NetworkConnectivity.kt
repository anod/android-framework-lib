package info.anodsplace.framework.net

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

class NetworkConnectivity(context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isNetworkAvailable: Boolean
        @SuppressLint("MissingPermission")
        get() = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)?.let { capabilities ->
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            } ?: false
        } ?: false

    val isWifiEnabled: Boolean
        @SuppressLint("MissingPermission")
        get() = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        } ?: false

    fun isNetworkException(tr: Throwable): Boolean {
        return tr is SocketException
                || tr is UnknownHostException
                || tr is SSLHandshakeException
                || tr is SSLPeerUnverifiedException
                || tr is ConnectException
                || tr is SocketTimeoutException
    }
}