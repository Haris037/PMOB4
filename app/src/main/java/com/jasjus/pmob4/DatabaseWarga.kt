package com.jasjus.pmob4

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Warga::class], version = 1, exportSchema = false)
abstract class DatabaseWarga : RoomDatabase() {
    abstract fun wargaDao(): WargaDao
    companion object {
        @Volatile
        private var INSTANCE: DatabaseWarga? = null
        fun getDatabase(context: Context): DatabaseWarga {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseWarga::class.java,
                    "db_warga"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}