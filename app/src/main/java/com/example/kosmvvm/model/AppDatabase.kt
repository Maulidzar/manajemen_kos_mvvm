package com.example.kosmvvm.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [KamarEntity::class, PenghuniEntity::class],
    version = 6,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun kamarDao(): KamarDao
    abstract fun penghuniDao(): PenghuniDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kos_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun closeInstance() {
            INSTANCE?.let {
                if (it.isOpen) {
                    it.close()
                }
                INSTANCE = null
            }
        }

        fun forceCheckpoint(context: Context) {
            val db = getInstance(context.applicationContext)

            try {
                db.openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(FULL);")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}