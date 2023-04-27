package com.rizqi.tms.ui.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityDashboardBinding
import com.rizqi.tms.ui.dashboard.home.HomeFragment
import com.rizqi.tms.ui.dashboard.profile.ProfileFragment
import com.rizqi.tms.ui.dashboard.transaction.TransactionFragment
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDashboardBinding
    private val homeFragment = HomeFragment()
    private val transactionFragment = TransactionFragment()
    private val profileFragment = ProfileFragment()
    private val dashboardFragmentList = mutableListOf(
        homeFragment, transactionFragment, profileFragment
    )

    private var menuIndex = 0
    private var dashboardState = DashboardState.HOME
    private var MENU_INDEX = "menu_index"
    private var DASHBOARD_STATE = "dashboard_state"

    private enum class DashboardState {
        HOME, CASHIER, PROFILE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            checkAndRequestPermissions(Manifest.permission.FOREGROUND_SERVICE)
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fcv_dashboard, homeFragment)
            .add(R.id.fcv_dashboard, transactionFragment)
            .add(R.id.fcv_dashboard, profileFragment)
            .commit()
        savedInstanceState?.getInt(MENU_INDEX)?.let { menuIndex = it }
        savedInstanceState?.getSerializable(DASHBOARD_STATE)?.let { dashboardState = it as DashboardState }
        setDashboardStateFragment(dashboardState).commit()
        setupNavigation()
    }

    private fun checkAndRequestPermissions(vararg permissions : String){
        val unGrantedPermissions = mutableListOf<String>()
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) unGrantedPermissions.add(permission)
        }

        if (unGrantedPermissions.isNotEmpty()){
            ActivityCompat.requestPermissions(
                this,
                unGrantedPermissions.toTypedArray(),
                123
            )
        }
    }

    private fun setDashboardStateFragment(state: DashboardState) : FragmentTransaction {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = supportFragmentManager.beginTransaction()
        when(state){
            DashboardState.HOME -> {
                dashboardFragmentList.forEachIndexed { index, fragment ->
                    if (index == 0) {
                        transaction.show(fragment)
                        try {
                            fragment.onResume()
                        }catch (_: Exception){}
                    }else{
                        transaction.hide(fragment)
                    }
                }
                menuIndex = 0
                dashboardState = DashboardState.HOME
            }
            DashboardState.CASHIER -> {
                dashboardFragmentList.forEachIndexed { index, fragment ->
                    if (index == 1) {
                        transaction.show(fragment)
                        try {
                            fragment.onResume()
                        }catch (_: Exception){}
                    }else{
                        transaction.hide(fragment)
                    }
                }
                menuIndex = 1
                dashboardState = DashboardState.CASHIER
            }
            DashboardState.PROFILE -> {
                dashboardFragmentList.forEachIndexed { index, fragment ->
                    if (index == 2) {
                        transaction.show(fragment)
                        try {
                            fragment.onResume()
                        }catch (_: Exception){}
                    }else{
                        transaction.hide(fragment)
                    }
                }
                menuIndex = 2
                dashboardState = DashboardState.PROFILE
            }
        }
        return transaction
    }

    private fun setupNavigation(){
        binding.bnvDashboard.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener{
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when(newIndex){
                    1 -> setDashboardStateFragment(DashboardState.CASHIER).commit()
                    2 -> setDashboardStateFragment(DashboardState.PROFILE).commit()
                    else -> setDashboardStateFragment(DashboardState.HOME).commit()
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MENU_INDEX, menuIndex)
        outState.putSerializable(DASHBOARD_STATE, dashboardState)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (dashboardState == DashboardState.HOME){
//            homeFragment.onDispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

}