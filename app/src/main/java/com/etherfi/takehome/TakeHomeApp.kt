package com.etherfi.takehome

import android.app.Application
import android.util.Log
import com.etherfi.takehome.model.AKModalDelegate
import com.reown.android.Core
import com.reown.android.CoreClient
import com.reown.android.relay.ConnectionType
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.presets.AppKitChainsPresets.ethToken
import com.reown.appkit.utils.EthUtils

class TakeHomeApp: Application() {

    class TakeHomeApp: Application() {

        override fun onCreate() {
            super.onCreate()
            val projectId = BuildConfig.WALLET_CONNECT_API_KEY
            val connectionType = ConnectionType.AUTOMATIC
            val appMetaData = Core.Model.AppMetaData(
                name = "EtherFi Take Home",
                description = "App to test WalletConnect",
                url = "obviouslyfakeurl.com",
                icons = listOf("https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"),
                redirect = "test-app:/request" // Custom Redirect URI
            )

            CoreClient.initialize(
                projectId = projectId,
                connectionType = connectionType,
                application = this,
                metaData = appMetaData,
                onError = {error -> Log.e("%%%%%", "CoreClientError ${error.throwable}")}
            )


            AppKit.initialize(
                init = Modal.Params.Init(CoreClient),
                onSuccess = {
                    Log.e("%%%%%", "AppKit Initialization Success")
                    AppKit.setChains(
                        listOf(
                            Modal.Model.Chain(
                                chainName = "Ethereum",
                                chainNamespace = "eip155",
                                chainReference = "1",
                                requiredMethods = listOf("personal_sign"),
                                optionalMethods = emptyList(),
                                events = EthUtils.ethEvents,
                                token = ethToken
                            )
                        )
                    )
                    AppKit.setDelegate(AKModalDelegate)
                },
                onError = { Log.e("%%%%%", "AppKiy Initialization Error ${it.throwable}") }
            )
        }
    }
}