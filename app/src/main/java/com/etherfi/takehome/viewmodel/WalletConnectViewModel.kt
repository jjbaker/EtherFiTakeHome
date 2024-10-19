package com.etherfi.takehome.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etherfi.takehome.model.AppKitDelegate
import com.etherfi.takehome.model.di.ApplicationScope
import com.reown.appkit.client.AppKit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletConnectViewModel @Inject constructor(
    private val appKitDelegate: AppKitDelegate,
    @ApplicationScope private val externalScope: CoroutineScope
) : ViewModel() {

    private val _walletStateFlow: MutableStateFlow<WalletState> =
        MutableStateFlow(WalletState.NoConnection)
    val walletStateFlow: StateFlow<WalletState> get() = _walletStateFlow

    private val _userMsgLiveData: MutableLiveData<String> = MutableLiveData()
    val userMsgLiveData: LiveData<String> get() = _userMsgLiveData

    init {
        checkWalletStatus()
    }

    fun checkWalletStatus() {
        externalScope.launch {
            _walletStateFlow.value = AppKit.getAccount()?.let {
                WalletState.Connected
            } ?: WalletState.NoConnection
        }
    }

    fun disconnectWallet() {
        AppKit.disconnect(
            onSuccess = {
                _walletStateFlow.value = WalletState.NoConnection
                _userMsgLiveData.postValue("Wallet Disconnected")
            },
            onError = { throwable ->
                _userMsgLiveData.postValue("Error occurred when attempting to disconnect. Try again later. ${throwable.message ?: ""}")
            }
        )
    }

}