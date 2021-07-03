package com.example.authtest

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.authtest.databinding.ActivityOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PHONE = "com.auroriq.authtest.EXTRA_PHONE"
        const val EXTRA_NAME = "com.auroriq.authtest.EXTRA_NAME"
    }

    lateinit var binding: ActivityOtpBinding
    private lateinit var mResendingToken: PhoneAuthProvider.ForceResendingToken
    private var mVerificationId: String? = null

    private var mName: String? = null
    private var mPhone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mName = intent.getStringExtra(EXTRA_NAME)
        mPhone = intent.getStringExtra(EXTRA_PHONE)

        binding.tvOtpResend.setOnClickListener {
            resendOtp()
        }
        binding.btnOtpContinue.setOnClickListener {
            mVerificationId?.let {
                val otp = binding.editTextOtp.text.toString().trim()
                CoroutineScope(IO).launch {
                    phoneSignIn(PhoneAuthProvider.getCredential(it, otp))
                }
            }
        }
        doPhoneSignIn()
    }

    private fun doPhoneSignIn() {
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(mPhone ?: "")
            .setTimeout(30000, TimeUnit.MILLISECONDS)
            .setActivity(this)
            .setCallbacks(phoneVerificationStateChangedCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        otpTimer.start()
    }

    private fun resendOtp() {
        if (this::mResendingToken.isInitialized) {
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(mPhone ?: "")
                .setTimeout(30000, TimeUnit.MILLISECONDS)
                .setActivity(this)
                .setCallbacks(phoneVerificationStateChangedCallbacks)
                .setForceResendingToken(mResendingToken)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        otpTimer.start()
    }

    private val phoneVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                code?.let {
                    binding.editTextOtp.setText(it)
                }
                CoroutineScope(IO).launch {
                    phoneSignIn(phoneAuthCredential)
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendingToken = token
            }

            override fun onVerificationFailed(firebaseException: FirebaseException) {
                otpTimer.cancel()
                Toast.makeText(
                    applicationContext,
                    firebaseException.message ?: "Some error",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(OtpActivity::class.java.simpleName, firebaseException.message ?: "Some error")
                runBlocking {
                    delay(1500)
                    onBackPressed()
                }
            }
        }

    private val otpTimer = object : CountDownTimer(30000, 1000) {
        override fun onTick(remaining: Long) {
            binding.tvOtpResend.apply {
                text = String.format("Resend OTP in %d", (remaining / 1000).toInt())
            }
        }

        override fun onFinish() {
            binding.tvOtpResend.apply {
                text = String.format("Resend OTP")
            }
        }
    }

    override fun onDestroy() {
        otpTimer.cancel()
        super.onDestroy()
    }

    /**
     * Phone Sign in method
     */
    private suspend fun phoneSignIn(credential: PhoneAuthCredential) {
        withContext(IO) {
            Firebase.auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    it.additionalUserInfo?.isNewUser?.let { newUser ->
                        if (newUser && mName.isNullOrBlank()) {
                            // Login attempt
                            Toast.makeText(
                                applicationContext,
                                "new user has to sign up first",
                                Toast.LENGTH_SHORT
                            ).show()
                            Firebase.auth.currentUser?.let { user ->
                                user.delete()
                                    .addOnCompleteListener {
                                        Log.d("Complete", "Delete complete")
                                    }
                                    .addOnFailureListener { ex ->
                                        Log.e("Error", "Error while delete", ex)
                                    }
                            }
                            onBackPressed()
                        } else {
                            signInSuccess(it.user)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.message ?: "Error", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("ERROR", it.message ?: "")
                }
        }
    }

    private fun signInSuccess(user: FirebaseUser?) {
        user?.let {
            Log.d("Success", "Sign in success")
        } ?: run {
            Log.e("Error", "Sign in error")
        }
    }
}
