package cz.cvut.fel.pda.bee_calendar.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.bee_calendar.dao.EventDao
import cz.cvut.fel.pda.bee_calendar.model.Event
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val eventDao: EventDao
) {

    fun getAllByDate(userId: Int, date: String): Flow<List<Event>> {
        return eventDao.getAllEventsByDateFlow(userId, date)
    }

    fun getAllByCat(userId: Int, catId: Int): Flow<List<Event>> {
        return eventDao.getAllEventsByCategoryFlow(userId, catId)
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
    suspend fun getByName(userId: Int, name: String): List<Event> {
        return eventDao.getByName(userId, name)
    }

    @WorkerThread
    suspend fun deleteAll() {
        eventDao.deleteAll()
    }
}