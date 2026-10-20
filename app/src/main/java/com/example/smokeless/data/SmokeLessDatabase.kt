package com.example.smokeless.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SmokeEvent::class, QuitState::class],
    version = 1,
    exportSchema = false,
)
abstract class SmokeLessDatabase : RoomDatabase() {

    abstract fun smokeDao(): SmokeDao

    companion object {
        private const val DATABASE_NAME = "smokeless.db"

        @Volatile
        private var instance: SmokeLessDatabase? = null

        fun getInstance(context: Context): SmokeLessDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SmokeLessDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { database ->
                    instance = database
                }
            }
        }
    }
}
