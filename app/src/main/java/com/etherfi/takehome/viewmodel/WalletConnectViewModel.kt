package com.etherfi.takehome.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etherfi.takehome.model.impl.AppKitDelegate
import com.etherfi.takehome.model.AuthorizationRepo
import com.etherfi.takehome.model.SharedPrefsRepo
import com.etherfi.takehome.model.di.IoDispatcher
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletConnectViewModel @Inject constructor(
    private val appKitDelegate: AppKitDelegate,
    private val authorizationRepo: AuthorizationRepo,
    private val sharedPrefsRepo: SharedPrefsRepo,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _screenState: MutableStateFlow<ScreenState> =
        MutableStateFlow(ScreenState())
    val screenState: StateFlow<ScreenState> get() = _screenState

    private val _userMsgLiveData: MutableLiveData<String> = MutableLiveData()
    val userMsgLiveData: LiveData<String> get() = _userMsgLiveData

    init {
        checkWalletStatus()
        setupAppKitDelegateListener()
    }

    fun checkWalletStatus() {
        viewModelScope.launch(dispatcher) {
            val account = AppKit.getAccount()
            if (account == null) {
                setNoConnection()
            } else {
                _screenState.update {
                    it.copy(
                        walletState = if (sharedPrefsRepo.isAuthorized()) {
                            WalletState.Authorized(account.address)
                        } else {
                            WalletState.Connected
                        }
                    )
                }
            }
        }
    }

    fun disconnectWallet() {
        disconnect(
            successMsg = "Wallet Disconnected",
            errorMsg = "Error occurred when attempting to disconnect. Try again later."
        )
    }

    fun authorizeWallet() {
        _screenState.update { it.copy(isAuthorizing = true) }
        viewModelScope.launch(dispatcher) {
            authorizationRepo.sendAuthorizationRequest(
                onSendFailure = { error ->
                    _userMsgLiveData.postValue(error)
                    _screenState.update { it.copy(isAuthorizing = false) }
                }
            )
        }
    }

    fun setAuthorized() {
        sharedPrefsRepo.setAuthorization(true)
        checkWalletStatus()
    }

    fun setNoConnection() {
        _screenState.update { ScreenState() }
        sharedPrefsRepo.setAuthorization(false)
    }

    private fun disconnect(successMsg: String, errorMsg: String) {
        _screenState.update { it.copy(isDisconnecting = true) }
        AppKit.disconnect(
            onSuccess = {
                _screenState.update { it.copy(isDisconnecting = false) }
                setNoConnection()
                _userMsgLiveData.postValue(successMsg)

            },
            onError = { throwable ->
                _screenState.update { it.copy(isDisconnecting = false) }
                _userMsgLiveData.postValue("$errorMsg ${throwable.message ?: ""}")
            }
        )
    }

    private fun setupAppKitDelegateListener(){
        viewModelScope.launch(dispatcher) {
            appKitDelegate.delegateSharedFlow.collect { model ->
                when (model) {
                    is Modal.Model.SessionRequestResponse -> {
                        when (model.result) {
                            is Modal.Model.JsonRpcResponse.JsonRpcResult -> {
                                setAuthorized()
                                _screenState.update { it.copy(isAuthorizing = false) }
                            }

                            is Modal.Model.JsonRpcResponse.JsonRpcError -> {
                                disconnect(
                                    successMsg = "Authorization Denied. Wallet Disconnected",
                                    errorMsg = "Authorization Denied. Unable to disconnect at this time. Try again later."
                                )
                                _screenState.update { it.copy(isAuthorizing = false) }
                            }
                        }

                    }

                    is Modal.Model.ExpiredProposal,
                    is Modal.Model.ExpiredRequest -> {
                        _userMsgLiveData.postValue("Request expired. Try again later.")
                        _screenState.update { it.copy(isAuthorizing = false) }
                    }

                    else -> {
                        Log.e(
                            "%%%%%%%",
                            "Unhandled Event: ${model?.javaClass?.simpleName ?: "No action taken"}"
                        )
                    }
                }
            }
        }
    }
}