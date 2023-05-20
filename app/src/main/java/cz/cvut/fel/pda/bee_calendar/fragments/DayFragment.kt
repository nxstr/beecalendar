package cz.cvut.fel.pda.bee_calendar.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cz.cvut.fel.pda.bee_calendar.activities.EventDetailsActivity
import cz.cvut.fel.pda.bee_calendar.activities.MainActivity
import cz.cvut.fel.pda.bee_calendar.activities.TaskDetailsActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentDayBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityEnum
import cz.cvut.fel.pda.bee_calendar.utils.EventListAdapter
import cz.cvut.fel.pda.bee_calendar.utils.TaskListAdapter
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DayFragment: Fragment(), EventListAdapter.Listener, TaskListAdapter.Listener {

    private lateinit var binding: FragmentDayBinding
    lateinit var date: TextView
    private var actualDate: LocalDate = LocalDate.now()
    private lateinit var adapter: EventListAdapter
    private lateinit var adapterTask: TaskListAdapter

    private val eventViewModel: EventViewModel by activityViewModels {
        EventViewModel.EventViewModelFactory(requireContext())
    }
    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskViewModel.TaskViewModelFactory(requireContext())
    }

    private val categoryViewModel: CategoryViewModel by activityViewModels {
        CategoryViewModel.CategoryViewModelFactory(requireContext())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = "DAY"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventsSpinner()
        date = binding.date
        date.setText(actualDate.toString())

        val recyclerView = binding.recyclerview
        adapter = EventListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val recyclerView2 = binding.recyclerview2
        adapterTask = TaskListAdapter(this)
        recyclerView2.adapter = adapterTask
        recyclerView2.layoutManager = LinearLayoutManager(activity)

        tasksSpinner()

        val next = binding.nextButton
        next.setOnClickListener{
            actualDate = actualDate.plusDays(1)
            date.setText(actualDate.toString())
            eventsSpinner()
            tasksSpinner()
        }

        val prev = binding.prevButton
        prev.setOnClickListener{
            actualDate = actualDate.minusDays(1)
            date.setText(actualDate.toString())
            eventsSpinner()
            tasksSpinner()
        }
    }

    private fun tasksSpinner(){
        val nameObserver = androidx.lifecycle.Observer<List<Category>> { newName ->
            val arr = kotlin.collections.ArrayList<String>()
            arr.add("all")
            arr.add("active")
            arr.add("priority")
            for (i in newName) {
                arr.add(i.name)
            }
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arr)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            var bind = binding.tasksSpinner
            bind.adapter = arrayAdapter

            bind.setSelection(arrayAdapter.getPosition("all"))

            bind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2==0){
                        loadTasks()
                    }else if(p2==1){
                        loadActiveTasks()
                    }else if(p2==2){
                        loadTasksByPriority()
                    }else{
                        loadTasksByCategory(arr.get(p2))
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    loadTasks()
                }
            }
        }
        categoryViewModel.categoriesLiveData.observe(requireActivity(), nameObserver)

    }

    private fun eventsSpinner(){
        val nameObserver = androidx.lifecycle.Observer<List<Category>> { newName ->
            val arr = kotlin.collections.ArrayList<String>()
            arr.add("all")
            arr.add("active")
            for (i in newName) {
                arr.add(i.name)
            }
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, arr)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            var bind = binding.eventsSpinner
            bind.adapter = arrayAdapter

            bind.setSelection(arrayAdapter.getPosition("all"))

            bind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2==0){
                        loadEvents()
                    }else if(p2==1){
                        loadActiveEvents()
                    }else{
                        loadEventsByCategory(arr.get(p2))
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    loadEvents()
                }
            }
        }
        categoryViewModel.categoriesLiveData.observe(requireActivity(), nameObserver)

    }

    private fun loadEvents(){
        eventViewModel.getEventsByDate(actualDate).observe(this) { words ->
            words.let {
                adapter.submitList(it)
            }
        }
    }

    private fun loadTasks(){
        taskViewModel.getTasksByDate(actualDate).observe(this) { words ->
            words.let {
                adapterTask.submitList(it)
            }
        }
    }

    private fun loadActiveTasks(){
        taskViewModel.getTasksByDate(actualDate).observe(this) { words ->
            words.let {
                var arr = ArrayList<Task>()
                for(i in it){
                    if(i.isActive){
                        arr.add(i)
                    }
                }
                adapterTask.submitList(arr)
            }
        }
    }

    private fun loadTasksByPriority(){
        taskViewModel.getTasksByDate(actualDate).observe(this) { words ->
            words.let {
                var arr = ArrayList<Task>()
                for(i in it){
                        arr.add(i)
                }
                val ans = arr.sortedByDescending { it.priority.value }
                adapterTask.submitList(ans)
            }
        }
    }

    private fun loadActiveEvents(){
        eventViewModel.getEventsByDate(actualDate).observe(this) { words ->
            words.let {
                var arr = ArrayList<Event>()
                for(i in it){
                    if(LocalDate.parse(i.date, DateTimeFormatter.ISO_DATE).equals(LocalDate.now())) {
                        if (LocalTime.parse(i.timeFrom, DateTimeFormatter.ofPattern("HH:mm"))
                                .isAfter(
                                    LocalTime.now()
                                )
                        ) {
                            arr.add(i)
                        }
                    }else if(LocalDate.parse(i.date, DateTimeFormatter.ISO_DATE).isAfter(LocalDate.now())){
                        arr.add(i)
                    }
                }
                adapter.submitList(arr)
            }
        }
    }

    private fun loadTasksByCategory(catName: String){
        taskViewModel.getTasksByDate(actualDate).observe(this) { words ->
            words.let {
                runBlocking {
                    var arr = ArrayList<Task>()
                    for(i in it){
                        if(i.categoryId == categoryViewModel.getByName(catName)?.id){
                            arr.add(i)
                        }
                    }
                    adapterTask.submitList(arr)
                }
            }
        }
    }

    private fun loadEventsByCategory(catName: String){
        eventViewModel.getEventsByDate(actualDate).observe(this) { words ->
            words.let {
                runBlocking {
                    var arr = ArrayList<Event>()
                    for(i in it){
                        if(i.categoryId == categoryViewModel.getByName(catName)?.id){
                            arr.add(i)
                        }
                    }
                    adapter.submitList(arr)
                }
            }
        }
    }

    override fun onClickItem(event: Event) {
        val intent = Intent(activity, EventDetailsActivity::class.java).apply {
            putExtra("event-detail", event)
        }
        startActivity(intent)
    }

    override fun onClickCheckbox(task: Task) {
        task.isActive = !task.isActive
        taskViewModel.updateTask(task)
    }

    override fun onClickTask(task: Task) {
        val intent = Intent(activity, TaskDetailsActivity::class.java).apply {
            putExtra("task-detail", task)
        }
        startActivity(intent)
    }
}