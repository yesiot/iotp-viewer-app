package com.example.android.mqttviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_device_view.*
import org.eclipse.paho.client.mqttv3.MqttMessage
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.ColorSpace
import android.widget.ImageView
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.IntBuffer


class DeviceViewActivity : AppCompatActivity() {

    lateinit var devName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_view)

        val deviceName = getIntent().getExtras().getString("DEVICE_NAME")

        devName = deviceName
        text_deviceName.text = deviceName

        val globalApp = applicationContext as App
        globalApp.getMqttEngine().setMessageHandler(::onNewMessage)
        globalApp.getMqttEngine().subscribeToDevice(deviceName)
    }

    fun onNewMessage(topic : String, value : MqttMessage) {

        if(topic.contains(devName)) {

            if (topic.contains("counter"))
                text_counter.text = value.toString()
            if (topic.contains("picture")) {

                val pictureSize = Pair(320, 240)

                val image = Bitmap.createBitmap(pictureSize.first, pictureSize.second, Bitmap.Config.ARGB_8888)

                var j = 0
                val buf = IntArray(pictureSize.first * pictureSize.second)
                for (y in 0 until pictureSize.second) {
                    for (x in 0 until pictureSize.first) {

                        val r = value.payload[j++].toInt() and 0xFF
                        val g = value.payload[j++].toInt() and 0xFF
                        val b = value.payload[j++].toInt() and 0xFF

                        buf[y * pictureSize.first + x] = (255 shl 24) or (r shl 16) or (g shl 8) or b
                    }
                }

                image.copyPixelsFromBuffer(IntBuffer.wrap(buf))

                imageView_picture.setImageBitmap(image)
                imageView_picture.scaleType = ImageView.ScaleType.FIT_XY

                text_topic.text = topic
            }
        }
    }
}
