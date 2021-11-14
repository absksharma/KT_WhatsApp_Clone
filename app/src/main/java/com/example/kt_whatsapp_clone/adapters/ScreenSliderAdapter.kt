package com.example.kt_whatsapp_clone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kt_whatsapp_clone.fragments.inbox.InboxFragment
import com.example.kt_whatsapp_clone.fragments.PeopleFragment

class ScreenSliderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> InboxFragment()
        else -> PeopleFragment()
    }
}
