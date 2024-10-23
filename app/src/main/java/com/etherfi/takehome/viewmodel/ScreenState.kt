package com.etherfi.takehome.viewmodel

data class ScreenState(
    val walletState: WalletState = WalletState.NoConnection,
    val isAuthorizing: Boolean = false,
    val isDisconnecting: Boolean = false
)