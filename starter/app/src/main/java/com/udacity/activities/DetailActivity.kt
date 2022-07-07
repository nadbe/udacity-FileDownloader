package com.udacity.activities

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.R
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        textViewToolbarTitle.setText(resources.getString(R.string.title_activity_detail))

        var bundle = intent.extras
        textViewFileNameDescription.text = bundle?.getString("downloadName")
        textViewStatusDescription.text = bundle?.getString("downloadStatus")
        buttonOK.setOnClickListener { onBackPressed() }

        val notificationManager =
            ContextCompat.getSystemService(applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
        notificationManager.cancelAll()
    }

}


