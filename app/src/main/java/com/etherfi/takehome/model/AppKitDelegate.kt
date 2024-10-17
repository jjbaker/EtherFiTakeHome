package com.etherfi.takehome.model

import android.util.Log
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal

object AKModalDelegate: AppKit.ModalDelegate{


    //OPTIONAL OVERRIDES
    override fun onSIWEAuthenticationResponse(response: Modal.Model.SIWEAuthenticateResponse) {
        super.onSIWEAuthenticationResponse(response)
        Log.e("%%%%%", "onSIWEAuthenticationResponse $response")
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.Event) {
        super.onSessionEvent(sessionEvent)
        Log.e("%%%%%", "onSessionEvent $sessionEvent")
    }
    override fun onSessionAuthenticateResponse(sessionAuthenticateResponse: Modal.Model.SessionAuthenticateResponse) {
        super.onSessionAuthenticateResponse(sessionAuthenticateResponse)
        Log.e("%%%%%", "onSessionAuthenticateResponse $sessionAuthenticateResponse")
    }

    //MANDATORY OVERRIDE
    override fun onConnectionStateChange(state: Modal.Model.ConnectionState) {
        Log.e("%%%%%", "onConnectionStateChange $state")
    }

    override fun onError(error: Modal.Model.Error) {
        Log.e("%%%%%", "onError $error")
    }

    override fun onProposalExpired(proposal: Modal.Model.ExpiredProposal) {
        Log.e("%%%%%", "onProposalExpired $proposal")
    }

    override fun onRequestExpired(request: Modal.Model.ExpiredRequest) {
        Log.e("%%%%%", "onRequestExpired $request")
    }

    override fun onSessionApproved(approvedSession: Modal.Model.ApprovedSession) {
        Log.e("%%%%%", "onSessionApproved $approvedSession")
    }

    override fun onSessionDelete(deletedSession: Modal.Model.DeletedSession) {
        Log.e("%%%%%", "onSessionDelete $deletedSession")
    }

    override fun onSessionEvent(sessionEvent: Modal.Model.SessionEvent) {
        Log.e("%%%%%", "onSessionEvent $sessionEvent")
    }

    override fun onSessionExtend(session: Modal.Model.Session) {
        Log.e("%%%%%", "onSessionExtend $session")
    }

    override fun onSessionRejected(rejectedSession: Modal.Model.RejectedSession) {
        Log.e("%%%%%", "onSessionRejected $rejectedSession")

    }

    override fun onSessionRequestResponse(response: Modal.Model.SessionRequestResponse) {
        Log.e("%%%%%", "onSessionRequestResponse $response")
    }

    override fun onSessionUpdate(updatedSession: Modal.Model.UpdatedSession) {
        Log.e("%%%%%", "onSessionUpdate $updatedSession")
    }

}