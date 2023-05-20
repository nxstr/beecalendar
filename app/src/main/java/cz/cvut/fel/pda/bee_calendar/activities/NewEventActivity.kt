package cz.cvut.fel.pda.bee_calendar.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.bee_calendar.utils.AlarmReceiver
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityNewEventBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatEnum
import cz.cvut.fel.pda.bee_calendar.utils.EventActivityUtil
import cz.cvut.fel.pda.bee_calendar.utils.Vibrations
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import kotlinx.coroutines.runBlocking
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NewEventActivity : AppCompatActivity() {

    private lateinit var eventUtil: EventActivityUtil

    private lateinit var binding: ActivityNewEventBinding
    private var event : Event? = null
    private var repeatE: RepeatEnum = RepeatEnum.ONCE
    private lateinit var alarmReceiver: AlarmReceiver

    private var submitDatePicked: LocalDate? = null
    private var remindDatePicked: LocalDate? = null
    private var repeatDatePicked: LocalDate? = null

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }

    private var catId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("NEW EVENT")
        alarmReceiver = AlarmReceiver()

        if(intent.extras!=null){
            event = intent.extras?.get("event-detail") as Event
            supportActionBar?.setTitle("EDIT EVENT")
            setData(event!!)
        }

        repeatSpinner()
        categorySpinner()
        eventUtil = EventActivityUtil()
        binding.submitDateButton.setOnClickListener {
            eventUtil.datePicker(binding.submitDate, this)
        }

        binding.remindDateButton.setOnClickListener {
            eventUtil.datePicker(binding.remindDate, this)
        }

        binding.timeFrom.setOnClickListener {
            eventUtil.times(binding.timeFrom, this)
        }
        binding.timeTo.setOnClickListener {
            eventUtil.times(binding.timeTo, this)
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
                    if(event!=null){
                        setResultForUpdate(event!!)
                        startActivity(Intent(this, MainActivity::class.java))
                    }else {
                        setResult()
                        finish()
                    }
                }
            }else{
                Toast.makeText(this,
                    "Event name must be unique!", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@NewEventActivity)
            }
        } else if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setData(event: Event){
        binding.eventName.setText(event.name)
        binding.submitDate.text = event.date
        submitDatePicked = LocalDate.parse(event.date, DateTimeFormatter.ISO_DATE)
        binding.timeFrom.text = event.timeFrom
        binding.timeTo.text = event.timeTo
        if(event.location!=null){
            binding.location.setText(event.location)
        }
        if(event.notes!=null){
            binding.notes.setText(event.notes)
        }
        if(event.remind!=""){
            binding.remindDate.text = event.remind.split("/").get(0)
            remindDatePicked = LocalDate.parse(event.remind.split("/").get(0), DateTimeFormatter.ISO_DATE)
            binding.remindTime.text = event.remind.split("/").get(1)
        }

    }

    private fun validateName(): Boolean{
        var exists = true
        runBlocking {
            if((event!=null && binding.eventName.text.toString()!= event!!.name) || (event==null)){
                if (!eventViewModel.getByName(binding.eventName.text.toString()).isEmpty()) {
                    exists = false
                }
            }
        }
        return exists
    }


    private fun validateDateTime(): Boolean{
        if(binding.submitDate.text.toString()!="submit date"){
            submitDatePicked = LocalDate.parse(binding.submitDate.text, DateTimeFormatter.ISO_DATE)
        }
        if(binding.remindDate.text.toString()!="date"){
            remindDatePicked = LocalDate.parse(binding.remindDate.text, DateTimeFormatter.ISO_DATE)
        }
        if(binding.repeatToDate.text.toString()!="date"){
            repeatDatePicked = LocalDate.parse(binding.repeatToDate.text, DateTimeFormatter.ISO_DATE)
        }
        if(submitDatePicked==null){
            Toast.makeText(this,
                "Date is necessary!", Toast.LENGTH_SHORT).show()
            Vibrations.vibrate(this@NewEventActivity)
            return false
        }
        if(binding.timeFrom.text.equals("Pick time from") || binding.timeTo.text.equals("Pick time to")){
            Toast.makeText(this,
                "Time is necessary!", Toast.LENGTH_SHORT).show()
            Vibrations.vibrate(this@NewEventActivity)
            return false
        }else{
            if(LocalTime.parse(binding.timeFrom.text, DateTimeFormatter.ofPattern("HH:mm")).isAfter(
                    LocalTime.parse(binding.timeTo.text, DateTimeFormatter.ofPattern("HH:mm")
                    ))){
                Toast.makeText(this,
                    "Time To can not be less than time From!", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@NewEventActivity)
                return false
            }
            if(LocalTime.parse(binding.timeFrom.text, DateTimeFormatter.ofPattern("HH:mm")).equals(
                    LocalTime.parse(binding.timeTo.text, DateTimeFormatter.ofPattern("HH:mm")
                    ))){
                Toast.makeText(this,
                    "Time To can not be same as time From!", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@NewEventActivity)
                return false
            }
        }
        if(!repeatE.equals(RepeatEnum.ONCE) ) {
            if(submitDatePicked!=null && repeatDatePicked!=null) {
                if (submitDatePicked!!.isAfter(repeatDatePicked)) {
                    Toast.makeText(
                        this,
                        "Repeat till date can not be less than event date!", Toast.LENGTH_SHORT
                    ).show()
                    Vibrations.vibrate(this@NewEventActivity)
                    return false
                }
            }else{
                Toast.makeText(
                    this,
                    "Dates can not be empty!", Toast.LENGTH_SHORT
                ).show()
                Vibrations.vibrate(this@NewEventActivity)
                return false
            }
        }

        if(!binding.remindTime.text.equals("time") && binding.remindDate.text.toString()=="date"){
            Toast.makeText(this,
                "Remind must have date, not only time!", Toast.LENGTH_SHORT).show()
            Vibrations.vibrate(this@NewEventActivity)
            return false
        }
        return true
    }


    private fun setResultForUpdate(event: Event){
        if(repeatE==RepeatEnum.ONCE && event.repeatEnum==repeatE){
            deleteReminder(event)
            event.name = binding.eventName.text.toString()
            event.location = binding.location.text.toString()
            event.notes = binding.notes.text.toString()
            event.timeFrom = binding.timeFrom.text.toString()
            event.timeTo = binding.timeTo.text.toString()
            event.categoryId = catId
            event.date = submitDatePicked.toString()
            event.repeatTo = submitDatePicked.toString()
            if(remindDatePicked==null){
                event.remind = ""
            }else{
                event.remind = remindDatePicked.toString() + "/" + binding.remindTime.text.toString()
            }
            eventViewModel.updateEvent(event)
            setReminder(event)


        }else if(repeatE!=RepeatEnum.ONCE && repeatE==event.repeatEnum){
            if(submitDatePicked.toString()==event.date && repeatDatePicked.toString() == event.repeatTo){
                if((remindDatePicked!=null && remindDatePicked.toString()==event.remind.split("/").get(0)) ||
                            (remindDatePicked==null && event.remind=="")){
                    runBlocking {
                        for(ev in eventViewModel.getByName(event.name)){
                            deleteReminder(ev)
                            ev.name = binding.eventName.text.toString()
                            ev.location = binding.location.text.toString()
                            ev.notes = binding.notes.text.toString()
                            ev.timeFrom = binding.timeFrom.text.toString()
                            ev.timeTo = binding.timeTo.text.toString()
                            ev.categoryId = catId
                            eventViewModel.updateEvent(ev)
                            setReminder(ev)
                        }
                    }
                }else{
                    runBlocking {
                        var arr = eventViewModel.getByName(event.name).toMutableList()
                        for (ev in arr) {
                            ev.id?.let { eventViewModel.deleteEvent(it) }
                            deleteReminder(ev)
                        }
                        setResult()
                    }
                }
            }else{
                runBlocking {
                    var arr = eventViewModel.getByName(event.name).toMutableList()
                    for (ev in arr) {
                        ev.id?.let { eventViewModel.deleteEvent(it) }
                        deleteReminder(ev)
                    }
                    setResult()
                }
            }
        }else if(repeatE!=event.repeatEnum){
            runBlocking {
                var arr = eventViewModel.getByName(event.name).toMutableList()
                for (ev in arr) {
                    ev.id?.let { eventViewModel.deleteEvent(it) }
                    deleteReminder(ev)
                }
                setResult()
            }
        }
    }

    private fun setResult() {

        if(repeatE==RepeatEnum.EVERY_DAY){
            var i = 0
            val start = ChronoUnit.DAYS.between(submitDatePicked, repeatDatePicked)
            while (i<=start){
                if(i!=0) {
                    submitDatePicked = submitDatePicked?.plusDays(1)
                    if(remindDatePicked!=null) {
                        remindDatePicked = remindDatePicked?.plusDays(1)
                    }
                }
                i++
                insertEvent(submitDatePicked!!, remindDatePicked, repeatDatePicked!!)
            }
        }else if(repeatE==RepeatEnum.ONCE){
            insertEvent(submitDatePicked!!, remindDatePicked, submitDatePicked!!)
        }else if(repeatE==RepeatEnum.EVERY_WEEK){
            var i = 0
            val start = ChronoUnit.DAYS.between(submitDatePicked, repeatDatePicked)
            while (i<=start){
                if(i!=0) {
                    submitDatePicked = submitDatePicked?.plusDays(7)
                    if(remindDatePicked!=null) {
                        remindDatePicked = remindDatePicked?.plusDays(7)
                    }
                }
                i+=7
                insertEvent(submitDatePicked!!, remindDatePicked, repeatDatePicked!!)
            }
        }else if(repeatE==RepeatEnum.EVERY_MONTH){
            var i = 0
            val start = ChronoUnit.MONTHS.between(submitDatePicked, repeatDatePicked)
            while (i<=start){
                if(i!=0) {
                    submitDatePicked = submitDatePicked?.plusMonths(1)
                    if(remindDatePicked!=null) {
                        remindDatePicked = remindDatePicked?.plusMonths(1)
                    }
                }
                i++
                insertEvent(submitDatePicked!!, remindDatePicked, repeatDatePicked!!)
            }
        }
        startActivity(
            Intent(this, MainActivity::class.java)
        )
    }

    private fun repeatSpinner() {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, RepeatEnum.values())

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.repeat.adapter = arrayAdapter

        if(event!=null){
            runBlocking {
                RepeatEnum.values().forEach { catWrap ->
                    if (event!!.repeatEnum == catWrap) {
                        binding.repeat.setSelection(arrayAdapter.getPosition(catWrap))
                        return@forEach
                    }
                }
            }

            if(event!!.repeatEnum!=RepeatEnum.ONCE){
                binding.showRepeat.visibility = View.VISIBLE
                binding.repeatToDate.text = event!!.repeatTo
            }
        }

        binding.repeat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                repeatE = RepeatEnum.ONCE
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                repeatE = RepeatEnum.values().get(position)
                if(position>0){

                    binding.showRepeat.visibility = View.VISIBLE
                    binding.repeatDateButton.setOnClickListener {
                        eventUtil.datePicker(binding.repeatToDate, this@NewEventActivity)
                    }
                }else{
                    binding.showRepeat.visibility = View.INVISIBLE
                }
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

            if(event!=null){
                runBlocking {
                    arr.forEach { catWrap ->
                        if (categoryViewModel.getById(event!!.categoryId)?.name == catWrap) {
                            binding.category.setSelection(arrayAdapter.getPosition(catWrap))
                            catId = event!!.categoryId
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

    private fun insertEvent(date: LocalDate, remindDate: LocalDate?, repeatTillDate: LocalDate){
        var rem = ""
        if(remindDate!=null){
            rem = remindDate.toString() + "/" + binding.remindTime.text.toString()
            alarmReceiver.setReminder(this, LocalDateTime.parse(remindDate.toString()+" "+binding.remindTime.text.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
                ZoneId.systemDefault()).toInstant().toEpochMilli()+6, binding.timeFrom.text.toString()+ " - " + binding.timeTo.text.toString() +" "+binding.eventName.text.toString(), "event")
        }
        eventViewModel.insertEvent(
            cz.cvut.fel.pda.bee_calendar.model.Event(
                name = binding.eventName.text.toString(),
                date = date.toString(),
                remind = rem,
                notes = binding.notes.text.toString(),
                categoryId = catId,
                userId = eventViewModel.loggedUser?.id!!,
                location = binding.location.text.toString(),
                timeFrom = binding.timeFrom.text.toString(),
                timeTo = binding.timeTo.text.toString(),
                repeatEnum = repeatE,
                repeatTo = repeatTillDate.toString()
            )
        )

        alarmReceiver.setReminder(this, LocalDateTime.parse(date.toString()+" "+binding.timeFrom.text.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
            ZoneId.systemDefault()).toInstant().toEpochMilli()+5, binding.timeFrom.text.toString()+ " - " + binding.timeTo.text.toString() +" "+binding.eventName.text.toString(), "event")
    }

    private fun deleteReminder(event: Event){
        alarmReceiver.cancelReminder(this, LocalDateTime.parse(event.date+" "+event.timeFrom, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
            ZoneId.systemDefault()).toInstant().toEpochMilli()+5)

        if(event.remind!=""){
            val remDate = event.remind.split("/").get(0)
            val remTime = event.remind.split("/").get(1)
            alarmReceiver.cancelReminder(this, LocalDateTime.parse(remDate+" "+remTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
                ZoneId.systemDefault()).toInstant().toEpochMilli()+6)

        }

    }

    private fun setReminder(event: Event){
        alarmReceiver.setReminder(this, LocalDateTime.parse(binding.submitDate.text.toString()+" "+binding.timeFrom.text.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
            ZoneId.systemDefault()).toInstant().toEpochMilli()+5, binding.timeFrom.text.toString()+ " - " + binding.timeTo.text.toString() +" "+binding.eventName.text.toString(), "event")

        if(remindDatePicked!=null){
            val remDate = remindDatePicked.toString()
            val remTime = binding.remindTime.text.toString()
            alarmReceiver.setReminder(this, LocalDateTime.parse(remDate+" "+remTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).atZone(
                ZoneId.systemDefault()).toInstant().toEpochMilli()+6, binding.timeFrom.text.toString()+ " - " + binding.timeTo.text.toString() +" "+binding.eventName.text.toString(), "event")
        }

    }
}