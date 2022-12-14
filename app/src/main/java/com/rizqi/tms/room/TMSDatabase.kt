package com.rizqi.tms.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        Transaction::class,
        AppBluetoothDevice::class,
        BillItem::class
        ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TMSDatabase : RoomDatabase(){
    abstract fun userDao() : UserDao
    abstract fun unitDao() : UnitDao
    abstract fun itemDao() : ItemDao
    abstract fun searchHistoryDao() : SearchHistoryDao
    abstract fun transactionDao() : TransactionDao
    abstract fun appBluetoothDeviceDao() : AppBluetoothDeviceDao
    abstract fun billItemDao() : BillItemDao

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
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE `Transaction` ADD COLUMN pay INTEGER NOT NULL DEFAULT 0"
        )
        database.execSQL(
            "ALTER TABLE `Transaction` ADD COLUMN change_money INTEGER NOT NULL DEFAULT 0"
        )
    }

}