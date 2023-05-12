package cz.cvut.fel.pda.bee_calendar.activities

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cz.cvut.fel.pda.bee_calendar.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityLoginBinding

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

        if (sp.contains("user-id")) {
            println("here------------------------" + sp.all)

            startActivity(
                Intent(this@LoginActivity, MainActivity::class.java)
            )
        }

        registrationLink()
        handleLogin()
    }

    private fun registrationLink() {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(
                    Intent(this@LoginActivity, RegistrationActivity::class.java)
                )
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        val spannableString = SpannableString("Don't have an account? Sing up")
        spannableString.setSpan(clickableSpan, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.loginLinkToSingUp.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
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
                            println("---------------------------user logged in "+ sp.all)

                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                            )
                        } else {
                            this@LoginActivity.runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Invalid login.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@LoginActivity, "Please enter all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setToContext(userId: Int) {
        val editor = sp.edit()
        editor.putInt("user-id", userId)
        editor.apply()
    }
}