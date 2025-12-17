package com.example.kosmvvm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Admin::class, RoomEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adminDao(): AdminDao
    abstract fun roomDao(): RoomDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 2 to 3 - add adminId column
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add adminId column to room table
                database.execSQL("ALTER TABLE room ADD COLUMN adminId TEXT NOT NULL DEFAULT 'default'")
            }
        }

        // Migration from version 3 to 4 - add roomKey primary key
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new table with auto-generated primary key
                database.execSQL("""
                    CREATE TABLE room_new (
                        roomKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        isOccupied INTEGER NOT NULL,
                        isPaid INTEGER,
                        lastPaymentDate TEXT,
                        maintenanceNeeds TEXT,
                        adminId TEXT NOT NULL
                    )
                """)
                
                // Copy data from old table to new table
                database.execSQL("""
                    INSERT INTO room_new (id, name, isOccupied, isPaid, lastPaymentDate, maintenanceNeeds, adminId)
                    SELECT id, name, isOccupied, isPaid, lastPaymentDate, maintenanceNeeds, adminId FROM room
                """)
                
                // Drop old table and rename new table
                database.execSQL("DROP TABLE room")
                database.execSQL("ALTER TABLE room_new RENAME TO room")
                
                // Create index on adminId
                database.execSQL("CREATE INDEX index_room_adminId ON room (adminId)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kos_database"
                )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 