package com.example.loginscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.loginscreen.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getInstance(this)
        userDao = database.userDao()

        val registerButton = binding.registerButton
        registerButton.setOnClickListener {
            val email = getEmailFromInput()
            val password = getPasswordFromInput()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val user = User(email = email, password = password)
                registerUser(user)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(user: User) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            userDao.insertUser(user)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun getEmailFromInput(): String {
        val emailEditText = binding.emailEditText
        return emailEditText.text.toString().trim()
    }

    private fun getPasswordFromInput(): String {
        val passwordEditText = binding.passwordEditText
        return passwordEditText.text.toString().trim()
    }

}