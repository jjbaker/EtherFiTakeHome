package com.etherfi.takehome

import android.app.Application
import android.util.Log
import com.etherfi.takehome.model.AppKitDelegate
import com.reown.android.Core
import com.reown.android.CoreClient
import com.reown.android.relay.ConnectionType
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.presets.AppKitChainsPresets.ethToken
import com.reown.appkit.utils.EthUtils
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TakeHomeApp : Application() {

    @Inject lateinit var appKitDelegate: AppKitDelegate

    override fun onCreate() {
        super.onCreate()
        val projectId = BuildConfig.WALLET_CONNECT_API_KEY
        val connectionType = ConnectionType.AUTOMATIC
        val appMetaData = Core.Model.AppMetaData(
            name = APP_NAME,
            description = APP_DESCRIPTION,
            url = APP_URL,
            icons = listOf("https://gblobscdn.gitbook.com/spaces%2F-LJJeCjcLrr53DcT1Ml7%2Favatar.png?alt=media"),
            redirect = APP_REDIRECT
        )

        CoreClient.initialize(
            projectId = projectId,
            connectionType = connectionType,
            application = this,
            metaData = appMetaData,
            onError = { error -> Log.e("%%%%%", "CoreClientError ${error.throwable}") }
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
                AppKit.setDelegate(appKitDelegate)
            },
            onError = { Log.e("%%%%%", "AppKiy Initialization Error ${it.throwable}") }
        )
    }

    companion object {
        const val APP_NAME = "EtherFi Take Home"
        const val APP_DESCRIPTION = "App to test WalletConnect"
        const val APP_URL = "obviouslyfakeurl.com"
        const val APP_REDIRECT = "test-app:/request"
    }
}