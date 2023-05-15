package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.bee_calendar.utils.EventListAdapter
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.utils.TaskListAdapter
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import java.time.LocalDate

class MainActivity : AppCompatActivity(), EventListAdapter.Listener, TaskListAdapter.Listener{
    lateinit var dateTV: TextView
    lateinit var ns: TextView
    lateinit var em: TextView
    lateinit var calendarView: CalendarView
    lateinit var cardView2: LinearLayout
    lateinit var bottomNav : BottomNavigationView
    lateinit var fab: FloatingActionButton
    lateinit var drawer: DrawerLayout
    private lateinit var sp: SharedPreferences
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private var actualDate: LocalDate = LocalDate.now()

    private lateinit var user: User

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModel.TaskViewModelFactory(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.left_drawer)
        setSupportActionBar(findViewById(R.id.toolbar))

        drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        dateTV = findViewById(R.id.idTVDate)
        calendarView = findViewById(R.id.calendarView)
        cardView2 = findViewById(R.id.cardView2)

        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)

        if(eventViewModel.loggedUser==null){
            val intent = Intent(this, NotLoggedInActivity::class.java)
            startActivity(intent)
        }
        user = eventViewModel.loggedUser!!
        dateTV.setText(LocalDate.now().toString());
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = EventListAdapter(this)
        val adapter1 = TaskListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadEvents(adapter)

        val schedule = findViewById<Button>(R.id.schedule)
        schedule.setOnClickListener {
            recyclerView.adapter = adapter
            loadEvents(adapter)
        }


        val todo_list = findViewById<Button>(R.id.todo_list)
        todo_list.setOnClickListener {
            recyclerView.adapter = adapter1
            loadTasks(adapter1)
        }


        calendarView
            .setOnDateChangeListener(
                OnDateChangeListener { view, year, month, dayOfMonth ->

                    actualDate = LocalDate.of(year, month+1, dayOfMonth)

                    val Date = (dayOfMonth.toString() + "."
                            + (month + 1) + "." + year)
                    dateTV.setText(Date)
                    if(recyclerView.adapter==adapter) {
                        loadEvents(adapter)
                    }else if(recyclerView.adapter==adapter1){
                        loadTasks(adapter1)
                    }
//                    eventViewModel.getEventsByDate(d)
                })


        fab = findViewById(R.id.fab)
        registerForContextMenu(fab)





    }


    private fun loadEvents(adapter: EventListAdapter){
        eventViewModel.getEventsByDate(actualDate).observe(this) { words ->
            words.let {
                adapter.submitList(it)
            }
        }
    }

    private fun loadTasks(adapter: TaskListAdapter){
        taskViewModel.getTasksByDate(actualDate).observe(this) { words ->
            words.let {
                adapter.submitList(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadUser(user: User) {
        ns = findViewById(R.id.name_surname)
        em = findViewById(R.id.email)
        ns.setText(user.firstName + " " + user.lastName)
        em.setText(user.email)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.navigation_house -> {
                Toast.makeText(this, "Search action", Toast.LENGTH_LONG).show()
                true
            }
            android.R.id.home -> {
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

                val navGeneral = findViewById<NavigationView>(R.id.drawer_nav_view)
                navGeneral.setNavigationItemSelectedListener{
                    when(it.itemId){
                        R.id.nav_categories->{
                            startActivity(Intent(this, CategoryActivity::class.java))
                        }
                    }
                    true
                }
                loadUser(user)
                true
            }

            R.id.navigation_notifications -> {
                val intent = Intent(this, DayActivity::class.java)
                startActivity(intent)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }






    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
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
        println("clicked =================== " + event.name)
        val intent = Intent(this, EventDetailsActivity::class.java).apply {
            putExtra("event-detail", event)
        }
        startActivity(intent)
    }

    override fun onClickTask(task: Task) {
        val intent = Intent(this, TaskDetailsActivity::class.java).apply {
            putExtra("task-detail", task)
        }
        startActivity(intent)
    }

    override fun onClickCheckbox(task: Task) {
        task.isActive = !task.isActive
        taskViewModel.updateTask(task)
    }


}