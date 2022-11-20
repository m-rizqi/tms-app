package com.rizqi.tms.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rizqi.tms.model.*
import com.rizqi.tms.model.Unit
import com.rizqi.tms.utility.TMS_DATABASE

@Database(
    entities = [
        User::class, Unit::class,
        SpecialPrice.MerchantSpecialPrice::class,
        SpecialPrice.ConsumerSpecialPrice::class,
        SubPrice.MerchantSubPrice::class,
        SubPrice.ConsumerSubPrice::class,
        Price::class,
        Item::class,
        SearchHistory::class,
        ItemInCashier::class,
        Transaction::class
        ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TMSDatabase : RoomDatabase(){
    abstract fun userDao() : UserDao
    abstract fun unitDao() : UnitDao
    abstract fun itemDao() : ItemDao
    abstract fun searchHistoryDao() : SearchHistoryDao
    abstract fun transactionDao() : TransactionDao

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