package com.example.authtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.authtest.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also { startActivity(it) }
        }

        binding.btnSignup.setOnClickListener {
            Intent(this, SignupActivity::class.java).also { startActivity(it) }
        }
    }
}
