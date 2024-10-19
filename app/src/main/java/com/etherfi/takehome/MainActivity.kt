package com.etherfi.takehome

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        enableEdgeToEdge()
        setContent {
            val state by walletConnectViewModel.walletStateFlow.collectAsState()
            EtherFiTakeHomeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    ) {
                        Text(text = "EtherFi Take Home App", style = Typography.titleLarge)
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
                                walletConnectViewModel::checkWalletStatus
                            )

                            WalletState.Connected -> WalletConnectedScreen(
                                { } ,
                                walletConnectViewModel::disconnectWallet
                            )
                            is WalletState.Authorized -> TODO()
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NoWalletConnectionScreen(checkConnectionStatus: () -> Unit) {
        var showAppKitModalBottomSheet by remember { mutableStateOf(false) }
        val modalSheetState =
            rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        TakeHomeButton(
            text = "Connect to Wallet",
            onClick = { showAppKitModalBottomSheet = true }
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
    fun WalletConnectedScreen(authorize: ()->Unit, disconnect: ()->Unit) {
        Text(
            text = "Wallet Connected",
            style = Typography.bodyLarge
        )
        Text(
            text = "EitherFi App must be authorized before continuing",
            style = Typography.bodyMedium
        )
        TakeHomeButton(
            text = "Authorize EtherFi App",
            onClick = { authorize() }
        )
        TakeHomeButton(
            text = "Disconnect Wallet",
            onClick = { disconnect() }
        )
    }

    @Composable
    fun TakeHomeButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = { onClick() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = text,
                style = Typography.bodyLarge,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}




