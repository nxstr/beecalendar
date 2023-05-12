package cz.cvut.fel.pda.bee_calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.pda.bee_calendar.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM Events WHERE userId = :userId")
    suspend fun getAllEvents(userId: Int): List<Event>

    @Query("SELECT * FROM Events WHERE userId = :userId")
    fun getAllEventsFlow(userId: Int): Flow<List<Event>>

    @Query("SELECT * FROM Events WHERE userId = :userId AND date = :date")
    fun getAllEventsByDateFlow(userId: Int, date: String): Flow<List<Event>>

    @Query("SELECT * FROM Events WHERE id = :id")
    suspend fun getById(id: Int): Event?

    @Query("SELECT * FROM Events WHERE name = :name")
    suspend fun getByName(name: String): List<Event>

    @Query("SELECT * FROM Events WHERE categoryId = :categoryId")
    suspend fun getAllEventsByCategory(categoryId: Int): List<Event>

    @Insert
    suspend fun insert(vararg event: Event)

    @Update
    suspend fun update(event: Event)

    @Query("DELETE FROM Events WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM Events")
    suspend fun deleteAll()
}