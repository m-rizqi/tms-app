package com.rizqi.tms.ui.dashboard

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityDashboardBinding
import com.rizqi.tms.ui.dashboard.DashboardActivity.DashboardState.*
import com.rizqi.tms.ui.dashboard.cashier.CashierFragment
import com.rizqi.tms.ui.dashboard.finance.FinanceFragment
import com.rizqi.tms.ui.dashboard.home.HomeFragment
import com.rizqi.tms.ui.dashboard.profile.ProfileFragment
import com.rizqi.tms.utility.DASHBOARD_STATE
import com.rizqi.tms.utility.MENU_INDEX
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar
import kotlin.system.exitProcess

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDashboardBinding
    private val homeFragment = HomeFragment()
    private val cashierFragment = CashierFragment()
    private val financeFragment = FinanceFragment()
    private val profileFragment = ProfileFragment()
    private val dashboardFragmentList = mutableListOf(
        homeFragment, cashierFragment, profileFragment
    )
    private var menuIndex = 0
    private var dashboardState = HOME
    private var isExitApp = false
    private val CAMERA_REQ = 732

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arrayPermission = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            arrayPermission.add(android.Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            arrayPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (arrayPermission.isNotEmpty()){
            ActivityCompat.requestPermissions(
                this,
                arrayPermission.toTypedArray(),
                CAMERA_REQ
            )
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fcv_dashboard, homeFragment)
            .add(R.id.fcv_dashboard, cashierFragment)
//            .add(R.id.fcv_dashboard, financeFragment)
            .add(R.id.fcv_dashboard, profileFragment)
            .commit()
        savedInstanceState?.getInt(MENU_INDEX)?.let { menuIndex = it }
        savedInstanceState?.getSerializable(DASHBOARD_STATE)?.let { dashboardState = it as DashboardState }
        setDashboardStateFragment(dashboardState).commit()
        setupNavigation()

    }

    private fun setDashboardStateFragment(state: DashboardState) : FragmentTransaction {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val transaction = supportFragmentManager.beginTransaction()
        when(state){
            HOME -> {
                dashboardFragmentList.forEachIndexed { index, fragment ->
                    if (index == 0) {
                        transaction.show(fragment)
                        try {
                            fragment.onResume()
                        }catch (e : Exception){}
                    }else{
                        transaction.hide(fragment)
                    }
                }
                menuIndex = 0
                dashboardState = HOME
            }
            CASHIER -> {
                dashboardFragmentList.forEachIndexed { index, fragment ->
                    if (index == 1) {
                        transaction.show(fragment)
                        try {
                            fragment.onResume()
                        }catch (e : Exception){}
                    }else{
                        transaction.hide(fragment)
                    }
                }
                menuIndex = 1
                dashboardState = CASHIER
            }
//            FINANCE -> {
//                dashboardFragmentList.forEachIndexed { index, fragment ->
//                    if (index == 2) {
//                        transaction.show(fragment)
//                        try {
//                            fragment.onResume()
//                        }catch (e : Exception){}
//                    }else{
//                        transaction.hide(fragment)
//                    }
//                }
//                menuIndex = 2
//                dashboardState = FINANCE
//            }
            PROFILE -> {
                dashboardFragmentList.forEachIndexed { index, fragment ->
                    if (index == 2) {
                        transaction.show(fragment)
                        try {
                            fragment.onResume()
                        }catch (e : Exception){}
                    }else{
                        transaction.hide(fragment)
                    }
                }
                menuIndex = 2
                dashboardState = PROFILE
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
                    1 -> setDashboardStateFragment(CASHIER).commit()
//                    2 -> setDashboardStateFragment(FINANCE).commit()
                    2 -> setDashboardStateFragment(PROFILE).commit()
                    else -> setDashboardStateFragment(HOME).commit()
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MENU_INDEX, menuIndex)
        outState.putSerializable(DASHBOARD_STATE, dashboardState)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0 || !homeFragment.isHidden) {
//            if (!isExitApp){
//                Toast.makeText(this, getString(R.string.press_again_to_exit_the_app), Toast.LENGTH_SHORT).show()
//                isExitApp = true
//                return
//            }
            finishAffinity()
        } else {
            setDashboardStateFragment(HOME).commit()
            binding.bnvDashboard.selectTabAt(0)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (dashboardState == HOME){
            homeFragment.onDispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    private enum class DashboardState {
        HOME, CASHIER, PROFILE
    }
}