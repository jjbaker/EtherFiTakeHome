package com.etherfi.takehome

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.etherfi.takehome.ui.theme.EtherFiTakeHomeTheme
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtherFiTakeHomeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        onClick = { testPairing() },
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Text("Test Pairing")
                    }
                }
            }
        }
    }

    private fun testPairing() {
        val url = ""
        val pair = Wallet.Params.Pair(url)

        Web3Wallet.pair(
            pair,
            onSuccess = { Log.e("%%%%%", "Paired Successfully") }
        ) { error ->
            Log.e("%%%%%", "Pairing Error ${error.throwable}$ {error.throwable.message}")

        }
    }
}




