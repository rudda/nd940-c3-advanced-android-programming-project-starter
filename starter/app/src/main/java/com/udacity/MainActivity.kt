package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.content.Intent as Intent


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var action: NotificationCompat.Action

    private var TITLE = "TESTE"
    private var STATUS = Status.FAILED

    private enum class Status {
        SUCESS, FAILED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            when(radio_group.checkedRadioButtonId){
                R.id.glide -> { download(URL_GLIDE) }
                R.id.udacity -> download(URL)
                R.id.retrofit -> download(URL_RETROFIT)
                else -> Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
            }
        }

        createChannel(CHANNEL_ID, CHANNEL_NAME)

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)



            if(downloadID == id) {


                val query = DownloadManager.Query().setFilterById(downloadID)
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            STATUS = Status.SUCESS
                            sendNotification()
                            cursor.close()
                        }
                        DownloadManager.STATUS_FAILED -> {
                            STATUS = Status.FAILED
                            sendNotification()
                            cursor.close()
                        }
                    }
                }



            }

            custom_button.buttonState = ButtonState.Completed
        }
    }

    private fun sendNotification() {
        val bundle = Bundle().apply {
            putString("TITLE", TITLE)
            putString("STATUS", STATUS.name.toString())
        }

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(applicationContext, DetailActivity::class.java)
        resultIntent.putExtra("download_properties", bundle)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(applicationContext).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        }

        action = NotificationCompat.Action(R.drawable.ic_assistant_black_24dp, "Show Details", resultPendingIntent)

        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Download")
            .setContentText("Download Success")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(action)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(1010, builder)
        }
    }

    private fun download(url: String) {
        TITLE = url
        custom_button.buttonState = ButtonState.Loading
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

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/refs/tags/2.9.0.zip"
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/refs/tags/v4.12.0.zip"
        private const val CHANNEL_ID = "channel"
        private const val CHANNEL_NAME = "udacity"
    }

    private fun createChannel(id: String, name: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Completed"
            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
