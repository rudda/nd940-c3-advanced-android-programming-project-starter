package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
            goBack()
        }

        val bundle = intent.getBundleExtra("download_properties")
        mtitle.text = "Url Downloaded: " + bundle?.getString("TITLE") ?: ""
        status.text = "Status Download:  " + bundle?.getString("STATUS") ?: ""
    }

    private fun goBack() {
        val it = Intent(this, MainActivity::class.java)
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(it)
    }

}
