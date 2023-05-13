package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import cz.cvut.fel.pda.bee_calendar.CategoryListAdapter
import cz.cvut.fel.pda.bee_calendar.EventListAdapter
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.model.Event
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), EventListAdapter.Listener{
    // on below line we are creating
    // variables for text view and calendar view

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

    private lateinit var user: User

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setContentView(R.layout.left_drawer)
        setSupportActionBar(findViewById(R.id.toolbar))




        drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        //home navigation
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // initializing variables of
        // list view with their ids.
        dateTV = findViewById(R.id.idTVDate)
        calendarView = findViewById(R.id.calendarView)
        cardView2 = findViewById(R.id.cardView2)

        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)

        if(eventViewModel.loggedUser==null){
            println("why is it null??????????????????????????")
            val intent = Intent(this, NotLoggedInActivity::class.java)
            startActivity(intent)
        }
            user = eventViewModel.loggedUser!!
        println("__________________________ " + user.id)
//            loadUser(user)
        dateTV.setText((LocalDate.now().dayOfMonth).toString() + "." + (LocalDate.now().monthValue).toString() + "." + LocalDate.now().year);

        // on below line we are adding set on
        // date change listener for calendar view.
        calendarView
            .setOnDateChangeListener(
                OnDateChangeListener { view, year, month, dayOfMonth ->
                    // In this Listener we are getting values
                    // such as year, month and day of month
                    // on below line we are creating a variable
                    // in which we are adding all the variables in it.
                    val d = LocalDate.of(dayOfMonth, month+1, year)
                    val Date = (dayOfMonth.toString() + "."
                            + (month + 1) + "." + year)

                    // set this date in TextView for Display
                    dateTV.setText(Date)
                    eventViewModel.getEventsByDate(d)
                })


        fab = findViewById(R.id.fab)
        registerForContextMenu(fab)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = EventListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        eventViewModel.getEventsByDate(LocalDate.now()).observe(this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let {

//                val arr = ArrayList<Event>()
//                for(i in it){
//                    if(!i.name.equals("default")) {
//                        arr.add(i)
//                    }
//                }
//                val arr = eventViewModel.getEventsByDate(LocalDate.now())
//                println("events here********************")
//                for(i in it){
////                    println("arr item ================" + i.name + " " + LocalTime.parse(i.timeFrom, DateTimeFormatter.ofPattern("hh:mm")))
//
//                }
                adapter.submitList(it)

//                for(i in arr){
//                    println("arr item ================" + i.name)
//                }
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

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.navigation_house -> {
                // User chose the "Print" item
                Toast.makeText(this, "Search action", Toast.LENGTH_LONG).show()
                true
            }
            android.R.id.home -> {
//            Toast.makeText(this,"Left side action",Toast.LENGTH_LONG).show()

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
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
            }
        }






    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        // you can set menu header with title icon etc
        menu.setHeaderTitle("Choose an action")
        // add menu items
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
//        editLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()) {
////            if (it.resultCode == Activity.RESULT_OK) {
////                eventViewModel.insertEvent(it.data?.getSerializableExtra("new-event") as Event)
//////                println("44444444444444444444444" + it.data + " " + it.resultCode)
////            }
//        }
//        startActivity(Intent(this, NewEventActivity::class.java))
        println("clicked =================== " + event.name)
        val intent = Intent(this, EventDetailsActivity::class.java).apply {
            putExtra("event-detail", event)
        }
//        editLauncher.launch(intent)
        startActivity(intent)
        //start event details activity!!!
    }


}