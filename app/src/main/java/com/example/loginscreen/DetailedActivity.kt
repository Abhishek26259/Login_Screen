package com.example.loginscreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.loginscreen.databinding.ActivityDetailedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var userDao: UserDao
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        userDao = AppDatabase.getInstance(this).userDao()

        val userId = sharedPref.getLong("userId", -1L)
        if (userId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                currentUser = userDao.getUserById(userId)
                withContext(Dispatchers.Main) {
                    if (currentUser != null) {
                        displayUserDetails(currentUser!!)
                    } else {
                        Toast.makeText(this@DetailedActivity, "User details not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
        }

        binding.editButton.setOnClickListener {
            if (currentUser != null) {
                enableEditMode()
            } else {
                Toast.makeText(this, "User details not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }

        binding.saveButton.setOnClickListener {
            if (currentUser != null) {
                updateProfile()
            } else {
                Toast.makeText(this, "User details not loaded yet", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun displayUserDetails(user: User) {
        binding.nameTextView.text = user.name
        binding.emailTextView.text = user.email
        binding.passwordTextView.text = user.password
    }

    private fun enableEditMode() {
        binding.nameTextView.visibility = View.GONE
        binding.emailTextView.isEnabled = false
        binding.passwordTextView.isEnabled = false

        binding.nameEditText.visibility = View.VISIBLE
        binding.nameEditText.setText(currentUser!!.name)
        binding.passwordEditText.visibility = View.VISIBLE
        binding.passwordEditText.setText(currentUser!!.password)

        binding.editButton.visibility = View.GONE
        binding.saveButton.visibility = View.VISIBLE
    }

    private fun updateProfile() {
        val newName = binding.nameEditText.text.toString().trim()
        val newPassword = binding.passwordEditText.text.toString().trim()

        if (newName.isNotEmpty() && newPassword.isNotEmpty()) {
            currentUser!!.name = newName
            currentUser!!.password = newPassword

            CoroutineScope(Dispatchers.IO).launch {
                userDao.updateUser(currentUser!!)

                withContext(Dispatchers.Main) {
                    binding.nameTextView.text = newName
                    binding.nameTextView.visibility = View.VISIBLE
                    binding.nameEditText.visibility = View.GONE

                    binding.passwordTextView.text = newPassword
                    binding.passwordTextView.visibility = View.VISIBLE
                    binding.passwordEditText.visibility = View.GONE

                    binding.saveButton.visibility = View.GONE
                    binding.editButton.visibility = View.VISIBLE

                    Toast.makeText(this@DetailedActivity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logoutUser() {
        sharedPref.edit().clear().apply()
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

