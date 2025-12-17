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

class RegisterActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnToggleConfirmPassword = findViewById<ImageButton>(R.id.btnToggleConfirmPassword)
        val tvConfirmPasswordError = findViewById<TextView>(R.id.tvConfirmPasswordError)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBackToLogin = findViewById<Button>(R.id.btnBackToLogin)

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

        // Confirm password visibility toggle
        btnToggleConfirmPassword.setOnClickListener {
            if (etConfirmPassword.inputType == android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                etConfirmPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etConfirmPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggleConfirmPassword.setImageResource(android.R.drawable.ic_menu_view)
            }
        }

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            
            // Validate password length
            if (password.length < 8) {
                tvPasswordError.visibility = View.VISIBLE
                tvConfirmPasswordError.visibility = View.GONE
                return@setOnClickListener
            } else {
                tvPasswordError.visibility = View.GONE
            }
            
            // Validate password match
            if (password != confirmPassword) {
                tvConfirmPasswordError.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                tvConfirmPasswordError.visibility = View.GONE
            }
            
            authViewModel.register(username, password)
        }

        btnBackToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        authViewModel.authResult.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 