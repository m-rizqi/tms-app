package com.rizqi.tms.ui.backup

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rizqi.tms.TMSPreferences.Companion.getLastBackupDate
import com.rizqi.tms.databinding.ActivityBackupBinding
import com.rizqi.tms.utility.getDateString

class BackupActivity : AppCompatActivity() {
    private lateinit var binding : ActivityBackupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lastBackupDate = getLastBackupDate()

        binding.apply {
            lastBackup = if (lastBackupDate != null) getDateString(lastBackupDate) else "-"
            btnBackupBack.setOnClickListener { onBackPressed() }
            btnBackup.setOnClickListener {
                if (!foregroundServiceRunning()){
                    val serviceIntent = Intent(this@BackupActivity, BackupService::class.java)
                    startForegroundService(serviceIntent)
                }
            }
        }
    }

    private fun foregroundServiceRunning(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in activityManager.getRunningServices(Int.MAX_VALUE)){
            if (BackupService::class.java.name.equals(service.service.className)){
                return true
            }
        }
        return false
    }

}