package com.etherfi.takehome.model.impl

import android.content.SharedPreferences
import com.etherfi.takehome.model.SharedPrefsRepo
import javax.inject.Inject

class SharedPrefsRepoImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): SharedPrefsRepo {

    override fun setAuthorization(isAuthorized: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(ACCOUNT_AUTHORIZED_KEY, isAuthorized)
            apply()
        }
    }

    override fun isAuthorized(): Boolean {
        return sharedPreferences.getBoolean(ACCOUNT_AUTHORIZED_KEY, false)
    }


    companion object {
        const val ACCOUNT_AUTHORIZED_KEY = "account_authorized"
    }

}