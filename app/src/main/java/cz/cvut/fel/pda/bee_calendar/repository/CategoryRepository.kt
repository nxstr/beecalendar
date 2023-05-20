package cz.cvut.fel.pda.bee_calendar.repository

import androidx.annotation.WorkerThread
import cz.cvut.fel.pda.bee_calendar.dao.CategoryDao
import cz.cvut.fel.pda.bee_calendar.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    fun getAllFlow(userId: Int): Flow<List<Category>> {
        return categoryDao.getAllCategoriesFlow(userId)
    }

    @WorkerThread
    suspend fun insert(vararg category: Category) {
        categoryDao.insert(*category)
    }

    @WorkerThread
    suspend fun delete(id: Int) {
        categoryDao.delete(id)
    }

    @WorkerThread
    suspend fun getById(id: Int): Category? {
        return categoryDao.getById(id)
    }

    @WorkerThread
    suspend fun getByName(userId: Int, name: String): Category? {
        return categoryDao.getByName(userId, name)
    }

    @WorkerThread
    suspend fun deleteAll(){
        categoryDao.deleteAll()
    }
}