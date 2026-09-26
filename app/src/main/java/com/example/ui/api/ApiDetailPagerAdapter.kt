package com.example.ui.api

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ApiDetailPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ApiDetailRequestFragment.newInstance()
            1 -> ApiDetailResponseFragment.newInstance()
            2 -> ApiDetailOverviewFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
