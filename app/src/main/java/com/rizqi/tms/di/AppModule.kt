package com.rizqi.tms.di

import android.content.Context
import com.rizqi.tms.R
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.dao.ItemDao
import com.rizqi.tms.data.datasource.room.dao.PriceDao
import com.rizqi.tms.data.datasource.room.dao.SpecialPriceDao
import com.rizqi.tms.data.datasource.room.dao.SubPriceDao
import com.rizqi.tms.data.repository.specialprice.MainSpecialPriceRepository
import com.rizqi.tms.data.repository.specialprice.SpecialPriceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTMSDatabase(
        @ApplicationContext context: Context
    ) : TMSDatabase = TMSDatabase.getDatabase(context)

    @Singleton
    @Provides
    @FirebaseWebClientId
    fun provideFirebaseWebClientId(
        @ApplicationContext context: Context
    ) : String = context.getString(R.string.default_web_client_id)

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FirebaseWebClientId

    @Singleton
    @Provides
    fun provideItemDao(
        @ApplicationContext context: Context
    ) : ItemDao = provideTMSDatabase(context).itemDao()

    @Singleton
    @Provides
    fun providePriceDao(
        @ApplicationContext context: Context
    ) : PriceDao = provideTMSDatabase(context).priceDao()

    @Singleton
    @Provides
    fun provideSubPriceDao(
        @ApplicationContext context: Context
    ) : SubPriceDao = provideTMSDatabase(context).subPriceDao()

    @Singleton
    @Provides
    fun provideSpecialDao(
        @ApplicationContext context: Context
    ) : SpecialPriceDao = provideTMSDatabase(context).specialPriceDao()

    @Singleton
    @Provides
    fun provideSpecialPriceRepository(
        specialPriceDao: SpecialPriceDao
    ) : SpecialPriceRepository = MainSpecialPriceRepository(specialPriceDao)
}