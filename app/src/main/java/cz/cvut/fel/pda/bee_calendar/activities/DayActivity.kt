package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.activities.*
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate

class DayActivity : AppCompatActivity() {

    lateinit var date: TextView
    lateinit var bottomNav : BottomNavigationView
    lateinit var toolbar_value : TextView

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }

    private lateinit var sp: SharedPreferences

    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.left_drawer_day)
        setSupportActionBar(findViewById(R.id.toolbar))
        //home navigation

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        date = findViewById(R.id.date)

        toolbar_value = findViewById(R.id.title1)
        toolbar_value.setText("DAY")

        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)

        user = eventViewModel.loggedUser!!


        var d1 = LocalDate.now()
        date.setText((d1.dayOfMonth).toString() + "." + (d1.monthValue).toString() + "." + d1.year.toString())

        val next = findViewById<AppCompatButton>(R.id.nextButton)
        next.setOnClickListener{
            d1 = d1.plusDays(1)
            date.setText((d1.dayOfMonth).toString() + "." + (d1.monthValue).toString() + "." + d1.year.toString())
        }

        val prev = findViewById<AppCompatButton>(R.id.prevButton)
        prev.setOnClickListener{
            d1 = d1.minusDays(1)
            date.setText((d1.dayOfMonth).toString() + "." + (d1.monthValue).toString() + "." + d1.year.toString())
        }


        var fab = findViewById<FloatingActionButton>(R.id.fab)
//        fab.setOnClickListener{
//            Toast.makeText(this,"Add new action", Toast.LENGTH_LONG).show()
//        }

//        fab = findViewById(R.id.fab)
        registerForContextMenu(fab)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
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
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
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