package cz.cvut.fel.pda.bee_calendar.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityNewEventBinding
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityNewTaskBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityEnum
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatEnum
import cz.cvut.fel.pda.bee_calendar.utils.AlarmReceiver
import cz.cvut.fel.pda.bee_calendar.utils.EventActivityUtil
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NewTaskActivity : AppCompatActivity() {

    private lateinit var eventUtil: EventActivityUtil

    private lateinit var binding: ActivityNewTaskBinding
    private var task : Task? = null
    private var priorityValue: PriorityEnum = PriorityEnum.LOW
    private lateinit var alarmReceiver: AlarmReceiver
    private var submitDatePicked: LocalDate? = null
    private var remindDatePicked: LocalDate? = null
    private var catId: Int = 0

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModel.TaskViewModelFactory(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        alarmReceiver = AlarmReceiver()

        if(intent.extras!=null){
            task = intent.extras?.get("task-detail") as Task
            binding.title.text = "EDIT TASK"
            setData(task!!)
        }

        prioritySpinner()
        categorySpinner()
        eventUtil = EventActivityUtil()

        binding.submitDateButton.setOnClickListener {
            eventUtil.datePicker(binding.submitDate, this)
        }

        binding.remindDateButton.setOnClickListener {
            eventUtil.datePicker(binding.remindDate, this)
        }

        binding.submitTimeButton.setOnClickListener {
            eventUtil.timeTextView(binding.submitTime, this)
        }
        binding.remindTime.setOnClickListener {
            eventUtil.times(binding.remindTime, this)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_save) {
            if(validateName()) {
                if(validateDateTime()) {
                    Toast.makeText(
                        this,
                        "Saved", Toast.LENGTH_SHORT
                    ).show()
                    if(task!=null){
                        setResultForUpdate(task!!)
                        startActivity(Intent(this, MainActivity::class.java))
                    }else {
                        setResult()
                        finish()
                    }
                }
            }else{
                Toast.makeText(this,
                    "Event name must be unique!", Toast.LENGTH_SHORT).show()
            }
        } else if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setData(task: Task){
        binding.taskName.setText(task.name)
        binding.submitDate.text = task.date
        submitDatePicked = LocalDate.parse(task.date, DateTimeFormatter.ISO_DATE)
        binding.submitTime.text = task.deadlineTime

        if(task.notes!=null){
            binding.notes.setText(task.notes)
        }
        if(task.remind!=""){
            binding.remindDate.text = task.remind.split("/").get(0)
            remindDatePicked = LocalDate.parse(task.remind.split("/").get(0), DateTimeFormatter.ISO_DATE)
            binding.remindTime.text = task.remind.split("/").get(1)
        }

        binding.taskCheckbox.isChecked = !task.isActive

        deleteReminder(task)
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

    private fun prioritySpinner() {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, PriorityEnum.values())

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.priority.adapter = arrayAdapter

        if(task!=null){
            runBlocking {
                PriorityEnum.values().forEach { catWrap ->
                    if (task!!.priority == catWrap) {
                        binding.priority.setSelection(arrayAdapter.getPosition(catWrap))
                        return@forEach
                    }
                }
            }
        }

        binding.priority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                priorityValue = PriorityEnum.LOW
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                priorityValue = PriorityEnum.values().get(position)
            }
        }
    }

    private fun categorySpinner(){
        val nameObserver = androidx.lifecycle.Observer<List<Category>> { newName ->
            val arr = kotlin.collections.ArrayList<String>()
            for(i in newName){
                arr.add(i.name)
            }
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arr)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.category.adapter = arrayAdapter

            if(task!=null){
                runBlocking {
                    arr.forEach { catWrap ->
                        if (categoryViewModel.getById(task!!.categoryId)?.name == catWrap) {
                            binding.category.setSelection(arrayAdapter.getPosition(catWrap))
                            catId = task!!.categoryId
                            return@forEach
                        }
                    }
                }
            }

            binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    catId = newName.get(p2).id!!
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    runBlocking {
                        catId = categoryViewModel.getByName("default")?.id!!
                    }
                }
            }
        }
        categoryViewModel.categoriesLiveData.observe(this, nameObserver)
    }


    private fun setResultForUpdate(task: Task){

        task.name = binding.taskName.text.toString()
        task.date = submitDatePicked.toString()
        task.deadlineTime = binding.submitTime.text.toString()
        task.notes = binding.notes.text.toString()
        task.categoryId = catId
        if(remindDatePicked==null){
            task.remind = ""
        }else{
            task.remind = remindDatePicked.toString() + "/" + binding.remindTime.text.toString()
            if(!binding.taskCheckbox.isChecked) {
                alarmReceiver.setReminder(
                    this,
                    LocalDateTime.parse(
                        binding.remindDate.text.toString() + " " + binding.remindTime.text.toString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    ).atZone(
                        ZoneId.systemDefault()
                    ).toInstant().toEpochMilli() + 16,
                    binding.submitTime.text.toString() + " " + binding.taskName.text.toString(),
                    "task"
                )
            }
        }
        task.priority = priorityValue
        task.isActive = !binding.taskCheckbox.isChecked

        taskViewModel.updateTask(task)
        if(!binding.taskCheckbox.isChecked) {
            alarmReceiver.setReminder(
                this,
                LocalDateTime.parse(
                    binding.submitDate.text.toString() + " " + binding.submitTime.text.toString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                ).atZone(
                    ZoneId.systemDefault()
                ).toInstant().toEpochMilli() + 15,
                binding.submitTime.text.toString() + " " + binding.taskName.text.toString(),
                "task"
            )
        }

    }
    private fun setResult(){
        var rem = ""
        if(binding.remindDate.text.toString()!="" && binding.remindDate.text.toString()!="date" && !binding.taskCheckbox.isChecked){
            rem = binding.remindDate.text.toString() + "/" + binding.remindTime.text.toString()
            alarmReceiver.setReminder(this, LocalDateTime.parse(binding.remindDate.text.toString()+" "+binding.remindTime.text.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
                ZoneId.systemDefault()).toInstant().toEpochMilli()+16, binding.submitTime.text.toString() +" "+binding.taskName.text.toString(), "task")
        }
        taskViewModel.insertTask(
            Task(
                name = binding.taskName.text.toString(),
                date = binding.submitDate.text.toString(),
                remind = rem,
                notes = binding.notes.text.toString(),
                categoryId = catId,
                userId = taskViewModel.loggedUser?.id!!,
                deadlineTime = binding.submitTime.text.toString(),
                isActive = !binding.taskCheckbox.isChecked,
                priority = priorityValue
            )
        )
        if(!binding.taskCheckbox.isChecked) {
            alarmReceiver.setReminder(
                this,
                LocalDateTime.parse(
                    binding.submitDate.text.toString() + " " + binding.submitTime.text.toString(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                ).atZone(
                    ZoneId.systemDefault()
                ).toInstant().toEpochMilli() + 15,
                binding.submitTime.text.toString() + " " + binding.taskName.text.toString(),
                "task"
            )
        }
    }

    private fun validateDateTime():Boolean{
        if(binding.submitDate.text.toString()!="deadline date"){
            submitDatePicked = LocalDate.parse(binding.submitDate.text, DateTimeFormatter.ISO_DATE)
        }
        if(binding.remindDate.text.toString()!="date"){
            remindDatePicked = LocalDate.parse(binding.remindDate.text, DateTimeFormatter.ISO_DATE)
        }
        if(submitDatePicked==null){
            Toast.makeText(this,
                "Date is necessary!", Toast.LENGTH_SHORT).show()
            return false
        }
        if(binding.submitTime.text.equals("deadline time")){
            Toast.makeText(this,
                "Time is necessary!", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!binding.remindTime.text.equals("time") && binding.remindDate.text.toString()=="date"){
            Toast.makeText(this,
                "Remind must have date, not only time!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun validateName(): Boolean{
        var exists = true
        runBlocking {
            if((task!=null && binding.taskName.text.toString()!= task!!.name) || (task==null)){
                if (!taskViewModel.getByName(binding.taskName.text.toString()).isEmpty()) {
                    exists = false
                }
            }
        }
        return exists
    }


}