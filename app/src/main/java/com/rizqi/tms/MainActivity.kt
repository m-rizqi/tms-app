package com.rizqi.tms

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.rizqi.tms.TMSPreferences.Companion.isLogin
import com.rizqi.tms.databinding.ActivityMainBinding
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

    private val UPDATE_REQUEST = 134
    private var updateManager : AppUpdateManager? = null
    private lateinit var binding : ActivityMainBinding

    private val installStateUpdateListener = InstallStateUpdatedListener {
        when(it.installStatus()){
            InstallStatus.DOWNLOADED -> {
                Snackbar.make(binding.root, getString(R.string.update_downloaded), Snackbar.LENGTH_INDEFINITE).apply {
                    setAction(getString(R.string.reload)){
                        updateManager?.completeUpdate()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateManager = AppUpdateManagerFactory.create(this)
        updateManager?.appUpdateInfo?.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                    try {
                        updateManager?.startUpdateFlowForResult(it, AppUpdateType.FLEXIBLE, this, UPDATE_REQUEST)
                        updateManager?.registerListener(installStateUpdateListener)
                    }catch (_ : Exception){
                        Toast.makeText(this, getString(R.string.update_available), Toast.LENGTH_SHORT).show()
                    }
                }
                else if (it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        updateManager?.startUpdateFlowForResult(it, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST)
                    }catch (_ : Exception){
                        Toast.makeText(this, getString(R.string.update_available), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Initialize Database
        TMSDatabase.getDatabase(this)
        if (isLogin()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            return
        }
        startActivity(Intent(this, OnBoardingActivity::class.java))

    }

    override fun onStop() {
        try {
            updateManager?.unregisterListener(installStateUpdateListener)
        }catch (_ : Exception){}
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_REQUEST && resultCode == RESULT_OK){

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}