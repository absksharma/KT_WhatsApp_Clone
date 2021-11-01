package com.example.kt_whatsapp_clone

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.kt_whatsapp_clone.databinding.ActivityOtpBinding

const val PHONE_NUMBER = "phoneNumber"

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    var phoneNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityOtpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        showTimer(60000)
    }

    private fun showTimer(milliInFuture: Long) {

        binding.resendBtn.isEnabled = false
        object : CountDownTimer(milliInFuture, 1000) {
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

    private fun initView() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        binding.verifyTv.text = getString(R.string.verify_no, phoneNumber)
        setSpanAbleStrings()
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
}