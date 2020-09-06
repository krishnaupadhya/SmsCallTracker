package com.mobile.tracer.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.tracer.utils.AppConstants

/**
 * Created by Krishna Upadhya on 06/09/20.
 */
private const val NUM_PAGES = 2

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        val fragment = ListFragment.newInstance()
        fragment.listType =
            if (position == 0) AppConstants.KEY_CALL_LOG_LIST else AppConstants.KEY_MESSAGE_LIST
        return fragment
    }
}