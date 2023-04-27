package com.rizqi.tms.di

import com.rizqi.tms.domain.login.SignInWithEmailAndPasswordUseCase
import com.rizqi.tms.domain.login.SignInWithEmailAndPasswordUseCaseImpl
import com.rizqi.tms.domain.restore.RestoreDatabaseUseCase
import com.rizqi.tms.domain.restore.RestoreDatabaseUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    fun provideSignInWithEmailAndPasswordUseCase(
        signInWithEmailAndPasswordUseCaseImpl: SignInWithEmailAndPasswordUseCaseImpl
    ) : SignInWithEmailAndPasswordUseCase = signInWithEmailAndPasswordUseCaseImpl

    @Provides
    fun provideRestoreDatabaseUseCase(
        restoreDatabaseUseCaseImpl: RestoreDatabaseUseCaseImpl
    ) : RestoreDatabaseUseCase = restoreDatabaseUseCaseImpl
}