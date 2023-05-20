package cz.cvut.fel.pda.bee_calendar.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityLoginBinding
import cz.cvut.fel.pda.bee_calendar.utils.Vibrations

class LoginActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var binding: ActivityLoginBinding

    private val userViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = getSharedPreferences("logged-in-user", MODE_PRIVATE)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (sp.contains("user-id")) {

            startActivity(
                Intent(this@LoginActivity, MainActivity::class.java)
            )
        }

        registrationLink()
        handleLogin()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registrationLink(){
        val registrationButton = binding.registrationButton
        registrationButton.setOnClickListener {
            startActivity(
                    Intent(this@LoginActivity, RegistrationActivity::class.java)
                )
        }
    }

    private fun handleLogin() {
        val email = binding.loginEmail
        val password = binding.loginPassword
        val loginButton = binding.loginPasswordSubmit

        loginButton.setOnClickListener {
            if (email.text.toString() != "" && password.text.toString() != "") {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val foundUser = userViewModel.getByEmail(email.text.toString())
                        if (foundUser != null && foundUser.password == password.text.toString()) {
                            setToContext(foundUser.id!!)

                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                        } else {
                            this@LoginActivity.runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Invalid login.", Toast.LENGTH_SHORT).show()
                                Vibrations.vibrate(this@LoginActivity)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Please enter all fields.", Toast.LENGTH_SHORT).show()
                Vibrations.vibrate(this@LoginActivity)
            }
        }
    }

    private fun setToContext(userId: Int) {
        val editor = sp.edit()
        editor.putInt("user-id", userId)
        editor.apply()
    }
}