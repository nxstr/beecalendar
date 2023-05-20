package cz.cvut.fel.pda.bee_calendar.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.bee_calendar.MainApp
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.repository.CategoryRepository
import cz.cvut.fel.pda.bee_calendar.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserViewModel(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    loggedInSharedPreferences: SharedPreferences
) : ViewModel() {

    var loggedUser: User? = null

    init {
        val loggedInUserId = loggedInSharedPreferences.getInt("user-id", 0)
        if (loggedInUserId != 0) {
            runBlocking {
                loggedUser = userRepository.getByUserId(loggedInUserId)
            }
        }
    }

    suspend fun insert(user: User): User {
        userRepository.insert(user)
        val createdUser = userRepository.getByEmail(user.email)!!
        categoryRepository.insert(
            Category(
                userId = createdUser.id!!,
                name = "default"
            )
        )
        return createdUser
    }

    fun delete(id: Int) = viewModelScope.launch {
        userRepository.delete(id)
    }

    suspend fun getByEmail(email: String): User? {
        return userRepository.getByEmail(email)
    }

    suspend fun getById(id: Int): User?{
        return userRepository.getByUserId(id);
    }


    fun updateUser() = viewModelScope.launch {
        userRepository.update(loggedUser!!)
        println("user model============== " + loggedUser!!.firstName)
    }

    class UserViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                val mainApp = context.applicationContext as MainApp
                @Suppress("Unchecked_cast")
                return UserViewModel(
                    UserRepository(mainApp.userDao),
                    CategoryRepository(mainApp.categoryDao),
                    context.getSharedPreferences("logged-in-user", AppCompatActivity.MODE_PRIVATE)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}