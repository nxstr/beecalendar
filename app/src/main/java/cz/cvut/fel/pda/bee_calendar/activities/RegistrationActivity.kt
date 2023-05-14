package cz.cvut.fel.pda.bee_calendar.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityRegistrationBinding
import cz.cvut.fel.pda.bee_calendar.model.User
import cz.cvut.fel.pda.bee_calendar.repository.UserRepository
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RegistrationActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var binding: ActivityRegistrationBinding

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handleLoginContext()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleLoginContext() {
        if (sp.contains("user-id")) {
            startActivity(

                Intent(this@RegistrationActivity, MainActivity::class.java)
            )
        } else {
            handleRegistration()
        }
    }

    private fun handleRegistration() {
        val firstName = binding.registrationFirstName
        val lastName = binding.registrationLastName
        val email = binding.registrationEmail
        val password1 = binding.registrationPassword
        val password2 = binding.registrationPassword2
        val registrationButton = binding.registrationPasswordSubmit


        registrationButton.setOnClickListener {
            if (email.text.toString() != "" && password1.text.toString() != "" && password2.text.toString() != "") {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val foundUser = userViewModel.getByEmail(email.text.toString())
                        if (foundUser == null && password1.text.toString() != password2.text.toString()) {
                            this@RegistrationActivity.runOnUiThread {
                                Toast.makeText(this@RegistrationActivity, "Passwords need to be equal.", Toast.LENGTH_SHORT).show()
                            }
                        } else if (foundUser == null) {

                            val newUser = userViewModel.insert(
                                User(
                                    firstName = firstName.text.toString(),
                                    lastName = lastName.text.toString(),
                                    email = email.text.toString(),
                                    password = password1.text.toString()
                                )
                            )
                            setToContext(newUser.id!!)

                            startActivity(
                                Intent(this@RegistrationActivity, MainActivity::class.java)
                            )

                        } else {
                            this@RegistrationActivity.runOnUiThread {
                                Toast.makeText(this@RegistrationActivity, "Email already exist.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@RegistrationActivity, "Please enter all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setToContext(userId: Int) {
        val editor = sp.edit()
        editor.putInt("user-id", userId)
        editor.apply()
    }
}