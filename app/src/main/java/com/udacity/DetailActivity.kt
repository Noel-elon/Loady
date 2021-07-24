package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.content.Intent
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        file.text = intent.getStringExtra(NOTIFICATION_MESSAGE)
        val downloadStatus = intent.getStringExtra(NOTIFICATION_STATUS)

        if (downloadStatus == "Failed") {
            download_status.setTextColor(Color.RED)
        } else {
            download_status.setTextColor(Color.GREEN)
        }
        download_status.text = downloadStatus

        okay_btn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

}
