package com.rizqi.tms.di

import android.content.Context
import com.rizqi.tms.R
import com.rizqi.tms.data.datasource.firebase.auth.FirebaseAuthentication
import com.rizqi.tms.data.datasource.firebase.auth.MainFirebaseAuthentication
import com.rizqi.tms.data.datasource.firebase.database.item.ItemFirebaseDatabase
import com.rizqi.tms.data.datasource.firebase.database.item.MainItemFirebaseDatabase
import com.rizqi.tms.data.datasource.firebase.database.transaction.MainTransactionFirebaseDatabase
import com.rizqi.tms.data.datasource.firebase.database.transaction.TransactionFirebaseDatabase
import com.rizqi.tms.data.datasource.firebase.database.unit.MainUnitFirebaseDatabase
import com.rizqi.tms.data.datasource.firebase.database.unit.UnitFirebaseDatabase
import com.rizqi.tms.data.datasource.room.TMSDatabase
import com.rizqi.tms.data.datasource.room.dao.*
import com.rizqi.tms.data.datasource.storage.images.ImageStorageDataSource
import com.rizqi.tms.data.datasource.storage.images.MainImageStorageDataSource
import com.rizqi.tms.data.repository.item.ItemRepository
import com.rizqi.tms.data.repository.item.MainItemRepository
import com.rizqi.tms.data.repository.itemincashier.ItemInCashierRepository
import com.rizqi.tms.data.repository.itemincashier.MainItemInCashierRepository
import com.rizqi.tms.data.repository.price.MainPriceRepository
import com.rizqi.tms.data.repository.price.PriceRepository
import com.rizqi.tms.data.repository.specialprice.MainSpecialPriceRepository
import com.rizqi.tms.data.repository.specialprice.SpecialPriceRepository
import com.rizqi.tms.data.repository.subprice.MainSubPriceRepository
import com.rizqi.tms.data.repository.subprice.SubPriceRepository
import com.rizqi.tms.data.repository.transaction.MainTransactionRepository
import com.rizqi.tms.data.repository.transaction.TransactionRepository
import com.rizqi.tms.data.repository.unit.MainUnitRepository
import com.rizqi.tms.data.repository.unit.UnitRepository
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
    fun provideUnitDao(
        @ApplicationContext context: Context
    ) : UnitDao = provideTMSDatabase(context).unitDao()

    @Singleton
    @Provides
    fun provideItemInCashierDao(
        @ApplicationContext context: Context
    ) : ItemInCashierDao = provideTMSDatabase(context).itemInCashierDao()

    @Singleton
    @Provides
    fun provideTransactionDao(
        @ApplicationContext context: Context
    ) : TransactionDao = provideTMSDatabase(context).transactionDao()

    @Singleton
    @Provides
    fun provideFirebaseAuthentication(
        @ApplicationContext context: Context,
        @FirebaseWebClientId firebaseWebClientId : String
    ) : FirebaseAuthentication {
        return MainFirebaseAuthentication(firebaseWebClientId, context)
    }

    @Singleton
    @Provides
    fun provideItemFirebaseDatabase() : ItemFirebaseDatabase = MainItemFirebaseDatabase()

    @Singleton
    @Provides
    fun provideTransactionFirebaseDatabase() : TransactionFirebaseDatabase = MainTransactionFirebaseDatabase()

    @Singleton
    @Provides
    fun provideUnitFirebaseDatabase() : UnitFirebaseDatabase = MainUnitFirebaseDatabase()

    @Singleton
    @Provides
    fun provideItemRepository(
        mainItemRepository: MainItemRepository
    ) : ItemRepository = mainItemRepository

    @Singleton
    @Provides
    fun providePriceRepository(
        mainPriceRepository: MainPriceRepository
    ) : PriceRepository = mainPriceRepository

    @Singleton
    @Provides
    fun provideSubPriceRepository(
        mainSubPriceRepository: MainSubPriceRepository
    ) : SubPriceRepository = mainSubPriceRepository

    @Singleton
    @Provides
    fun provideSpecialPriceRepository(
        mainSpecialPriceRepository: MainSpecialPriceRepository
    ) : SpecialPriceRepository = mainSpecialPriceRepository

    @Singleton
    @Provides
    fun provideItemInCashierRepository(
        mainItemInCashierRepository: MainItemInCashierRepository
    ) : ItemInCashierRepository = mainItemInCashierRepository

    @Singleton
    @Provides
    fun provideTransactionRepository(
        mainTransactionRepository: MainTransactionRepository
    ) : TransactionRepository = mainTransactionRepository

    @Singleton
    @Provides
    fun provideUnitRepository(
        mainUnitRepository: MainUnitRepository
    ) : UnitRepository = mainUnitRepository

    @Singleton
    @Provides
    fun provideImageStorageDataSource(
        mainImageStorageDataSource: MainImageStorageDataSource
    ) : ImageStorageDataSource = mainImageStorageDataSource
}