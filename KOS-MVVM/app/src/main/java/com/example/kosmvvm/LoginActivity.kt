package com.example.kosmvvm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class LoginActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        // Password visibility toggle
        btnTogglePassword.setOnClickListener {
            if (etPassword.inputType == android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
            }
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            
            // Validate password length
            if (password.length < 8) {
                tvPasswordError.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                tvPasswordError.visibility = View.GONE
            }
            
            authViewModel.login(username, password)
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        authViewModel.authResult.observe(this, Observer { success ->
            if (success) {
                val username = etUsername.text.toString()
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra(DashboardActivity.EXTRA_ADMIN_ID, username.hashCode().toString())
                intent.putExtra(DashboardActivity.EXTRA_ADMIN_USERNAME, username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login failed. Check your credentials.", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 