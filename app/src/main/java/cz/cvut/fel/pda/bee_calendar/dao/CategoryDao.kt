package cz.cvut.fel.pda.bee_calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cz.cvut.fel.pda.bee_calendar.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM Categories WHERE userId = :userId")
    fun getAllCategoriesFlow(userId: Int): Flow<List<Category>>

    @Query("SELECT * FROM Categories WHERE userId = :userId")
    suspend fun getAllCategories(userId: Int): List<Category>

    @Query("SELECT EXISTS(SELECT * FROM Categories WHERE name = :name)")
    fun existsByName(name : String) : Boolean

    @Insert
    suspend fun insert(vararg category: Category)

    @Query ("DELETE FROM Categories WHERE id IS :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Categories WHERE id = :id")
    suspend fun getById(id: Int): Category?

    @Query("SELECT * FROM Categories WHERE name = :name")
    suspend fun getByName(name: String): Category?

    @Query("DELETE FROM Categories")
    suspend fun deleteAll()
}