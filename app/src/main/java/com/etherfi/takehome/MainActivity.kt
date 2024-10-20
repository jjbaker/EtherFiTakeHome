package com.etherfi.takehome

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.etherfi.takehome.ui.theme.EtherFiTakeHomeTheme
import com.etherfi.takehome.ui.theme.Typography
import com.etherfi.takehome.viewmodel.WalletConnectViewModel
import com.etherfi.takehome.viewmodel.WalletState
import com.reown.appkit.client.AppKit
import com.reown.appkit.ui.components.internal.AppKitComponent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        val networkAvailable: MutableState<Boolean> = mutableStateOf(false)
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

        enableEdgeToEdge()
        setContent {
            val state by walletConnectViewModel.walletStateFlow.collectAsState()
            val network by networkAvailable
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
                        when (state) {
                            WalletState.NoConnection -> NoWalletConnectionScreen(
                                walletConnectViewModel::checkWalletStatus,
                                networkAvailable.value
                            )

                            WalletState.Connected -> WalletConnectedScreen(
                                walletConnectViewModel::authorizeWallet,
                                walletConnectViewModel::disconnectWallet,
                                networkAvailable.value
                            )

                            is WalletState.Authorized -> WalletAuthorizedScreen(
                                (state as WalletState.Authorized).account,
                                walletConnectViewModel::disconnectWallet,
                                networkAvailable.value
                            )
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NoWalletConnectionScreen(checkConnectionStatus: () -> Unit, hasNetwork: Boolean) {
        var showAppKitModalBottomSheet by remember { mutableStateOf(false) }
        val modalSheetState =
            rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        NetworkReliantButton(
            text = "Connect to Wallet",
            onClick = { showAppKitModalBottomSheet = true },
            hasNetworkConnection = hasNetwork
        )


        if (showAppKitModalBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAppKitModalBottomSheet = false },
                sheetState = modalSheetState
            ) {
                AppKitComponent(
                    shouldOpenChooseNetwork = false,
                    closeModal = {
                        scope.launch { modalSheetState.hide() }.invokeOnCompletion {
                            if (!modalSheetState.isVisible) showAppKitModalBottomSheet = false
                            checkConnectionStatus()
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.navigationBarsPadding())

    }

    @Composable
    fun WalletConnectedScreen(authorize: () -> Unit, disconnect: () -> Unit, hasNetwork: Boolean) {
        Text(
            text = "Wallet Connected",
            style = Typography.bodyLarge
        )
        Text(
            text = "EitherFi App must be authorized before continuing",
            style = Typography.bodyMedium
        )
        NetworkReliantButton(
            text = "Authorize EtherFi App",
            onClick = { authorize() },
            hasNetworkConnection = hasNetwork
        )
        NetworkReliantButton(
            text = "Disconnect Wallet",
            onClick = { disconnect() },
            hasNetworkConnection = hasNetwork
        )
    }

    @Composable
    fun WalletAuthorizedScreen(account: String, disconnect: () -> Unit, hasNetwork: Boolean) {
        Text(
            text = "Account Number",
            style = Typography.bodyLarge
        )
        Text(
            text = account,
            overflow = TextOverflow.Ellipsis,
            style = Typography.bodySmall
        )
        NetworkReliantButton(
            onClick = { disconnect() },
            text = "Disconnect Wallet",
            hasNetworkConnection = hasNetwork
        )
    }


    @Composable
    fun NetworkReliantButton(
        text: String,
        onClick: () -> Unit,
        hasNetworkConnection: Boolean = true,
        noNetworkOnClick: () -> Unit = { Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()}
    ) {
        Button(
            onClick = { if (hasNetworkConnection) onClick() else noNetworkOnClick() },
            modifier = Modifier.padding(10.dp),
            colors = if (hasNetworkConnection) {
                ButtonDefaults.buttonColors()
            } else {
                with(ButtonDefaults.buttonColors()) {
                    ButtonDefaults.buttonColors(
                        containerColor = disabledContainerColor,
                        contentColor = disabledContentColor
                    )
                }
            }
        ) {
            Text(
                text = text,
                style = Typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}




