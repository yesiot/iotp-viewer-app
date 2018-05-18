package com.example.android.mqttviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_device_view.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.ColorSpace
import android.widget.ImageView
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer


class DeviceViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_view)

        val deviceName = getIntent().getExtras().getString("DEVICE_NAME")

        text_deviceName.text = deviceName

        val globalApp = applicationContext as App
        globalApp.getMqttEngine().setMessageHandler(::onNewMessage)
        globalApp.getMqttEngine().subscribeToDevice(deviceName)
    }

    fun onNewMessage(topic : String, value : MqttMessage) {
        if(topic.contains("counter"))
            text_counter.text = value.toString()
        if(topic.contains("picture")) {
            val image = Bitmap.createBitmap(1280 / 10, 960 / 10, Bitmap.Config.ARGB_8888)
            val buf = ByteBuffer.wrap(value.payload)
            image.copyPixelsFromBuffer(buf)

            imageView_picture.setImageBitmap(image)
            imageView_picture.setScaleType(ImageView.ScaleType.FIT_XY);

            text_topic.text = topic
        }
    }
}
