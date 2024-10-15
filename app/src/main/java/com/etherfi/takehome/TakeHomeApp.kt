package com.etherfi.takehome

import android.app.Application
import android.util.Log
import com.etherfi.takehome.model.WalletDelegate
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.web3.wallet.client.Wallet
import com.walletconnect.web3.wallet.client.Web3Wallet

class TakeHomeApp: Application() {

    class TakeHomeApp: Application() {

        override fun onCreate() {
            super.onCreate()
            val projectId = BuildConfig.WALLET_CONNECT_API_KEY
            val connectionType = ConnectionType.AUTOMATIC
            val appMetaData = Core.Model.AppMetaData(
                name = "Wallet Connect Take Home",
                description = "App to test WalletConnect",
                url = "obviouslyfakeurl.com",
                icons = listOf("https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"),
                redirect = "kotlin-wallet-wc:/request" // Custom Redirect URI
            )

            CoreClient.initialize(
                projectId = projectId,
                connectionType = connectionType,
                application = this,
                metaData = appMetaData,
                onError = {error -> Log.e("%%%%%", "CoreClientError ${error.throwable}")}
            )

            val initParams = Wallet.Params.Init(core = CoreClient)

            Web3Wallet.initialize(
                initParams,
                onSuccess = {
                    Log.e("%%%%%","Initialization Success")
                    Web3Wallet.setWalletDelegate(WalletDelegate())
                }
            ) { error ->
                Log.e("%%%%%","Initialization error ${error.throwable}")
            }
        }
    }
}