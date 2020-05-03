package com.example.shadowui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkDebug.setOnCheckedChangeListener { buttonView, isChecked ->
            btnCircle.debug.enabled = isChecked
            btnRect.debug.enabled = isChecked
            btnCircle.invalidate()
            btnRect.invalidate()
        }
    }
}
