package cz.cvut.fel.pda.bee_calendar.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.EventDetailsBinding
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatEnum
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import kotlinx.coroutines.runBlocking

class EventDetailsActivity: AppCompatActivity() {
    private lateinit var binding: EventDetailsBinding
    private lateinit var event : Event

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = EventDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        event = intent.extras?.get("event-detail") as Event
        setData()
    }

    private fun setData(){
        binding.eventName.text = event.name
        binding.submitDate.text = event.date
        binding.timeFrom.text = event.timeFrom
        binding.timeTo.text = event.timeTo
        binding.location.text = event.location
        binding.notes.text = event.notes

        runBlocking {
            binding.categoryName.text = categoryViewModel.getById(event.categoryId)?.name
        }
        binding.remind.text = event.remind
        binding.repeatType.text = RepeatEnum.valueOf(event.repeatEnum.toString()).text
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.info_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_item_delete) {
            Toast.makeText(this,
                "Delete", Toast.LENGTH_SHORT).show()
            runBlocking {
                val arr = eventViewModel.getByName(event.name)
                for(i in arr){
                    i.id?.let { eventViewModel.deleteEvent(it) }
                }
            }
            finish()
        } else if (item.itemId == android.R.id.home) {
            finish()
        }else if (item.itemId == R.id.navigation_item_edit) {
            Toast.makeText(this,
                "Edit", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NewEventActivity::class.java).apply {
                putExtra("event-detail", event)
            }
            startActivity(intent)
//            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}