package com.rizqi.tms.ui.backup

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rizqi.tms.TMSPreferences.Companion.getBackupSchedule
import com.rizqi.tms.TMSPreferences.Companion.getLastBackupDate
import com.rizqi.tms.TMSPreferences.Companion.isBackupWithImage
import com.rizqi.tms.TMSPreferences.Companion.setBackupSchedule
import com.rizqi.tms.TMSPreferences.Companion.setBackupWithImage
import com.rizqi.tms.databinding.ActivityBackupBinding
import com.rizqi.tms.model.BackupSchedule
import com.rizqi.tms.model.BackupSchedule.*
import com.rizqi.tms.utility.checkServiceRunning
import com.rizqi.tms.utility.getDateString
import com.rizqi.tms.utility.setSwitchTheme

class BackupActivity : AppCompatActivity() {
    private lateinit var binding : ActivityBackupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lastBackupDate = getLastBackupDate()
        when(getBackupSchedule()){
            EVERY_DAY -> binding.radioBackupEveryDay.isChecked = true
            EVERY_WEEK -> binding.radioBackupEveryWeek.isChecked = true
            EVERY_MONTH -> binding.radioBackupEveryMonth.isChecked = true
            NEVER -> binding.radioBackupNever.isChecked = true
        }
        binding.apply {
            lastBackup = if (lastBackupDate != null) getDateString(lastBackupDate) else "-"
            switchBackupIncludeImage.setOnCheckedChangeListener { _, b ->
                setSwitchTheme(binding.switchBackupIncludeImage, b)
                setBackupWithImage(b)
            }
            radioGrupBackup.setOnCheckedChangeListener { radioGroup, _ ->
                when(radioGroup.checkedRadioButtonId){
                    binding.radioBackupEveryDay.id -> setBackupSchedule(EVERY_DAY)
                    binding.radioBackupEveryWeek.id -> setBackupSchedule(EVERY_WEEK)
                    binding.radioBackupEveryMonth.id -> setBackupSchedule(EVERY_MONTH)
                    binding.radioBackupNever.id -> setBackupSchedule(NEVER)
                }
            }
            btnBackupBack.setOnClickListener { onBackPressed() }
            btnBackup.setOnClickListener {
                if (!checkServiceRunning(BackupService::class.java.name)){
                    val serviceIntent = Intent(this@BackupActivity, BackupService::class.java)
                    startForegroundService(serviceIntent)
                }
            }
            switchBackupIncludeImage.isChecked = isBackupWithImage()
        }
    }

}