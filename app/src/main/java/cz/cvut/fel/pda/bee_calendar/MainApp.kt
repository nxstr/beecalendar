package cz.cvut.fel.pda.bee_calendar

import android.app.Application

class MainApp : Application() {
    private val database by lazy { MainDB.getDB(this) }
    val categoryDao by lazy { database.getCategoryDao() }
    val eventDao by lazy { database.getEventDao() }
    val taskDao by lazy { database.getTaskDao() }
    val userDao by lazy { database.getUserDao() }
}