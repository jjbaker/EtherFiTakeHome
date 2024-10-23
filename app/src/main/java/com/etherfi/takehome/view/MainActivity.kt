package com.etherfi.takehome.view

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.etherfi.takehome.ui.theme.EtherFiTakeHomeTheme
import com.etherfi.takehome.ui.theme.Typography
import com.etherfi.takehome.view.compose.NoWalletConnectionScreen
import com.etherfi.takehome.view.compose.WalletAuthorizedScreen
import com.etherfi.takehome.view.compose.WalletConnectedScreen
import com.etherfi.takehome.viewmodel.WalletConnectViewModel
import com.etherfi.takehome.viewmodel.WalletState
import com.reown.appkit.client.AppKit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            AppKit.register(this) //for coinbase wallet
        } catch (e: Exception) {
            Log.e("%%%%%", "AppKit Coinbase issue $e")
        }

        val walletConnectViewModel by viewModels<WalletConnectViewModel>()
        walletConnectViewModel.userMsgLiveData.observe(this) { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
        }

        val networkAvailable: MutableState<Boolean> = mutableStateOf(false)
        setupNetworkMonitor(networkAvailable)

        enableEdgeToEdge()
        setContent {
            val state by walletConnectViewModel.screenState.collectAsState()
            EtherFiTakeHomeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    ) {
                        Text(text = "EtherFi Take Home App", style = Typography.titleLarge)
                        if (!networkAvailable.value) {
                            Text("No Internet Connection")
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (state.walletState) {
                            WalletState.NoConnection -> NoWalletConnectionScreen(
                                walletConnectViewModel::checkWalletStatus,
                                networkAvailable.value
                            )

                            is WalletState.Connected -> WalletConnectedScreen(
                                walletConnectViewModel::authorizeWallet,
                                state.isAuthorizing,
                                walletConnectViewModel::disconnectWallet,
                                state.isDisconnecting,
                                networkAvailable.value
                            )

                            is WalletState.Authorized -> WalletAuthorizedScreen(
                                state.walletState as WalletState.Authorized,
                                walletConnectViewModel::disconnectWallet,
                                state.isDisconnecting,
                                networkAvailable.value
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupNetworkMonitor(networkAvailable: MutableState<Boolean>) {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkAvailable.value = true
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                networkAvailable.value = false

            }
        }
        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

}




