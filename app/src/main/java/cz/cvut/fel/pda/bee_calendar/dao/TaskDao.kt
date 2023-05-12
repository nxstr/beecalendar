package cz.cvut.fel.pda.bee_calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.pda.bee_calendar.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    @Query("SELECT * FROM Tasks WHERE userId = :userId")
    suspend fun getAllTasks(userId: Int): List<Task>

    @Query("SELECT * FROM Tasks WHERE userId = :userId")
    fun getAllTasksFlow(userId: Int): Flow<List<Task>>

    @Query("SELECT * FROM Tasks WHERE userId = :userId AND date = :date")
    fun getAllTasksByDateFlow(userId: Int, date: String): Flow<List<Task>>

    @Query("SELECT * FROM Tasks WHERE id = :id")
    suspend fun getById(id: Int): Task?

    @Query("SELECT * FROM Tasks WHERE name = :name")
    suspend fun getByName(name: String): List<Task>

    @Query("SELECT * FROM Tasks WHERE categoryId = :categoryId")
    suspend fun getAllTasksByCategory(categoryId: Int): List<Task>

    @Insert
    suspend fun insert(vararg task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM Tasks WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM Tasks")
    suspend fun deleteAll()
}