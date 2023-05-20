package cz.cvut.fel.pda.bee_calendar.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.bee_calendar.dao.UserDao
import cz.cvut.fel.pda.bee_calendar.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    @WorkerThread
    suspend fun getByUserId(userId: Int): User {
        return userDao.getByUserId(userId)
    }

    @WorkerThread
    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    @WorkerThread
    suspend fun delete(id: Int) {
        userDao.delete(id)
    }

    @WorkerThread
    suspend fun getByEmail(email: String): User? {
        return userDao.getByEmail(email)
    }

    @WorkerThread
    suspend fun update(user: User) {
        userDao.update(user)
    }

    @WorkerThread
    suspend fun deleteAll(){
        userDao.deleteAll()
    }
}