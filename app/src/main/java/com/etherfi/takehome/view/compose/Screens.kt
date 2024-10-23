package com.etherfi.takehome.view.compose

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import com.etherfi.takehome.ui.theme.Typography
import com.etherfi.takehome.viewmodel.WalletState
import com.reown.appkit.ui.components.internal.AppKitComponent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoWalletConnectionScreen(checkConnectionStatus: () -> Unit, hasNetwork: Boolean) {
    var showAppKitModalBottomSheet by remember { mutableStateOf(false) }
    val modalSheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    EnabledDisabledButton(
        text = "Connect to Wallet",
        onClick = { showAppKitModalBottomSheet = true },
        isEnabled = hasNetwork
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
fun WalletConnectedScreen(
    authorize: () -> Unit,
    isAuth: Boolean,
    disconnect: () -> Unit,
    isDisconnect: Boolean,
    hasNetwork: Boolean
) {
    val context = LocalContext.current
    Text(
        text = "Wallet Connected",
        style = Typography.bodyLarge
    )
    Text(
        text = "EitherFi App must be authorized before continuing",
        style = Typography.bodyMedium
    )

    EnabledDisabledButtonWithSpinner(
        text = "Authorize EtherFi App",
        onClick = { authorize() },
        isEnabled = hasNetwork && !isAuth,
        isDisabledClick = if (!hasNetwork) {
            { Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show() }
        } else {
            { Toast.makeText(context, "Auth in Prog", Toast.LENGTH_SHORT).show() }
        },
        isSpinning = hasNetwork && isAuth
    )

    EnabledDisabledButtonWithSpinner(
        text = "Disconnect Wallet",
        onClick = { disconnect() },
        isEnabled = hasNetwork && !isDisconnect,
        isDisabledClick = if (!hasNetwork) {
            { Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show() }
        } else {
            { Toast.makeText(context, "Disconnect in Prog", Toast.LENGTH_SHORT).show() }
        },
        isSpinning = hasNetwork && isDisconnect
    )
}

@Composable
fun WalletAuthorizedScreen(
    state: WalletState.Authorized,
    disconnect: () -> Unit,
    isDisconnect: Boolean,
    hasNetwork: Boolean
) {
    val context = LocalContext.current
    Text(
        text = "Account Number",
        style = Typography.bodyLarge
    )
    Text(
        text = state.account,
        overflow = TextOverflow.Ellipsis,
        style = Typography.bodySmall
    )
    EnabledDisabledButtonWithSpinner(
        onClick = { disconnect() },
        text = "Disconnect Wallet",
        isEnabled = hasNetwork && !isDisconnect,
        isDisabledClick = if (!hasNetwork) {
            { Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show() }
        } else {
            { Toast.makeText(context, "Disconnect in Prog", Toast.LENGTH_SHORT).show() }
        },
        isSpinning = hasNetwork && isDisconnect
    )
}


