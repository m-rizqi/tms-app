package com.rizqi.tms.model

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import com.rizqi.tms.R
import com.rizqi.tms.ui.backup.BackupActivity
import com.rizqi.tms.ui.unit.UnitListActivity

data class Setting(
    val name: String,
    val description : String,
    val icon : Drawable,
    val activityAction: Class<out AppCompatActivity>? = null
){
    companion object {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun getSettings(resources: Resources) = listOf<Setting>(
            Setting(
                resources.getString(R.string.unit),
                resources.getString(R.string.unit_setting_description),
                resources.getDrawable(R.drawable.ic_unit, null),
                UnitListActivity::class.java
            ),
            Setting(
                resources.getString(R.string.backup),
                resources.getString(R.string.backup_setting_description),
                resources.getDrawable(R.drawable.ic_backup, null),
                BackupActivity::class.java
            ),
//            Setting(
//                resources.getString(R.string.printer),
//                resources.getString(R.string.printer_setting_description),
//                resources.getDrawable(R.drawable.ic_printer, null)
//            ),
//            Setting(
//                resources.getString(R.string.bill),
//                resources.getString(R.string.bill_setting_description),
//                resources.getDrawable(R.drawable.ic_bill, null)
//            ),
//            Setting(
//                resources.getString(R.string.privacy_policy),
//                resources.getString(R.string.privacy_policy_setting_description),
//                resources.getDrawable(R.drawable.ic_privacy_policy, null)
//            ),
//            Setting(
//                resources.getString(R.string.help),
//                resources.getString(R.string.help_setting_description),
//                resources.getDrawable(R.drawable.ic_help, null)
//            ),
            Setting(
                resources.getString(R.string.logout),
                "",
                resources.getDrawable(R.drawable.ic_logout, null)
            ),
        )
    }
}