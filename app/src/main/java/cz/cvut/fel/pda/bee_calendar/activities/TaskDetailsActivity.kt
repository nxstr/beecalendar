package cz.cvut.fel.pda.bee_calendar.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.TaskDetailsBinding
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityEnum
import cz.cvut.fel.pda.bee_calendar.utils.AlarmReceiver
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TaskDetailsActivity: AppCompatActivity() {
    private lateinit var binding: TaskDetailsBinding
    private lateinit var task : Task
    private lateinit var alarmReceiver: AlarmReceiver

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModel.TaskViewModelFactory(this)
    }

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = TaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        task = intent.extras?.get("task-detail") as Task
        alarmReceiver = AlarmReceiver()
        setData()
    }

    private fun setData(){
        binding.taskName.text = task.name
        binding.submitDate.text = task.date
        binding.time.text = task.deadlineTime
        binding.notes.text = task.notes

        runBlocking {
            binding.categoryName.text = categoryViewModel.getById(task.categoryId)?.name
        }
        if(task.remind!="") {
            binding.remind.text = task.remind.split("/").get(0) + " " + task.remind.split("/").get(1)
        }
        binding.priority.text = PriorityEnum.valueOf(task.priority.toString()).text
        if(task.isActive){
            binding.state.text = "active"
        }else{
            binding.state.text = "completed"
        }
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
                val arr = taskViewModel.getByName(task.name)
                for(i in arr){
                    i.id?.let { taskViewModel.deleteTask(it) }
                    deleteReminder(i)
                }
            }
            finish()
        } else if (item.itemId == android.R.id.home) {
            finish()
        }else if (item.itemId == R.id.navigation_item_edit) {
            Toast.makeText(this,
                "Edit", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NewTaskActivity::class.java).apply {
                putExtra("task-detail", task)
            }
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteReminder(task: Task){
        alarmReceiver.cancelReminder(this, LocalDateTime.parse(task.date+" "+task.deadlineTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
            ZoneId.systemDefault()).toInstant().toEpochMilli()+15)

        if(task.remind!=""){
            val remDate = task.remind.split("/").get(0)
            val remTime = task.remind.split("/").get(1)
            alarmReceiver.cancelReminder(this, LocalDateTime.parse(remDate+" "+remTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
                ZoneId.systemDefault()).toInstant().toEpochMilli()+16)

        }

    }

}