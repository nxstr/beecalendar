package cz.cvut.fel.pda.bee_calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.cvut.fel.pda.bee_calendar.dao.CategoryDao
import cz.cvut.fel.pda.bee_calendar.dao.EventDao
import cz.cvut.fel.pda.bee_calendar.dao.TaskDao
import cz.cvut.fel.pda.bee_calendar.dao.UserDao
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityConverter
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatConverter

@Database(entities = [Category::class, Event::class, Task::class, User::class], version = 1)
@TypeConverters(RepeatConverter::class, PriorityConverter::class)

abstract class MainDB : RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDao
    abstract fun getEventDao(): EventDao
    abstract fun getTaskDao(): TaskDao
    abstract fun getUserDao(): UserDao

    companion object {
        @Volatile
        private var dbInstance: MainDB? = null
        fun getDB(context: Context): MainDB {
            return dbInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDB::class.java,
                    "bee_calendar.db"
                ).fallbackToDestructiveMigration().build()
                instance
            }
        }
    }
}