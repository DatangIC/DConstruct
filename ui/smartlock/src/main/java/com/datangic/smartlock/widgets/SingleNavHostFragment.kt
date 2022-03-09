package com.datangic.smartlock.widgets

import android.view.View
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.datangic.smartlock.R

class SingleNavHostFragment:NavHostFragment() {
    private fun getContainerId(): Int {
        val id = id
        return if (id != 0 && id != View.NO_ID) {
            id
        } else R.id.nav_host_fragment
    }
    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return SingleFragmentNavigator(requireContext(), childFragmentManager,
                getContainerId())
    }
}