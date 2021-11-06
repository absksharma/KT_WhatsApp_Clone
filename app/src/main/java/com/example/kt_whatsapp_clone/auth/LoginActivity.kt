package com.example.kt_whatsapp_clone.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.kt_whatsapp_clone.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var phoneNumber: String
    private lateinit var countryCode: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var nextBtn: Button = binding.NextBtn

        binding.etPhoneNumber.addTextChangedListener {
            nextBtn.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }

        nextBtn.setOnClickListener {
            checkNumber()
        }
    }

    private fun checkNumber() {
        countryCode = binding.ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + binding.etPhoneNumber.text.toString()
        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(
                "We will be verifying the Phone number:$phoneNumber\n" +
                        "Is this Ok,or would you like to edit this number?"
            )
            setPositiveButton("OK") { _, _ ->
                showOtpActivity()
            }
            setNegativeButton("EDIT") { dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        startActivity(Intent(this, OtpActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber))
        finish()
    }
}