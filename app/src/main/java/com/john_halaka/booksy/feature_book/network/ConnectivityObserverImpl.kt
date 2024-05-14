package com.john_halaka.booksy.feature_book.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ConnectivityObserverImpl(
    context:Context
) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.d("ConnectivityObserver", "Network available")
                    launch {
                        send(ConnectivityObserver.Status.Available)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.d("ConnectivityObserver", "Network lost")
                    launch {
                        send(ConnectivityObserver.Status.Lost)
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    Log.d("ConnectivityObserver", "Network unavailable")
                    launch {
                        send(ConnectivityObserver.Status.Unavailable)
                    }
                }
            }
               // Check the initial network status
            val activeNetworkInfo = connectivityManager.activeNetwork
            if (activeNetworkInfo != null) {
                callback.onAvailable(activeNetworkInfo)
            } else {
                callback.onUnavailable()
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}