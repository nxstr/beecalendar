package cz.cvut.fel.pda.bee_calendar.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.get
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityNewEventBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatEnum
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class NewEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewEventBinding
    private var repeatE: RepeatEnum = RepeatEnum.ONCE

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
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_new_event)
//        setSupportActionBar(findViewById(R.id.toolbar))
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//
//        val priorities = arrayOf("once", "every day", "every week", "every month")
//        val spinner = findViewById<Spinner>(R.id.repeat)
//
//        if (spinner != null) {
//            val adapter = ArrayAdapter(
//                this,
//                android.R.layout.simple_spinner_item, priorities
//            )
//            spinner.adapter = adapter
//
//            spinner.onItemSelectedListener = object :
//                AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>,
//                                            view: View, position: Int, id: Long) {
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>) {
//                    Toast.makeText(this@NewEventActivity,
//                        "Priority", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        spinner()
        repeatSpinner()
        categorySpinner()
        binding.submitDateButton.setOnClickListener {
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our text view.
                    submitDatePicked = LocalDate.of(year, monthOfYear+1, dayOfMonth)
                    binding.submitDate.text =
                        (dayOfMonth.toString() + "." + (monthOfYear + 1) + "." + year)

                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            datePickerDialog.show()
//            datePicker(binding.submitDate)
//            println("selected Date ======================= " + submitDatePicked)

        }

        binding.remindDateButton.setOnClickListener {
//            remindDatePicked = datePicker(binding.remindDate)
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our text view.
                    remindDatePicked = LocalDate.of(year, monthOfYear+1, dayOfMonth)
                    binding.remindDate.text =
                        (dayOfMonth.toString() + "." + (monthOfYear + 1) + "." + year)

                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        binding.timeFrom.setOnClickListener {
            times(binding.timeFrom)
        }
        binding.timeTo.setOnClickListener {
            times(binding.timeTo)
        }
        binding.remindTime.setOnClickListener {
            times(binding.remindTime)
        }

//        println("----------------" + categoryViewModel.getByName("default")?.id!!)




//        println("----------------" + a.toString())
//        categoryViewModel.let {
//            lifecycleScope.launch(Dispatchers.Main){
//                binding.catText1.setText(categoryViewModel.getByName("default")?.id!!.toString())
////                catId = categoryViewModel.getByName("default")?.id!!
//                println("//////////////////////////" + categoryViewModel.getByName("default")?.id!! + "//////////////////////////" + categoryViewModel.getByName("default") + "////////////" + catId)
//            }
//        }
//
//        val cat = binding.catText1.getText()
//        println("eeeeeeeeeeee" + cat)
//        if(cat.isDigitsOnly()){
//            cat.toList()
//        }
//
//        println("here+++++++++++++++++++++" + cat.get(0))


    }

    private fun times(time: Button) {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                time.setText(LocalTime.of(hourOfDay, minute).format(DateTimeFormatter.ofPattern("HH:mm")))
            }
        }, hour, minute, true)
//            mTimePicker.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
        mTimePicker.show()
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_save) {
            Toast.makeText(this,
                "Saved", Toast.LENGTH_SHORT).show()
            setResult()
            finish()
        } else if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun datePicker(sbd: TextView){
        var selectedDate: LocalDate? = null
        val c = Calendar.getInstance()

        // on below line we are getting
        // our day, month and year.
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            this,
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our text view.
                repeatDatePicked = LocalDate.of(year, monthOfYear+1, dayOfMonth)
                sbd.text =
                    (dayOfMonth.toString() + "." + (monthOfYear + 1) + "." + year)

            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )

        datePickerDialog.show()
        println("date pick ++++++++++++++++++++++= " + submitDatePicked)
//        return LocalDate.of(year, month, day)
    }

    private fun setResult() {
        if(repeatE==RepeatEnum.EVERY_DAY){
            var i = 0
            val start = ChronoUnit.DAYS.between(submitDatePicked, repeatDatePicked)
            while (i<=start){
                if(i!=0) {
                    submitDatePicked = submitDatePicked?.plusDays(1)
                    remindDatePicked = remindDatePicked?.plusDays(1)
                }
                i++
                insertEvent(submitDatePicked!!, remindDatePicked!!, repeatDatePicked!!)
            }
        }else if(repeatE==RepeatEnum.ONCE){
            insertEvent(submitDatePicked!!, remindDatePicked!!, submitDatePicked!!)
        }else if(repeatE==RepeatEnum.EVERY_WEEK){
            var i = 0
            val start = ChronoUnit.DAYS.between(submitDatePicked, repeatDatePicked)
            while (i<=start){
                if(i!=0) {
                    submitDatePicked = submitDatePicked?.plusDays(7)
                    remindDatePicked = remindDatePicked?.plusDays(7)
                }
                i+=7
                insertEvent(submitDatePicked!!, remindDatePicked!!, repeatDatePicked!!)
            }
        }else if(repeatE==RepeatEnum.EVERY_MONTH){
            var i = 0
            val start = ChronoUnit.MONTHS.between(submitDatePicked, repeatDatePicked)
            while (i<=start){
                if(i!=0) {
                    submitDatePicked = submitDatePicked?.plusMonths(1)
                    remindDatePicked = remindDatePicked?.plusMonths(1)
                }
                i++
                insertEvent(submitDatePicked!!, remindDatePicked!!, repeatDatePicked!!)
            }
        }
        startActivity(
            Intent(this, MainActivity::class.java)
        )
    }

    private fun spinner(){
        binding.category?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                catId = 1
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                catId = 1
            }

        }
    }

    private fun repeatSpinner() {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, RepeatEnum.values())

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.repeat.adapter = arrayAdapter
        println("-----------------" + binding.repeat.toString())

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
                println("position===================== " + position)
                if(position>0){

                    binding.showRepeat.visibility = View.VISIBLE
                    binding.repeatDateButton.setOnClickListener {
                        datePicker(binding.repeatToDate)

                    }
                }else{
                    binding.showRepeat.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun categorySpinner(){

        var a = kotlin.collections.ArrayList<Category>()

        val nameObserver = androidx.lifecycle.Observer<List<Category>> { newName ->
            // Update the UI, in this case, a TextView.

            val arr = kotlin.collections.ArrayList<String>()
            for(i in newName){
                arr.add(i.name)
            }
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arr)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.category.adapter = arrayAdapter

            binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    catId = newName.get(p2).id!!

//                    println("selected cat =================== " + catId)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    println(" not selected---------------------")
                }

            }

        }


        categoryViewModel.categoriesLiveData.observe(this, nameObserver)


    }

    private fun insertEvent(date: LocalDate, remindDate: LocalDate, repeatTillDate: LocalDate){

//        catId = categoryViewModel.getByName("default")?.id!!

        println("insert ========================" + catId)

        eventViewModel.insertEvent(
            cz.cvut.fel.pda.bee_calendar.model.Event(
                name = binding.eventName.text.toString(),
                date = date.toString(),
                remind = remindDate.toString() + "/" + binding.remindTime.text.toString(),
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
    }


}