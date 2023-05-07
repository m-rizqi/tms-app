package com.rizqi.tms.di

import com.rizqi.tms.domain.item.CheckItemNameAlreadyExistUseCase
import com.rizqi.tms.domain.item.CheckItemNameAlreadyExistUseCaseImpl
import com.rizqi.tms.domain.item.CreateItemUseCase
import com.rizqi.tms.domain.item.CreateItemUseCaseImpl
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

    @Provides
    fun provideCreateItemUseCase(
        createItemUseCaseImpl: CreateItemUseCaseImpl
    ) : CreateItemUseCase = createItemUseCaseImpl

    @Provides
    fun provideCheckItemNameAlreadyExistUseCase(
        checkItemNameAlreadyExistUseCaseImpl: CheckItemNameAlreadyExistUseCaseImpl
    ) : CheckItemNameAlreadyExistUseCase = checkItemNameAlreadyExistUseCaseImpl
}