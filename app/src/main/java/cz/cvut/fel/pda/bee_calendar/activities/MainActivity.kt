package cz.cvut.fel.pda.bee_calendar.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.fragments.CalendarFragment
import cz.cvut.fel.pda.bee_calendar.fragments.DayFragment
import cz.cvut.fel.pda.bee_calendar.fragments.SearchFragment
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.utils.BitmapConverter
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(){
    lateinit var ns: TextView
    lateinit var em: TextView
    lateinit var bottomNav : BottomNavigationView
    lateinit var fab: FloatingActionButton
    lateinit var drawer: DrawerLayout
    private lateinit var sp: SharedPreferences
    private val currentInstance: Fragment? = supportFragmentManager.findFragmentById(R.id.main_fr)

    private lateinit var user: User

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(this)
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

        if(userViewModel.loggedUser==null){
            val intent = Intent(this, NotLoggedInActivity::class.java)
            startActivity(intent)
        }
        user = userViewModel.loggedUser!!

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            showBookingAlertSuccessfully()

        }


    }

    private fun showBookingAlertSuccessfully(){
        val bookingAlert = AlertDialog.Builder(this)
        bookingAlert.setTitle("Choose action")

       bookingAlert.setPositiveButton("NEW EVENT"){dialog, id ->
            val intent = Intent(this,NewEventActivity::class.java)
            startActivity(intent)
        }
        bookingAlert.setNegativeButton("NEW TASK"){dialog, id ->
            val intent = Intent(this,NewTaskActivity::class.java)
            startActivity(intent)
        }
        val alertDialog = bookingAlert.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }


    @SuppressLint("SetTextI18n")
    private fun loadUser(user: User) {
        ns = findViewById(R.id.name_surname)
        em = findViewById(R.id.email)
        ns.setText(user.firstName + " " + user.lastName)
        em.setText(user.email)
        if(user.profileImg!=null){
            user.profileImg.apply {
                findViewById<ImageView>(R.id.imageView1).setImageBitmap(this?.let { BitmapConverter.convert(it) })
            }
        }
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
                runBlocking {
                    user = userViewModel.getById(userViewModel.loggedUser!!.id!!)!!
                }
                drawer.openDrawer(Gravity.LEFT)
                println("user name -------------------- " + user.firstName)
                val nav = findViewById<NavigationView>(R.id.drawer_nav_view_user)

                user.profileImg.apply {
                    findViewById<ImageView>(R.id.imageView1).setImageBitmap(this?.let { BitmapConverter.convert(it) })
                }
                nav.setNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.nav_profile_settings ->{
                            val intent = Intent(this, ProfileActivity::class.java)
                            startActivity(intent)
                            drawer.close()
                        }
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
                        R.id.nav_general_settings ->{
                            val intent = Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", this.packageName, null)
                            intent.setData(uri)
                            startActivity(intent)
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
}