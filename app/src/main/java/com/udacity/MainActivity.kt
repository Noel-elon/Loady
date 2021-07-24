package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.widget.Toast
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.app.NotificationChannel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var fileName = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        download_btn.setOnClickListener {
            download_btn.changeButtonState(ButtonState.Clicked)
            when {
                glide_radio_btn.isChecked -> {
                    download(GLIDE_URL)
                    fileName = getString(R.string.glide_txt)
                }

                load_radio_btn.isChecked -> {
                    download(LOAD_URL)
                    fileName = getString(R.string.load_app_txt)
                }

                retrofit_radio_btn.isChecked -> {
                    download(RETROFIT_URL)
                    fileName = getString(R.string.retrofit_txt)
                }
                else -> {
                    Toast.makeText(this, "Choose a file please", Toast.LENGTH_SHORT).show()
                }

            }
        }
     createNotifChannel()

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                val result: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (result) {
                    DownloadManager.STATUS_FAILED -> {
                        Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                        showNotification("Failed")
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                        showNotification("Success")
                    }
                }
                cursor.close()
                download_btn.changeButtonState(ButtonState.Completed)
            }
        }
    }

    private fun download(url : String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }

    fun showNotification(
        status: String
    ) {
        val intentContent = Intent(applicationContext, DetailActivity::class.java).apply {
            putExtra(NOTIFICATION_MESSAGE, fileName)
            putExtra(NOTIFICATION_STATUS, status)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intentContent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext
                .getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setAutoCancel(true)
            .addAction(
                NotificationCompat.Action(
                    null,
                    applicationContext.getString(R.string.notification_button),
                    pendingIntent
                )
            )

       notificationManager.notify(NOTIFICATION_ID, builder.build())
        radio_group.clearCheck()



    }

    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_title),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = getString(R.string.notification_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
