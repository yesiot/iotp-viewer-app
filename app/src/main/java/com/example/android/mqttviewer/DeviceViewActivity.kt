package com.example.android.mqttviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_device_view.*

class DeviceViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_view)

        val deviceName = getIntent().getExtras().getString("DEVICE_NAME")

        text_deviceName.text = deviceName
    }
}
