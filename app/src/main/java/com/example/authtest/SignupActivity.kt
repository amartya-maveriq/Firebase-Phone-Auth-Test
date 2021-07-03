package com.example.authtest

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.authtest.OtpActivity.Companion.EXTRA_NAME
import com.example.authtest.OtpActivity.Companion.EXTRA_PHONE
import com.example.authtest.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignupContinue.setOnClickListener {
            val name = binding.editTextPersonName.text.toString().trim()
            val ph = binding.editTextPhoneSignup.text.toString().trim()
            validate(name, ph)
        }
    }

    private fun validate(name: String, phone: String) {
        if (name.isBlank() || phone.isBlank() || phone.length < 10) {
            Toast.makeText(this, "Enter valid field values", Toast.LENGTH_SHORT).show()
            return
        }
        Intent(this, OtpActivity::class.java).also {
            it.putExtra(EXTRA_NAME, name)
            it.putExtra(EXTRA_PHONE, "+91$phone")
            startActivity(it)
        }
    }
}
