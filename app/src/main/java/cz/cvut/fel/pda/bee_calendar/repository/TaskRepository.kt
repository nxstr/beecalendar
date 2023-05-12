package cz.cvut.fel.pda.bee_calendar.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.bee_calendar.dao.TaskDao
import cz.cvut.fel.pda.bee_calendar.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepository (
    private val taskDao: TaskDao
    ) {
        fun getAllFlow(userId: Int): Flow<List<Task>> {
            return taskDao.getAllTasksFlow(userId)
        }

        fun getAllByDate(userId: Int, date: String): Flow<List<Task>> {
            return taskDao.getAllTasksByDateFlow(userId, date)
        }

        @WorkerThread
        suspend fun getAll(userId: Int): List<Task> {
            return taskDao.getAllTasks(userId)
        }

        @WorkerThread
        suspend fun insert(vararg Task: Task) {
            taskDao.insert(*Task)
        }

        @WorkerThread
        suspend fun update(Task: Task){
            taskDao.update(Task)
        }

        @WorkerThread
        suspend fun delete(id: Int) {
            taskDao.delete(id)
        }

        @WorkerThread
        suspend fun getById(id: Int): Task? {
            return taskDao.getById(id)
        }

        @WorkerThread
        suspend fun getByName(name: String): List<Task> {
            return taskDao.getByName(name)
        }

        @WorkerThread
        suspend fun getAllTasksByCategory(categoryId: Int): List<Task> {
            return taskDao.getAllTasksByCategory(categoryId)
        }

        @WorkerThread
        suspend fun deleteAll() {
            taskDao.deleteAll()
        }
}