package com.wayj.inputbinder.example2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.button).setOnClickListener { startActivity(Intent(this@MainActivity, InputActivity::class.java)) }
        findViewById<View>(R.id.button2).setOnClickListener {
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra("action", "edit")
            startActivity(intent)
        }
    }
}
