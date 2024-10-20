package com.etherfi.takehome.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.etherfi.takehome.model.AppKitDelegate
import com.etherfi.takehome.model.AuthorizationRepo
import com.etherfi.takehome.model.SharedPrefsRepo
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
    private val sharedPrefsRepo: SharedPrefsRepo,
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
                        when (model.result) {
                            is Modal.Model.JsonRpcResponse.JsonRpcResult -> {
                                setAuthorized()
                            }

                            is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                                AppKit.disconnect(
                                    onSuccess = {
                                        setNoConnection()
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
                    is Modal.Model.ExpiredProposal,
                    is Modal.Model.ExpiredRequest -> _userMsgLiveData.postValue("Request expired. Try again later.")
                    else -> {
                        Log.e("%%%%%%%", model?.javaClass?.simpleName ?: "No action taken")
                    }
                }
            }
        }
    }

    fun checkWalletStatus() {
        val account = AppKit.getAccount()
        if (account == null) {
            setNoConnection()
        } else {
            _walletStateFlow.value = if (sharedPrefsRepo.isAuthorized()) {
                WalletState.Authorized(account.address)
            } else {
                WalletState.Connected
            }

        }
    }

    fun disconnectWallet() {
        AppKit.disconnect(
            onSuccess = {
                setNoConnection()
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
                onSendFailure = { error -> _userMsgLiveData.postValue(error) }
            )
        }
    }


    fun setAuthorized() {
        sharedPrefsRepo.setAuthorization(true)
        checkWalletStatus()
    }

    fun setNoConnection() {
        _walletStateFlow.value = WalletState.NoConnection
        sharedPrefsRepo.setAuthorization(false)
    }
}