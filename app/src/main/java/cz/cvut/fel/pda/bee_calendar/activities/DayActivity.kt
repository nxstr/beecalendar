package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.activities.*
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityDayBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatEnum
import cz.cvut.fel.pda.bee_calendar.utils.EventListAdapter
import cz.cvut.fel.pda.bee_calendar.utils.TaskListAdapter
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DayActivity : AppCompatActivity(), EventListAdapter.Listener, TaskListAdapter.Listener {

    lateinit var date: TextView
    lateinit var bottomNav : BottomNavigationView
    lateinit var toolbar_value : TextView
    private var actualDate: LocalDate = LocalDate.now()
    private lateinit var binding: ActivityDayBinding
    private lateinit var adapter: EventListAdapter
    private lateinit var adapterTask: TaskListAdapter

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModel.TaskViewModelFactory(this)
    }

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private lateinit var sp: SharedPreferences

    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityDayBinding.inflate(layoutInflater)
        setContentView(R.layout.left_drawer_day)
        setSupportActionBar(findViewById(R.id.toolbar))
        //home navigation

        eventsSpinner()
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        date = findViewById(R.id.date)

        toolbar_value = findViewById(R.id.title1)
        toolbar_value.setText("DAY")

        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)

        user = eventViewModel.loggedUser!!


        date.setText(actualDate.toString())

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        adapter = EventListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val recyclerView2 = findViewById<RecyclerView>(R.id.recyclerview2)
        adapterTask = TaskListAdapter(this)
        recyclerView2.adapter = adapterTask
        recyclerView2.layoutManager = LinearLayoutManager(this)

        tasksSpinner()

        val next = findViewById<AppCompatButton>(R.id.nextButton)
        next.setOnClickListener{
            actualDate = actualDate.plusDays(1)
            date.setText(actualDate.toString())
            eventsSpinner()
            tasksSpinner()
        }

        val prev = findViewById<AppCompatButton>(R.id.prevButton)
        prev.setOnClickListener{
            actualDate = actualDate.minusDays(1)
            date.setText(actualDate.toString())
            eventsSpinner()
            tasksSpinner()
        }




        var fab = findViewById<FloatingActionButton>(R.id.fab)
        registerForContextMenu(fab)

    }

    private fun tasksSpinner(){
        val nameObserver = androidx.lifecycle.Observer<List<Category>> { newName ->
            val arr = kotlin.collections.ArrayList<String>()
            arr.add("all")
            arr.add("active")
            for (i in newName) {
                arr.add(i.name)
            }
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arr)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            var bind = findViewById<Spinner>(R.id.tasksSpinner)
            bind.adapter = arrayAdapter

            bind.setSelection(arrayAdapter.getPosition("all"))

            bind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2==0){
                        loadTasks()
                    }else if(p2==1){
                        loadActiveTasks()
                    }else{
                        println("arr get p2>>>>>>>>>>>>>>>>>>> " + arr.get(p2))
                        loadTasksByCategory(arr.get(p2))
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    loadTasks()
                }
            }
        }
        categoryViewModel.categoriesLiveData.observe(this, nameObserver)

    }

    private fun eventsSpinner(){
        val nameObserver = androidx.lifecycle.Observer<List<Category>> { newName ->
            val arr = kotlin.collections.ArrayList<String>()
            arr.add("all")
            arr.add("active")
            for (i in newName) {
                arr.add(i.name)
            }
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arr)

            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            var bind = findViewById<Spinner>(R.id.eventsSpinner)
            bind.adapter = arrayAdapter

            bind.setSelection(arrayAdapter.getPosition("all"))

            bind.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2==0){
                        loadEvents()
                    }else if(p2==1){
                        loadActiveEvents()
                    }else{
                        println("arr get p2>>>>>>>>>>>>>>>>>>> " + arr.get(p2))
                        loadEventsByCategory(arr.get(p2))
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    loadEvents()
                }
            }
        }
        categoryViewModel.categoriesLiveData.observe(this, nameObserver)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
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

    private fun loadActiveEvents(){
        eventViewModel.getEventsByDate(actualDate).observe(this) { words ->
            words.let {
                var arr = ArrayList<Event>()
                for(i in it){
                    if(LocalTime.parse(i.timeFrom, DateTimeFormatter.ofPattern("HH:mm")).isAfter(
                            LocalTime.now())){
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
                            //це список категорій для різних юзерів
                            arr.add(i)
                            println("cat Name????????????????????? " + i.categoryId)
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
                            //це список категорій для різних юзерів
                            arr.add(i)
                            println("cat Name????????????????????? " + i.categoryId)
                        }
                    }
                    adapter.submitList(arr)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadUser(user: User) {
        var ns = findViewById<TextView>(R.id.name_surname)
        var em = findViewById<TextView>(R.id.email)
        ns.setText(user.firstName + " " + user.lastName)
        em.setText(user.email)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.navigation_house -> {
            // User chose the "Print" item
            Toast.makeText(this,"Search action",Toast.LENGTH_LONG).show()
            true
        }
        android.R.id.home ->{
            var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(Gravity.LEFT)
            val nav = findViewById<NavigationView>(R.id.drawer_nav_view_user)


            nav.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_logout -> {
                        sp.apply{
                            val spEditor = edit()
                            spEditor.remove("user-id")
                            spEditor.apply()

                        }
                        val intent = Intent(this, NotLoggedInActivity::class.java)
                        startActivityForResult(intent, 1)
                    }
                }
                true
            }
            loadUser(user)
            true
        }

        R.id.navigation_notifications ->{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Choose an action")
        menu.add(0, v.id, 0, "New Event")
        menu.add(0, v.id, 0, "New Task")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title === "New Event") {
            val intent = Intent(this, NewEventActivity::class.java)
            startActivity(intent)
        } else if (item.title === "New Task") {
            val intent = Intent(this, NewTaskActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    override fun onClickItem(event: Event) {
        val intent = Intent(this, EventDetailsActivity::class.java).apply {
            putExtra("event-detail", event)
        }
        startActivity(intent)
    }

    override fun onClickCheckbox(task: Task) {
        task.isActive = !task.isActive
        taskViewModel.updateTask(task)
    }

    override fun onClickTask(task: Task) {
        val intent = Intent(this, TaskDetailsActivity::class.java).apply {
            putExtra("task-detail", task)
        }
        startActivity(intent)
    }
}