package cz.cvut.fel.pda.bee_calendar.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.bee_calendar.dao.EventDao
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class EventRepository(
    private val eventDao: EventDao
) {
    fun getAllFlow(userId: Int): Flow<List<Event>> {
        return eventDao.getAllEventsFlow(userId)
    }

    fun getAllByDate(userId: Int, date: String): Flow<List<Event>> {
        return eventDao.getAllEventsByDateFlow(userId, date)
    }

    fun getAllByCat(userId: Int, catId: Int): Flow<List<Event>> {
        return eventDao.getAllEventsByCategoryFlow(userId, catId)
    }

    @WorkerThread
    suspend fun getAll(userId: Int): List<Event> {
        return eventDao.getAllEvents(userId)
    }

    @WorkerThread
    suspend fun insert(vararg event: Event) {
        eventDao.insert(*event)
    }

    @WorkerThread
    suspend fun update(event: Event){
        eventDao.update(event)
    }

    @WorkerThread
    suspend fun delete(id: Int) {
        eventDao.delete(id)
    }

    @WorkerThread
    suspend fun getById(id: Int): Event? {
        return eventDao.getById(id)
    }

    @WorkerThread
    suspend fun getByName(name: String): List<Event> {
        return eventDao.getByName(name)
    }

    @WorkerThread
    suspend fun getAllEventsByCategory(categoryId: Int): List<Event> {
        return eventDao.getAllEventsByCategory(categoryId)
    }

    @WorkerThread
    suspend fun deleteAll() {
        eventDao.deleteAll()
    }
}