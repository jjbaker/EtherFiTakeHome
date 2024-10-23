package com.etherfi.takehome.model.di

import android.content.Context
import android.content.SharedPreferences
import com.etherfi.takehome.model.AuthorizationRepo
import com.etherfi.takehome.model.impl.AuthorizationRepoImpl
import com.etherfi.takehome.model.SharedPrefsRepo
import com.etherfi.takehome.model.impl.SharedPrefsRepoImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface RepoModule {

    @Binds
    fun bindsAuthorizationRepo(authorizationRepoImpl: AuthorizationRepoImpl):AuthorizationRepo



    @Binds
    fun bindsSharedPrefsRepo(sharedPrefsRepoImpl: SharedPrefsRepoImpl): SharedPrefsRepo

    companion object {
        const val SHARED_PREFS_FILE_KEY = "com.example.version2.PREFERENCES"

        @Provides
        fun providesSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences(SHARED_PREFS_FILE_KEY, Context.MODE_PRIVATE)
        }
    }
}