package com.example.meeting_app.ui.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private val TAB_TITLE = arrayOf("Event", "Events")
        private val TAB_TITLE_SIZE = TAB_TITLE.size
    }

    override fun getCount(): Int = TAB_TITLE_SIZE

    override fun getItem(position: Int): Fragment =
        when(position) {
            0 -> Fragment()
            1 -> Fragment()
            else -> Fragment()
        }

    override fun getPageTitle(position: Int): CharSequence? =
        TAB_TITLE[position]
}