package cz.cvut.fel.pda.bee_calendar.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityProfileBinding
import cz.cvut.fel.pda.bee_calendar.fragments.PersonalInfoFragment
import cz.cvut.fel.pda.bee_calendar.fragments.PhotoFragment
import cz.cvut.fel.pda.bee_calendar.fragments.ProfileSettingsFragment
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.utils.BitmapConverter
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel

class ProfileActivity: AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var user: User
    private val currentInstance: Fragment? = supportFragmentManager.findFragmentById(R.id.profile_fr)

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        user = userViewModel.loggedUser!!

        val transaction = this.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.profile_fr, ProfileSettingsFragment())
        transaction.commit()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.navigation_save){
            val fragment = supportFragmentManager.findFragmentById(R.id.profile_fr)
            if(fragment is PersonalInfoFragment){
                if(fragment.setResult()){
                    Toast.makeText(
                        this@ProfileActivity,
                        "Saved", Toast.LENGTH_SHORT
                    ).show()
                    supportFragmentManager.popBackStack()
                }
                }
            }

        if (item.itemId == android.R.id.home) {
            val fragment = supportFragmentManager.findFragmentById(R.id.profile_fr)
            if(fragment !is ProfileSettingsFragment) {
                supportFragmentManager.popBackStack()
                supportActionBar?.title = "PROFILE"
            }else if(fragment is ProfileSettingsFragment){
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}