package com.ekosoftware.secretdms.injection

import com.ekosoftware.secretdms.data.auth.AuthenticationDataSource
import com.ekosoftware.secretdms.domain.AuthenticationRepository
import com.ekosoftware.secretdms.domain.DefaultAuthenticationRepository
import com.ekosoftware.secretdms.domain.DefaultMessagesRepository
import com.ekosoftware.secretdms.domain.MessagesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesSingletonModule {
    @Binds
    abstract fun bindMessagesRepository(implementation: DefaultMessagesRepository): MessagesRepository

    @Binds
    abstract fun bindAuthenticationRepository(implementation: DefaultAuthenticationRepository): AuthenticationRepository
}