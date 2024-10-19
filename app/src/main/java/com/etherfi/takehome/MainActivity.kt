package com.etherfi.takehome

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.etherfi.takehome.ui.theme.EtherFiTakeHomeTheme
import com.reown.appkit.client.AppKit
import com.reown.appkit.ui.components.internal.AppKitComponent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            AppKit.register(this) //for coinbase wallet
        } catch (e: Exception) {
            Log.e("%%%%%", "AppKit Coinbase issue $e")
        }


        enableEdgeToEdge()
        setContent {
            EtherFiTakeHomeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MainNavHost()
                    }
                }
            }
        }
    }

    @Composable
    fun MainNavHost() {
        val navController = rememberNavController()
        NavHost(navController, startDestination = NoWalletConnection) {
            composable<NoWalletConnection> {
                NoWalletConnectionScreen()
            }
            composable<WalletConnected> {
                WalletConnectedScreen()
            }
        }
    }

    @Serializable
    object NoWalletConnection

    @Serializable
    object WalletConnected


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NoWalletConnectionScreen() {
        var showAppKitModalBottomSheet by remember { mutableStateOf(false) }
        val modalSheetState =
            rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        Button(
            onClick = { showAppKitModalBottomSheet = true }
        ) {
            Text("Connect to Wallet")
        }


        if (showAppKitModalBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAppKitModalBottomSheet = false },
                sheetState = modalSheetState
            ) {
                AppKitComponent(
                    shouldOpenChooseNetwork = false,
                    closeModal = {
                        scope.launch { modalSheetState.hide() }.invokeOnCompletion {
                            if (!modalSheetState.isVisible) showAppKitModalBottomSheet =
                                false
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.navigationBarsPadding())
        }

    }
}




