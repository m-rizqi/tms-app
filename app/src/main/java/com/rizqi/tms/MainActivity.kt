package com.rizqi.tms

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rizqi.tms.TMSPreferences.Companion.isLogin
import com.rizqi.tms.room.TMSDatabase
import com.rizqi.tms.ui.backup.BackupActivity
import com.rizqi.tms.ui.bill.BillSettingActivity
import com.rizqi.tms.ui.cashiersystem.CashierSystemActivity
import com.rizqi.tms.ui.createitem.CreateItemActivity
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.login.LoginActivity
import com.rizqi.tms.ui.onboarding.OnBoardingActivity
import com.rizqi.tms.ui.printer.PrinterActivity
import com.rizqi.tms.ui.printer.PrinterProfileActivity
import com.rizqi.tms.ui.register.RegisterActivity
import com.rizqi.tms.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Database
        TMSDatabase.getDatabase(this)
        if (isLogin()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            return
        }
        startActivity(Intent(this, OnBoardingActivity::class.java))

    }
}