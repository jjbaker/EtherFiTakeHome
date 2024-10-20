package com.etherfi.takehome.model.di

import com.etherfi.takehome.model.AuthorizationRepo
import com.etherfi.takehome.model.AuthorizationRepoImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AuthorizationModule {

    @Provides
    fun providesAuthorizationRepo(): AuthorizationRepo {
        return AuthorizationRepoImpl()
    }
}