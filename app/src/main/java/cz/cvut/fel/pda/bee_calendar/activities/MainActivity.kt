package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.CalendarView.OnDateChangeListener
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.bee_calendar.R
import java.time.LocalDate

class MainActivity : AppCompatActivity(){
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




}