package com.example.shadowui

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFormat(PixelFormat.RGBA_8888);

        checkDebug.setOnCheckedChangeListener { buttonView, isChecked ->
            btnCircle.debug.enabled = isChecked
            btnRect.debug.enabled = isChecked
            btnCircle.invalidate()
            btnRect.invalidate()
        }
    }
}
