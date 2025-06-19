package it.qbr.testapisudoku.db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Game::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun partitaDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
    //  Singleton pattern to ensure only one instance of the database is created

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "game_db"
                            ).fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

