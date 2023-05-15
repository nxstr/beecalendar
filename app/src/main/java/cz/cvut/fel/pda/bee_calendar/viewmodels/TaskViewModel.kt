package cz.cvut.fel.pda.bee_calendar.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import cz.cvut.fel.pda.bee_calendar.MainApp
import cz.cvut.fel.pda.bee_calendar.dao.TaskDao
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.repository.CategoryRepository
import cz.cvut.fel.pda.bee_calendar.repository.TaskRepository
import cz.cvut.fel.pda.bee_calendar.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class TaskViewModel (
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    loggedInSharedPreferences: SharedPreferences
) : ViewModel() {

    private val loggedInUserId = loggedInSharedPreferences.getInt("user-id", 0)

    val allCategoriesLiveData = categoryRepository.getAllFlow(loggedInUserId).asLiveData()
    val allTasksLiveData = taskRepository.getAllFlow(loggedInUserId).asLiveData()

    var loggedUser: User? = null
    init {
        val loggedInUserId = loggedInSharedPreferences.getInt("user-id", 0)
        if (loggedInUserId != 0) {
            runBlocking {
                loggedUser = userRepository.getByUserId(loggedInUserId)
            }
        }
    }

    fun getTasksByDate(date: LocalDate): LiveData<List<Task>> {
        return taskRepository.getAllByDate(loggedInUserId, date.toString()).asLiveData()
    }

    fun getTasksByCatFlow(catId: Int): LiveData<List<Task>>{
        return taskRepository.getAllByCat(loggedInUserId, catId).asLiveData()
    }

    suspend fun getByName(name: String): List<Task>{
        return taskRepository.getByName(name)
    }

    fun insertTask(newTask: Task) = viewModelScope.launch {
        taskRepository.insert(newTask)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    fun deleteTask(id: Int) = viewModelScope.launch {
        taskRepository.delete(id)
    }

    fun deleteAll() = viewModelScope.launch {
        taskRepository.deleteAll()
    }

    class TaskViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                val mainApp = context.applicationContext as MainApp

                @Suppress("Unchecked_cast")
                return TaskViewModel(
                    TaskRepository(mainApp.taskDao),
                    CategoryRepository(mainApp.categoryDao),
                    UserRepository(mainApp.userDao),
                    context.getSharedPreferences("logged-in-user", AppCompatActivity.MODE_PRIVATE)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}