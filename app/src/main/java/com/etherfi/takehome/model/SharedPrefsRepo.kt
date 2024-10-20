package com.etherfi.takehome.model

interface SharedPrefsRepo {

    fun setAuthorization(isAuthorized: Boolean)

    fun isAuthorized(): Boolean
}