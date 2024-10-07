package com.example.parlimentapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.parlimentapp.data.dao.ParliamentMemberDao
import com.example.parlimentapp.data.entity.ParliamentMemberEntity

@Database(entities = [ParliamentMemberEntity::class], version = 3, exportSchema = false)
abstract class ParliamentDatabase : RoomDatabase() {

    abstract fun parliamentMemberDao(): ParliamentMemberDao

    companion object {
        @Volatile
        private var INSTANCE: ParliamentDatabase? = null

        // Migration from version 1 to 2 (if necessary)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // You can add any schema changes from version 1 to 2 here, if applicable
            }
        }

        // Migration from version 2 to 3: adding the new columns
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val cursor = database.query("PRAGMA table_info(parliament_members)")

                val existingColumns = mutableSetOf<String>()
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    existingColumns.add(columnName)
                }
                cursor.close()

                // Add columns if they don't exist
                if (!existingColumns.contains("vote")) {
                    database.execSQL("ALTER TABLE parliament_members ADD COLUMN vote INTEGER NOT NULL DEFAULT 0")
                }
                if (!existingColumns.contains("totalVotes")) {
                    database.execSQL("ALTER TABLE parliament_members ADD COLUMN totalVotes INTEGER NOT NULL DEFAULT 0")
                }
                if (!existingColumns.contains("note")) {
                    database.execSQL("ALTER TABLE parliament_members ADD COLUMN note TEXT NOT NULL DEFAULT ''")
                }
                if (!existingColumns.contains("twitterHandle")) {
                    database.execSQL("ALTER TABLE parliament_members ADD COLUMN twitterHandle TEXT")
                }
            }
        }


        fun getDatabase(context: Context): ParliamentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ParliamentDatabase::class.java,
                    "parliament_database"
                )
                    // Fallback to destructive migration
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}

