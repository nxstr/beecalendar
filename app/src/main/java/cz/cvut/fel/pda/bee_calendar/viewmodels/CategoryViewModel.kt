package cz.cvut.fel.pda.bee_calendar.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cz.cvut.fel.pda.bee_calendar.MainApp
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    loggedInSharedPreferences: SharedPreferences
) : ViewModel() {

    private val loggedInUserId = loggedInSharedPreferences.getInt("user-id", 0)

    val categoriesLiveData = categoryRepository.getAllFlow(loggedInUserId).asLiveData()

    suspend fun getById(id: Int): Category? {
        return categoryRepository.getById(id)
    }

    suspend fun getByName(name: String): Category? {
        return categoryRepository.getByName(loggedInUserId, name)
    }

    fun insert(categoryName: String) = viewModelScope.launch {
        val newCategory = Category(userId = loggedInUserId, name = categoryName)
        categoryRepository.insert(newCategory)
    }

    fun delete(id: Int) = viewModelScope.launch {
        categoryRepository.delete(id)
    }

    class CategoryViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                val mainApp = context.applicationContext as MainApp

                @Suppress("Unchecked_cast")
                return CategoryViewModel(
                    CategoryRepository(mainApp.categoryDao),
                    context.getSharedPreferences("logged-in-user", AppCompatActivity.MODE_PRIVATE)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}