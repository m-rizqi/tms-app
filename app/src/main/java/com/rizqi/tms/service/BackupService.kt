package com.rizqi.tms.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rizqi.tms.R
import com.rizqi.tms.TMSPreferences.Companion.getBackupSchedule
import com.rizqi.tms.TMSPreferences.Companion.getFirebaseUserId
import com.rizqi.tms.TMSPreferences.Companion.isBackupWithImage
import com.rizqi.tms.TMSPreferences.Companion.setLastBackupDate
import com.rizqi.tms.TMSPreferences.Companion.setNextBackupDate
import com.rizqi.tms.di.NotificationModule
import com.rizqi.tms.model.BackupSchedule.*
import com.rizqi.tms.repository.ItemRepository
import com.rizqi.tms.repository.TransactionRepository
import com.rizqi.tms.repository.UnitRepository
import com.rizqi.tms.utility.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class BackupService : Service() {

    @Inject
    @NotificationModule.BackupNotificationCompatBuilder
    lateinit var backupNotificationBuilder : NotificationCompat.Builder
    @Inject
    lateinit var notificationManager: NotificationManagerCompat
    @Inject
    lateinit var itemRepository: ItemRepository
    @Inject
    lateinit var unitRepository: UnitRepository
    @Inject
    lateinit var transactionRepository: TransactionRepository

    private var isBackupItemSuccess = false
    private var isBackupTransactionSuccess = false
    private var isBackupUnitSuccess = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            BACKUP_NOTIFICATION_ID,
            backupNotificationBuilder.build()
        )
        backupDatabase()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun backupDatabase(){
        val database = Firebase.database
        val backupRef = database.getReference("${BACKUP_REFERENCE}/${getFirebaseUserId()}")
        CoroutineScope(Dispatchers.IO).launch {
            backupItems(backupRef){
                backupTransaction(backupRef){
                    backupUnits(backupRef){
                        var contentText = ""
                        if (isBackupItemSuccess) contentText += getString(R.string.backup_item_success) else getString(R.string.backup_items_failed)
                        if (isBackupTransactionSuccess) contentText += getString(R.string.backup_transactions_success) else getString(R.string.backup_transactions_failed)
                        if (isBackupUnitSuccess) contentText += getString(R.string.backup_units_success) else getString(R.string.backup_units_failed)

                        val contentTitle = if (listOf(isBackupItemSuccess, isBackupTransactionSuccess, isBackupUnitSuccess).all { true }) getString(R.string.backup_success) else getString(R.string.backup_failed)
                        showBackupNotification(contentTitle, contentText)
                        setNextBackupSchedule()
                    }
                }
            }
        }
    }

    private suspend fun backupItems(backupRef : DatabaseReference, nextBackup: suspend () -> Unit) {
        itemRepository.getAllItem().collect{
            showBackupNotification(getString(R.string.uploading_items))
            val itemTask = backupRef.child(ITEMS_REFERENCE).setValue(it.map { it1 -> it1.toNetworkItem() })
            if (isBackupWithImage()){
                var count = 0L
                it.forEachIndexed { index, itemWithPrices ->
                    itemWithPrices.item.imagePath?.let { path ->
                        backupToStorage(ITEMS_REFERENCE, path)
                    }
                    count++
                    val progress = count / it.size * 100
                    showProgressNotification(getString(R.string.uploading_items), count, it.size.toLong(), progress.toInt())
                }
            }
            isBackupItemSuccess = itemTask.isSuccessful
            nextBackup()
        }
    }

    private suspend fun backupTransaction(backupRef : DatabaseReference, nextBackup : suspend () -> Unit) {
        transactionRepository.getAll().collect{
            showBackupNotification(getString(R.string.uploading_transaction))
            val transactionTask = backupRef.child(TRANSACTIONS_REFERENCE).setValue(it.map { it1 -> it1.toNetworkTransaction() })
            if (isBackupWithImage()){
                var count = 0L
                it.forEachIndexed { indexI, transactionWithItemInCashier ->
                    transactionWithItemInCashier.itemInCashiers.forEachIndexed { indexJ, itemInCashier ->
                        itemInCashier.imagePath?.let { path ->
                            backupToStorage(TRANSACTIONS_REFERENCE, path)
                        }
                        count++
                        val progress = count / (if (it.isEmpty()) 1 else it.size * if (transactionWithItemInCashier.itemInCashiers.isEmpty()) 1 else transactionWithItemInCashier.itemInCashiers.size) * 100
                        showProgressNotification(getString(R.string.uploading_transaction), count, (indexI*indexJ).toLong(), progress.toInt())
                    }
                }
            }
            isBackupTransactionSuccess = transactionTask.isSuccessful
            nextBackup()
        }
    }

    private fun backupToStorage(reference : String, path : String){
        val storage = Firebase.storage
        val storageRef = storage.reference.child("${BACKUP_REFERENCE}/${getFirebaseUserId()}/${reference}/${path}")
        getBitmapFromPath(path)?.let {bitmap ->
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data: ByteArray = baos.toByteArray()
            val uploadTask = storageRef.putBytes(data)
        }
    }

    private suspend fun backupUnits(backupRef : DatabaseReference, nextBackup: suspend () -> Unit) {
        unitRepository.getAll().collect{
            showBackupNotification(getString(R.string.uploading_units))
            val unitTask = backupRef.child(UNITS_REFERENCE).setValue(it)
            isBackupUnitSuccess = unitTask.isSuccessful
            nextBackup()
        }
    }

    private fun showBackupNotification(contentTitle : String, contentText : String = ""){
        notificationManager.notify(
            BACKUP_NOTIFICATION_ID,
            backupNotificationBuilder
                .setContentTitle(contentTitle)
                .clearActions()
                .setContentText(contentText)
                .setProgress(0,0, false)
                .build()
        )
    }

    private fun showProgressNotification(contentTitle : String, count : Long, size : Long, progress : Int){
        notificationManager.notify(
            BACKUP_NOTIFICATION_ID,
            backupNotificationBuilder
                .setContentTitle(contentTitle)
                .setContentText("${count}/${size}")
                .setProgress(100, progress, true)
                .build()
        )
    }

    private fun setNextBackupSchedule(){
        val calendar = Calendar.getInstance()
        calendar.add(
            when(getBackupSchedule()){
                EVERY_DAY -> Calendar.DATE
                EVERY_WEEK -> Calendar.DATE
                EVERY_MONTH -> Calendar.MONTH
                NEVER ->  Calendar.YEAR
            },
            if (getBackupSchedule() != EVERY_WEEK) 1 else 7
        )
        setNextBackupDate(calendar.timeInMillis)
        setLastBackupDate()
        stopForeground(false)
        stopSelf()
    }

}