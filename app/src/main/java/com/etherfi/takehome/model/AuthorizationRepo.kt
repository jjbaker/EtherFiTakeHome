package com.etherfi.takehome.model

interface AuthorizationRepo {

    suspend fun sendAuthorizationRequest(onSendFailure: (String)->Unit)
}