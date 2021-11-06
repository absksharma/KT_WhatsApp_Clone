package com.example.kt_whatsapp_clone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kt_whatsapp_clone.adapters.ScreenSliderAdapter
import com.example.kt_whatsapp_clone.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        binding.viewPager.adapter = ScreenSliderAdapter(this)

        TabLayoutMediator(
            binding.tabs, binding.viewPager,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, i: Int ->
                when (i) {
                    0 -> tab.text = "CHATAS"
                    1 -> tab.text = "PEOPLE"
                }
            }).attach()


    }
}