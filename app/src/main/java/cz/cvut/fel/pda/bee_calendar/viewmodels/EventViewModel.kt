package cz.cvut.fel.pda.bee_calendar.viewmodels


import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import cz.cvut.fel.pda.bee_calendar.MainApp
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.repository.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class EventViewModel(
    private val eventRepository: EventRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    loggedInSharedPreferences: SharedPreferences
) : ViewModel() {

    private val loggedInUserId = loggedInSharedPreferences.getInt("user-id", 0)

    val allCategoriesLiveData = categoryRepository.getAllFlow(loggedInUserId).asLiveData()
    val allEventLiveData = eventRepository.getAllFlow(loggedInUserId).asLiveData()

    var loggedUser: User? = null
    init {
        val loggedInUserId = loggedInSharedPreferences.getInt("user-id", 0)
        if (loggedInUserId != 0) {
            runBlocking {
                loggedUser = userRepository.getByUserId(loggedInUserId)
            }
        }
    }

    fun getEventsByDate(date: LocalDate): LiveData<List<Event>> {
        return eventRepository.getAllByDate(loggedInUserId, date.toString()).asLiveData()
    }

    fun getEventsByCatFlow(catId: Int): LiveData<List<Event>>{
        return eventRepository.getAllByCat(loggedInUserId, catId).asLiveData()
    }

    fun insertEvent(newEvent: Event) = viewModelScope.launch {
        eventRepository.insert(newEvent)
    }

    fun updateEvent(event: Event) = viewModelScope.launch {
        eventRepository.update(event)
    }

    fun deleteEvent(id: Int) = viewModelScope.launch {
        eventRepository.delete(id)
    }

//    fun deleteCategory(categoryId: Int) = viewModelScope.launch {
//        categoryRepository.delete(categoryId)
//
////        for(event in eventRepository.getAllEventsByCategory(categoryId)) {
////            eventRepository.delete(event.id)
////        }
//    }

    fun deleteAll() = viewModelScope.launch {
        eventRepository.deleteAll()
    }

    suspend fun getByName(name: String): List<Event>{
        return eventRepository.getByName(name)
    }

    class EventViewModelFactory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
                val mainApp = context.applicationContext as MainApp

                @Suppress("Unchecked_cast")
                return EventViewModel(
                    EventRepository(mainApp.eventDao),
                    CategoryRepository(mainApp.categoryDao),
                    UserRepository(mainApp.userDao),
                    context.getSharedPreferences("logged-in-user", AppCompatActivity.MODE_PRIVATE)
                ) as T
            }
            throw IllegalArgumentException("Unknown_ViewModelClass")
        }
    }
}