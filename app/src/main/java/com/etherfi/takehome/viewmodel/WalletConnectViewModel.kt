package com.etherfi.takehome.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etherfi.takehome.model.AppKitDelegate
import com.etherfi.takehome.model.AuthorizationRepo
import com.etherfi.takehome.model.di.ApplicationScope
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletConnectViewModel @Inject constructor(
    private val appKitDelegate: AppKitDelegate,
    private val authorizationRepo: AuthorizationRepo,
    @ApplicationScope private val externalScope: CoroutineScope
) : ViewModel() {

    private val _walletStateFlow: MutableStateFlow<WalletState> =
        MutableStateFlow(WalletState.NoConnection)
    val walletStateFlow: StateFlow<WalletState> get() = _walletStateFlow

    private val _userMsgLiveData: MutableLiveData<String> = MutableLiveData()
    val userMsgLiveData: LiveData<String> get() = _userMsgLiveData

    init {
        checkWalletStatus()
        externalScope.launch {
            appKitDelegate.delegateSharedFlow.collect { model ->
                when (model) {
                    is Modal.Model.SessionRequestResponse -> {
                        when(model.result){
                            is Modal.Model.JsonRpcResponse.JsonRpcResult -> {
                                _walletStateFlow.value = WalletState.Authorized("")
                            }
                            is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                                AppKit.disconnect(
                                    onSuccess = {
                                        _walletStateFlow.value = WalletState.NoConnection
                                        _userMsgLiveData.postValue("Authorization Denied. Wallet Disconnected")
                                    },
                                    onError = { throwable: Throwable ->
                                        _userMsgLiveData.postValue(
                                            "Authorization Denied. Unable to disconnect at this time. Try again later."
                                        )
                                    }
                                )
                            }
                        }

                    }
                    else -> { Log.e("%%%%%%%", model?.javaClass?.simpleName ?: "No Model")}
                }
            }
        }
    }

    fun checkWalletStatus() {

        _walletStateFlow.value = AppKit.getAccount()?.let {
            WalletState.Connected
        } ?: WalletState.NoConnection
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

    fun authorizeWallet() {
        externalScope.launch {
            authorizationRepo.sendAuthorizationRequest(
                onSendFailure = {error ->_userMsgLiveData.postValue(error) }
            )
        }
    }


}