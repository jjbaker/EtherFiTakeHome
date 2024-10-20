package com.etherfi.takehome.model

import android.util.Log
import com.etherfi.takehome.model.di.ApplicationScope
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppKitDelegate @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope
) : AppKit.ModalDelegate {

    private val _delegateSharedFlow: MutableSharedFlow<Modal.Model?> = MutableSharedFlow()
    val delegateSharedFlow: SharedFlow<Modal.Model?> = _delegateSharedFlow.asSharedFlow()


    //OPTIONAL OVERRIDES
    override fun onSIWEAuthenticationResponse(response: Modal.Model.SIWEAuthenticateResponse) {
        super.onSIWEAuthenticationResponse(response)
        Log.e("%%%%%", "onSIWEAuthenticationResponse $response")
        response.updateSharedFlow()
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.Event) {
        super.onSessionEvent(sessionEvent)
        Log.e("%%%%%", "onSessionEvent $sessionEvent")
        sessionEvent.updateSharedFlow()
    }

    override fun onSessionAuthenticateResponse(sessionAuthenticateResponse: Modal.Model.SessionAuthenticateResponse) {
        super.onSessionAuthenticateResponse(sessionAuthenticateResponse)
        Log.e("%%%%%", "onSessionAuthenticateResponse $sessionAuthenticateResponse")
        sessionAuthenticateResponse.updateSharedFlow()
    }

    //MANDATORY OVERRIDE
    override fun onConnectionStateChange(state: Modal.Model.ConnectionState) {
        Log.e("%%%%%", "onConnectionStateChange $state")
        state.updateSharedFlow()
    }

    override fun onError(error: Modal.Model.Error) {
        Log.e("%%%%%", "onError $error")
        error.updateSharedFlow()
    }

    override fun onProposalExpired(proposal: Modal.Model.ExpiredProposal) {
        Log.e("%%%%%", "onProposalExpired $proposal")
        proposal.updateSharedFlow()
    }

    override fun onRequestExpired(request: Modal.Model.ExpiredRequest) {
        Log.e("%%%%%", "onRequestExpired $request")
        request.updateSharedFlow()
    }

    override fun onSessionApproved(approvedSession: Modal.Model.ApprovedSession) {
        Log.e("%%%%%", "onSessionApproved $approvedSession")
        approvedSession.updateSharedFlow()
    }

    override fun onSessionDelete(deletedSession: Modal.Model.DeletedSession) {
        Log.e("%%%%%", "onSessionDelete $deletedSession")
        deletedSession.updateSharedFlow()
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.SessionEvent) {
        Log.e("%%%%%", "onSessionEvent $sessionEvent")
        sessionEvent.updateSharedFlow()
    }

    override fun onSessionExtend(session: Modal.Model.Session) {
        Log.e("%%%%%", "onSessionExtend $session")
        session.updateSharedFlow()
    }

    override fun onSessionRejected(rejectedSession: Modal.Model.RejectedSession) {
        Log.e("%%%%%", "onSessionRejected $rejectedSession")
        rejectedSession.updateSharedFlow()
    }

    override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
        Log.e("%%%%%", "onSessionRequestResponse $response")
        response.updateSharedFlow()
    }

    override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
        Log.e("%%%%%", "onSessionUpdate $updatedSession")
        updatedSession.updateSharedFlow()
    }

    private fun Modal.Model.updateSharedFlow() {
        scope.launch {
            _delegateSharedFlow.emit(this@updateSharedFlow)
        }
    }
}