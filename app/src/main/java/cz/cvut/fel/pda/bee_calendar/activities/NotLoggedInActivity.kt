package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NotLoggedInActivity : AppCompatActivity(){
    // on below line we are creating
    // variables for text view and calendar view

    lateinit var dateTV: TextView
    lateinit var calendarView: CalendarView
    lateinit var cardView2: LinearLayout
    lateinit var bottomNav : BottomNavigationView
    lateinit var fab: FloatingActionButton
    private lateinit var sp: SharedPreferences

    private lateinit var user: User

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        setContentView(R.layout.left_drawer_unlogged)
        setSupportActionBar(findViewById(R.id.toolbar))

        //home navigation
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // initializing variables of
        // list view with their ids.
        dateTV = findViewById(R.id.idTVDate)
        calendarView = findViewById(R.id.calendarView)
        cardView2 = findViewById(R.id.cardView2)

        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)

//        loadUser(user)
//        if(eventViewModel.loggedUser?.id !=null){
//            setContentView(R.layout.left_drawer)
//
//        }
//        dateTV.setText((LocalDate.now().dayOfMonth).toString() + "." + (LocalDate.now().monthValue).toString() + "." + LocalDate.now().year);

        // on below line we are adding set on
        // date change listener for calendar view.
        CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
                    // In this Listener we are getting values
                    // such as year, month and day of month
                    // on below line we are creating a variable
                    // in which we are adding all the variables in it.
                    val Date = (dayOfMonth.toString() + "."
                            + (month + 1) + "." + year)

                    // set this date in TextView for Display
                    dateTV.setText(Date)
                }


        fab = findViewById(R.id.fab)
        fab.setOnClickListener{
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_LONG).show()
        }

    }

    private fun loadUser(user: User) {
        dateTV.setText(user.email)

    }

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.navigation_house -> {
            // User chose the "Print" item
            Toast.makeText(this,"Search action", Toast.LENGTH_LONG).show()
            true
        }
        android.R.id.home ->{
//            Toast.makeText(this,"Left side action",Toast.LENGTH_LONG).show()
            var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(Gravity.LEFT)
            var btn = findViewById<Button>(R.id.loginbutton)
            btn.setOnClickListener {
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
            }
            true
        }

        R.id.navigation_notifications ->{
//            val intent = Intent(this, DayActivity::class.java)
//            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

//    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        // you can set menu header with title icon etc
//        menu.setHeaderTitle("Choose an action")
//        // add menu items
//        menu.add(0, v.id, 0, "New Event")
//        menu.add(0, v.id, 0, "New Task")
//    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        if (item.title === "New Event") {
////            val intent = Intent(this, NewEventActivity::class.java)
////            startActivity(intent)
//
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivityForResult(intent, 1)
//        } else if (item.title === "New Task") {
////            val intent = Intent(this, NewTaskActivity::class.java)
////            startActivity(intent)
//        }
//        return true
//    }




}