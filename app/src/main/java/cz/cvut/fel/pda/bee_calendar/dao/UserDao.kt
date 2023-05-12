package cz.cvut.fel.pda.bee_calendar.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cz.cvut.fel.pda.bee_calendar.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM Users")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("SELECT * FROM Users WHERE id = :userId")
    suspend fun getByUserId(userId: Int): User

    @Insert
    suspend fun insert(vararg user: User)

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM Users WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM Users WHERE email = :email")
    suspend fun getByEmail(email: String): User?

    @Query("DELETE FROM Users")
    suspend fun deleteAll()
}