package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras

        val status: String? = extras?.getString("status")
        val fileName: String? = extras?.getString("file_name")

        txt_file_name.text = fileName
        txt_file_status.text = status

        //Change the color of text if status is Fail
        if (status == "Fail")
            txt_file_status.setTextColor(Color.RED)

        //Going back to the main activity
        fab.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}
