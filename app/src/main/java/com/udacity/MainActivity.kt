package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.Helper.NotificationHelper
import com.udacity.Model.FileDownload
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            when (rg_file_selection.checkedRadioButtonId) {
                R.id.rd_glide -> download(GLIDE_FILE)
                R.id.rd_app -> download(LOADAPP_FILE)
                R.id.rd_retrofit -> download(RETROFIT_FILE)
                else ->
                    Toast.makeText(this, "No option has been selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            println("id=="+id)
            custom_button.buttonState = ButtonState.Completed

            val query = DownloadManager.Query()

            if (id != null) {
                query.setFilterById(id)
            }
            val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                var downloadedPackage: FileDownload = FileDownload()
                when (cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))) {
                    GLIDE_FILE.name -> {
                        downloadedPackage = GLIDE_FILE
                        downloadedPackage.fileName = context!!.getString(R.string.glide)
                    }
                    LOADAPP_FILE.name -> {
                        downloadedPackage = LOADAPP_FILE
                        downloadedPackage.fileName = context!!.getString(R.string.load_app)
                    }
                    RETROFIT_FILE.name -> {
                        downloadedPackage = RETROFIT_FILE
                        downloadedPackage.fileName = context!!.getString(R.string.retrofit)
                    }
                }

                // Result of the download status
                if (status == DownloadManager.STATUS_SUCCESSFUL)
                    downloadedPackage.status = "Success"
                else
                    downloadedPackage.status = "Fail"

                println("send notification")
                NotificationHelper(application).sendNotification(downloadedPackage)
            }
        }
    }

    private fun download(download: FileDownload) {
        val request =
            DownloadManager.Request(Uri.parse(download.url))
                .setTitle(download.name)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

       val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        custom_button.buttonState = ButtonState.Loading
    }

    companion object {
        private val GLIDE_FILE =  FileDownload("Glide", "https://github.com/bumptech/glide/archive/refs/heads/master.zip")
        private val LOADAPP_FILE = FileDownload("Load App","https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip")
        private val RETROFIT_FILE = FileDownload("Retrofit", "https://github.com/square/retrofit/archive/refs/heads/master.zip")
    }
}
