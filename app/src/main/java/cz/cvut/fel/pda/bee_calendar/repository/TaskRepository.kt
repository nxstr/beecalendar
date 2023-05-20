package cz.cvut.fel.pda.bee_calendar.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.bee_calendar.dao.TaskDao
import cz.cvut.fel.pda.bee_calendar.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository (
    private val taskDao: TaskDao
    ) {

        fun getAllByDate(userId: Int, date: String): Flow<List<Task>> {
            return taskDao.getAllTasksByDateFlow(userId, date)
        }

        fun getAllByCat(userId: Int, catId: Int): Flow<List<Task>> {
            return taskDao.getAllTasksByCategoryFlow(userId, catId)
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
        suspend fun deleteAll() {
            taskDao.deleteAll()
        }
}