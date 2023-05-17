package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.bee_calendar.fragments.CalendarFragment
import cz.cvut.fel.pda.bee_calendar.fragments.DayFragment
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.fragments.SearchFragment
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import java.time.LocalDate

class MainActivity : AppCompatActivity(){
    lateinit var ns: TextView
    lateinit var em: TextView
    lateinit var bottomNav : BottomNavigationView
    lateinit var fab: FloatingActionButton
    lateinit var drawer: DrawerLayout
    private lateinit var sp: SharedPreferences
    private val currentInstance: Fragment? = supportFragmentManager.findFragmentById(R.id.main_fr)

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

        val transaction = this.supportFragmentManager.beginTransaction()

        transaction.replace(R.id.main_fr, CalendarFragment())
        transaction.commit()

        drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)

        if(eventViewModel.loggedUser==null){
            val intent = Intent(this, NotLoggedInActivity::class.java)
            startActivity(intent)
        }
        user = eventViewModel.loggedUser!!

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.navigation_house -> {
                if(currentInstance !is SearchFragment) {
                    val transaction = this.supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_fr, SearchFragment())
                    transaction.commit()
                }
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
                val fragmentInstance = supportFragmentManager.findFragmentById(R.id.main_fr)

                if(fragmentInstance !is SearchFragment) {
                    val transaction = this.supportFragmentManager.beginTransaction()


                    if (fragmentInstance is DayFragment) {
                        transaction.replace(R.id.main_fr, CalendarFragment())

                    } else if (fragmentInstance is CalendarFragment) {
                        transaction.replace(R.id.main_fr, DayFragment())
                    }
                    transaction.commit()
                }
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
}