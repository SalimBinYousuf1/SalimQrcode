package com.salim.qr.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CodeRecord::class], version = 1, exportSchema = false)
abstract class SalimDatabase : RoomDatabase() {
    abstract fun codeRecordDao(): CodeRecordDao

    companion object {
        @Volatile
        private var INSTANCE: SalimDatabase? = null

        fun getInstance(context: Context): SalimDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SalimDatabase::class.java,
                    "salim_qr.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
