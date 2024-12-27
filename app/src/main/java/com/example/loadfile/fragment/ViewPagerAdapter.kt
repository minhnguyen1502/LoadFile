package com.example.loadfile.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2 // Có 2 tab: Audio và Video

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AudioFragment()
            1 -> VideoFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
