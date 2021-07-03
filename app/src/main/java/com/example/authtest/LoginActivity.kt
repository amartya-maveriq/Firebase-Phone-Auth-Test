package com.example.authtest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.authtest.OtpActivity.Companion.EXTRA_PHONE
import com.example.authtest.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoginContinue.setOnClickListener {
            val ph = binding.editTextPhone.text.toString().trim()
            validate(ph)
        }
    }

    private fun validate(phone: String) {
        if (phone.isBlank() || phone.length < 10) {
            Toast.makeText(this, "Enter valid phone", Toast.LENGTH_SHORT).show()
            return
        }

        Intent(this, OtpActivity::class.java).also {
            it.putExtra(EXTRA_PHONE, "+91$phone")
            startActivity(it)
        }
    }
}
