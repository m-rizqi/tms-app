package com.rizqi.tms.di

import com.rizqi.tms.domain.login.SignInWithEmailAndPasswordUseCase
import com.rizqi.tms.domain.login.SignInWithEmailAndPasswordUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideSignInWithEmailAndPasswordUseCase(
        signInWithEmailAndPasswordUseCaseImpl: SignInWithEmailAndPasswordUseCaseImpl
    ) : SignInWithEmailAndPasswordUseCase = signInWithEmailAndPasswordUseCaseImpl
}