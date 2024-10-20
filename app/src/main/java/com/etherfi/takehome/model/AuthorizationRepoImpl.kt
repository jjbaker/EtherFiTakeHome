package com.etherfi.takehome.model

import android.util.Log
import com.etherfi.takehome.TakeHomeApp
import com.reown.android.internal.common.signing.cacao.Cacao
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.client.models.request.Request
import com.reown.sign.client.Sign
import com.reown.sign.client.SignClient
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

class AuthorizationRepoImpl: AuthorizationRepo {

    override suspend fun sendAuthorizationRequest(
        onSendFailure: (String) -> Unit
    ) {
        val account = AppKit.getAccount()
        if(account == null ) {
            onSendFailure("No account found")
            return
        }
        val issuer = "did:pkh:${account.chain.id}:${account.address}"
        val siweMessage = formatSigningMessage(
            Modal.Model.AuthPayloadParams(
                chains = listOf("eip155:1"),
                domain = TakeHomeApp.APP_URL,
                uri = "https://${TakeHomeApp.APP_URL}/login",
                nonce = UUID.randomUUID().toString(),
                statement = "I accept the Terms of Service: https://${TakeHomeApp.APP_URL}/login",
                methods = listOf("personal_sign")
            ), issuer
        )
        val msg = siweMessage.encodeToByteArray()
            .joinToString(separator = "", prefix = "0x") { eachByte -> "%02x".format(eachByte) }
        val body = "[\"$msg\", \"${account.address}\"]"

        AppKit.request(
            request = Request("personal_sign", body),
            onError = { throwable->
                onSendFailure("Authorization request failed to send. $throwable")
            },
        )
    }

    private fun Modal.Model.AuthPayloadParams.toSign(issuer: String): Sign.Params.FormatMessage =
        Sign.Params.FormatMessage(
            payloadParams = Sign.Model.PayloadParams(
                chains = chains,
                domain = domain,
                aud = uri,
                nonce = nonce,
                nbf = nbf,
                exp = exp,
                iat = SimpleDateFormat(Cacao.Payload.ISO_8601_PATTERN).format(Calendar.getInstance().time),
                type = "",
                statement = statement,
                requestId = requestId,
                resources = resources,
            ),
            iss = issuer
        )


    private fun formatSigningMessage(authParams: Modal.Model.AuthPayloadParams, issuer: String): String {
        return SignClient.formatAuthMessage(authParams.toSign(issuer))
    }


}