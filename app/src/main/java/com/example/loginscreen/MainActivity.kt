package com.example.loginscreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.loginscreen.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var sharedPrefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        database = AppDatabase.getInstance(this)
        userDao = database.userDao()
        sharedPrefs = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        if (isLoggedIn()){
            navigateToNextScreen()
            return
        }

        val loginButton = binding.loginBtn
        loginButton.setOnClickListener {
            val email = getEmailFromInput()
            val password = getPasswordFromInput()

            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val user = userDao.getUserByEmail(email)
                withContext(Dispatchers.Main) {
                    if (user != null && user.password == password) {
                        saveLoginStatus(true)
                        Toast.makeText(this@MainActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        navigateToNextScreen()
                    } else {
                        Toast.makeText(this@MainActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, DetailedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getEmailFromInput(): String {
        val emailEditText = binding.tilEditText
        return emailEditText.text.toString().trim()
    }

    private fun getPasswordFromInput(): String {
        val passwordEditText = binding.tilPassword
        return passwordEditText.text.toString().trim()
    }

    private fun isLoggedIn(): Boolean {
        return sharedPrefs.getBoolean("isLoggedIn", false)
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

}

