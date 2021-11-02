package com.example.kt_whatsapp_clone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.kt_whatsapp_clone.databinding.ActivityOtpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phoneNumber"

class OtpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var progressDialog: ProgressDialog
    private var phoneNumber: String? = null
    private var mCounterTimer: CountDownTimer? = null

    var mVerificationId: String? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOtpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()

        startVerify()
    }

    private fun startVerify() {
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        showTimer(60000)
        progressDialog = createProgressDialog("Sending a verification code", false)
        progressDialog.show()
    }

    private fun showTimer(milliInFuture: Long) {

        binding.resendBtn.isEnabled = false
        mCounterTimer = object : CountDownTimer(milliInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.counterTv.isVisible = true
                binding.counterTv.text =
                    getString(R.string.remaining_time, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                binding.resendBtn.isEnabled = true
                binding.counterTv.isVisible = false
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCounterTimer != null) {
            mCounterTimer!!.cancel()
        }
    }

    private fun initView() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        binding.verifyTv.text = getString(R.string.verify_no, phoneNumber)
        setSpanAbleStrings()

        binding.resendBtn.setOnClickListener(this)
        binding.verificationBtn.setOnClickListener(this)

        /*** Firebase call for authentication ***/

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                val smsCode = credential.smsCode
                if (!smsCode.isNullOrBlank())
                    binding.sentcodeEt.setText(smsCode)

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {


                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                progressDialog.dismiss()
                binding.counterTv.isVisible = false
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
            }
        }
    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                showLoginActivity()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, SignUpActivity::class.java)
                    )
                    finish()
                } else {
                    notifyUserAndRetry("Phone Number verification failed, Retry Again!!")
                }
            }
    }

    private fun setSpanAbleStrings() {
        val span = SpannableString(getString(R.string.waitng, phoneNumber))
        val clickableSpan = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }

            override fun onClick(widget: View) {
                showLoginActivity()
            }
        }
        span.setSpan(
            clickableSpan,
            span.length - 13,
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.waitingTv.movementMethod = LinkMovementMethod.getInstance()
        binding.waitingTv.text = span

    }

    private fun showLoginActivity() {
        startActivity(
            Intent(
                this@OtpActivity,
                LoginActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    override fun onBackPressed() {
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.verificationBtn -> {

                var code = binding.sentcodeEt.text.toString()
                if (code.isNotEmpty() && !mVerificationId.isNullOrEmpty()) {

                    progressDialog = createProgressDialog("Please wait...", false)
                    progressDialog.show()
                    val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
                    signInWithPhoneAuthCredential(credential)
                    Toast.makeText(this, "User Added Sucessfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "verification failed at Button", Toast.LENGTH_SHORT).show()
                }
            }
            binding.resendBtn -> {

                val code = binding.sentcodeEt.text.toString()
                if (mResendToken != null) {
                    resendVerification(phoneNumber.toString(), mResendToken)
                    showTimer(60000)
                }
            }
        }
    }

    private fun resendVerification(
        phoneNumber: String,
        mResendToken: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
    }
}

fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
    return ProgressDialog(this).apply {
        setCancelable(false)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }

}