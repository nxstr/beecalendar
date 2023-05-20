package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import cz.cvut.fel.pda.bee_calendar.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate

class NotLoggedInActivity : AppCompatActivity(){

    lateinit var dateTV: TextView
    lateinit var calendarView: CalendarView
    lateinit var cardView2: LinearLayout
    lateinit var bottomNav : BottomNavigationView
    lateinit var fab: FloatingActionButton
    private lateinit var sp: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.left_drawer_unlogged)
        setSupportActionBar(findViewById(R.id.toolbar))

        setTitle("CALENDAR")

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        dateTV = findViewById(R.id.idTVDate)
        dateTV.setText(LocalDate.now().toString())
        calendarView = findViewById(R.id.calendarView)
        cardView2 = findViewById(R.id.cardView2)

        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)

        CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
                    val Date = (dayOfMonth.toString() + "."
                            + (month + 1) + "." + year)

                    dateTV.setText(Date)
                }


        fab = findViewById(R.id.fab)
        fab.setOnClickListener{
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.navigation_house -> {
            Toast.makeText(this,"You are not logged in", Toast.LENGTH_LONG).show()
            true
        }
        android.R.id.home ->{
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
            Toast.makeText(this,"You are not logged in",Toast.LENGTH_LONG).show()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}