package com.nurbk.ps.v1.image.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter

class DemoCollectionPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var lf = mutableListOf<Fragment>()
     var lt = mutableListOf<String>()
    override fun getItemCount(): Int {
        return lf.size
    }

    override fun createFragment(position: Int): Fragment {
        return lf[position]
    }

        fun addFragment(f: Fragment, t: String) {
        lf.add(f)
        lt.add(t)
    }



}