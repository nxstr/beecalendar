package cz.cvut.fel.pda.bee_calendar.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityNewEventBinding
import cz.cvut.fel.pda.bee_calendar.databinding.EventDetailsBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatEnum
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.*

class EditEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewEventBinding
    private var repeatE: RepeatEnum = RepeatEnum.ONCE
    private lateinit var event : Event

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
        binding.title.text = "EDIT EVENT"

        event = intent.extras?.get("event-detail") as Event
        setData()
        categorySpinner()
        repeatSpinner()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_save) {
//            if(validateName()) {
//                if(validateDateTime()) {
//                    Toast.makeText(
//                        this,
//                        "Saved", Toast.LENGTH_SHORT
//                    ).show()
//                    setResult()
//                    finish()
//                }
//            }else{
//                Toast.makeText(this,
//                    "Event name must be unique!", Toast.LENGTH_SHORT).show()
//            }
            Toast.makeText(this, "Saved edit", Toast.LENGTH_SHORT).show()
        } else if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setData(){
        binding.eventName.setText(event.name)
        binding.submitDate.text = event.date
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
            binding.remindTime.text = event.remind.split("/").get(1)
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

            runBlocking {
                arr.forEach { catWrap ->
                    if (categoryViewModel.getById(event.categoryId)?.name == catWrap) {
                        binding.category.setSelection(arrayAdapter.getPosition(catWrap))
                        return@forEach
                    }
                }
            }


            binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    catId = newName.get(p2).id!!
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    println(" not selected---------------------")
                }
            }
        }
        categoryViewModel.categoriesLiveData.observe(this, nameObserver)
    }

    private fun repeatSpinner() {
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, RepeatEnum.values())

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.repeat.adapter = arrayAdapter

        runBlocking {
            RepeatEnum.values().forEach { catWrap ->
                if (event.repeatEnum == catWrap) {
                    binding.repeat.setSelection(arrayAdapter.getPosition(catWrap))
                    return@forEach
                }
            }
        }

        if(event.repeatEnum!=RepeatEnum.ONCE){
            binding.showRepeat.visibility = View.VISIBLE
            binding.repeatToDate.text = event.repeatTo
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
                        datePicker(binding.repeatToDate)

                    }
                }else{
                    binding.showRepeat.visibility = View.INVISIBLE
                }
            }
        }
    }
    private fun datePicker(sbd: TextView){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
            { view, year, monthOfYear, dayOfMonth ->
                repeatDatePicked = LocalDate.of(year, monthOfYear+1, dayOfMonth)
                sbd.text = (dayOfMonth.toString() + "." + (monthOfYear + 1) + "." + year)

            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}