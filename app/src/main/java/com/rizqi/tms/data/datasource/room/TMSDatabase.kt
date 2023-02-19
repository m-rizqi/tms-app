package com.rizqi.tms.data.datasource.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rizqi.tms.data.datasource.room.dao.*
import com.rizqi.tms.data.datasource.room.entities.*

private const val TMS_DATABASE = "tms_database"

@Database(
    entities = [
        UserEntity::class, UnitEntity::class,
        SpecialPriceEntity.MerchantSpecialPriceEntity::class,
        SpecialPriceEntity.ConsumerSpecialPriceEntity::class,
        SubPriceEntity.MerchantSubPriceEntity::class,
        SubPriceEntity.ConsumerSubPriceEntity::class,
        PriceEntity::class,
        ItemEntity::class,
        SearchHistoryEntity::class,
        ItemInCashierEntity::class,
        TransactionEntity::class,
        AppBluetoothDeviceEntity::class,
        BillSettingEntity::class
        ],
    version = 1,
    exportSchema = false
)
@TypeConverters(EntityConverters::class)
abstract class TMSDatabase : RoomDatabase(){
    abstract fun unitDao() : UnitDao
    abstract fun itemDao() : ItemDao
    abstract fun priceDao() : PriceDao
    abstract fun searchHistoryDao() : SearchHistoryDao
    abstract fun transactionDao() : TransactionDao
    abstract fun itemInCashierDao() : ItemInCashierDao
    abstract fun appBluetoothDeviceDao() : AppBluetoothDeviceDao
    abstract fun billSettingDao() : BillSettingDao
    abstract fun specialPriceDao() : SpecialPriceDao
    abstract fun subPriceDao() : SubPriceDao

    companion object {
        @Volatile
        private var INSTANCE : TMSDatabase? = null
        fun getDatabase(context: Context) : TMSDatabase {
            return INSTANCE ?: synchronized(this){
                val instance =  Room.databaseBuilder(
                    context.applicationContext,
                    TMSDatabase::class.java,
                    TMS_DATABASE
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}