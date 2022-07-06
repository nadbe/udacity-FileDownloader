package com.udacity.activities

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private val NOTIFICATION_ID = 0
    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var selectedUrl:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroupUrls.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButtonGlide -> selectedUrl = GLIDE_URL
                R.id.radioButtonUdacity -> selectedUrl = UDACITY_URL
                R.id.radioButtonRetrofit -> selectedUrl = RETROFIT_URL
            }
        }

        custom_button.setOnClickListener {
            if (selectedUrl.isNotEmpty()) {
                download(selectedUrl)
            } else {
                Toast.makeText(this,getString(R.string.select_file), Toast.LENGTH_SHORT).show()
            }
        }

        notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createChannel(
                CHANNEL_ID, getString(
                            R.string.download_text), getString(R.string.download_channel)))
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            var downloadName = ""
            when(radioGroupUrls.checkedRadioButtonId){
                R.id.radioButtonGlide -> downloadName = getString(R.string.glide)
                R.id.radioButtonUdacity -> downloadName = getString(R.string.udacity)
                R.id.radioButtonRetrofit -> downloadName = getString(R.string.retrofit)
            }
            context?.let { notificationManager.sendNotification(it, CHANNEL_ID, downloadName, id.toString()) }
        }
    }

    private fun download(url:String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    fun NotificationManager.sendNotification(applicationContext: Context, channelName: String, downloadName:String, downloadStatus:String) {

        val checkStatusIntent = Intent(applicationContext, DetailActivity::class.java)
        val checkStatusPendingIntent = PendingIntent.getActivity(applicationContext, NOTIFICATION_ID, checkStatusIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val bundle = Bundle()
        bundle.putString("downloadName", downloadName)
        bundle.putString("downloadStatus", downloadStatus)
        checkStatusIntent.putExtras(bundle)

        val builder = NotificationCompat.Builder(applicationContext, channelName)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(applicationContext.resources.getText(R.string.notification_description))
            .addAction(R.drawable.ic_assistant_black_24dp,"Check the status",checkStatusPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        notify(NOTIFICATION_ID, builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, channelName: String, description: String): NotificationChannel {
        var notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_LOW)
        notificationChannel.description = description
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        return notificationChannel
    }




    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private const val CHANNEL_ID = "download_notification_channel"
    }

}
