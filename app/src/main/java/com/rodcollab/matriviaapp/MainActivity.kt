package com.rodcollab.matriviaapp

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.rodcollab.matriviaapp.game.theme.MaTriviaAppTheme
import com.rodcollab.matriviaapp.game.ui.TriviaGameScreen
import com.rodcollab.matriviaapp.game.TriviaGameVm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val networkRequest by lazy {
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }
    private var networkCallback: NetworkCallback? = null
    private lateinit var observer: NetworkObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: TriviaGameVm by viewModels()
        observer = NetworkObserver(viewModel)
        networkCallback = observer
        lifecycle.addObserver(observer)

        val connectivityManager =
            baseContext.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(
            networkRequest,
            networkCallback as ConnectivityManager.NetworkCallback
        )

        setContent {
            MaTriviaAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TriviaGameScreen(viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkCallback = null
    }
}

class NetworkObserver(private val viewModel: TriviaGameVm) : DefaultLifecycleObserver, NetworkCallback() {
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        viewModel.changeNetworkState(true)
    }
    override fun onLost(network: Network) {
        super.onLost(network)
        viewModel.changeNetworkState(null)
    }
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        viewModel.onResume()
    }
}