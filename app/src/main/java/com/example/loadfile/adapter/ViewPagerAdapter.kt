package com.example.loadfile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.loadfile.fragment.AudioFragment
import com.example.loadfile.fragment.VideoFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragments = listOf(
        AudioFragment(),
        VideoFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getFragment(position: Int): Fragment? {
        return fragments.getOrNull(position)
    }
}

