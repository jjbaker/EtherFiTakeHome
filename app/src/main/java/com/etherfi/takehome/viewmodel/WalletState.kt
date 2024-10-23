package com.etherfi.takehome.viewmodel

sealed class WalletState() {
    data object NoConnection : WalletState()
    data object Connected : WalletState()
    data class Authorized(val account: String) : WalletState()
}