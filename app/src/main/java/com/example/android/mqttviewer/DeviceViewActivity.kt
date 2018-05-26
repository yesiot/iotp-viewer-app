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
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.R.attr.category
import android.R.attr.visible
import android.view.View
import android.view.ViewGroup
import android.widget.TextView






class DeviceViewActivity : AppCompatActivity() {

    lateinit var devName : String
    lateinit var mqttEngine: MqttEngineInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_view)

        val deviceName = getIntent().getExtras().getString("DEVICE_NAME")

        devName = deviceName
        text_deviceName.text = deviceName

        val globalApp = applicationContext as App
        mqttEngine = globalApp.getMqttEngine()
        mqttEngine.setMessageHandler(::onNewMessage)
        mqttEngine.subscribeToDevice(deviceName)

        switchSendPicture.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked) {
                mqttEngine.send("master/send_picture", "on")
            }
            else {
                mqttEngine.send("master/send_picture", "off")
            }


        })
    }


    fun rgbArrayToArgb(pictureSize : Pair<Int, Int>, data : ByteArray) : IntBuffer {
        var dataIdx = 0
        val result = IntArray(pictureSize.first * pictureSize.second)
        for (y in 0 until pictureSize.second) {
            for (x in 0 until pictureSize.first) {

                val r = data[dataIdx++].toInt() and 0xFF
                val g = data[dataIdx++].toInt() and 0xFF
                val b = data[dataIdx++].toInt() and 0xFF

                result[y * pictureSize.first + x] = (255 shl 24) or (r shl 16) or (g shl 8) or b
            }
        }
        return IntBuffer.wrap(result)
    }

    fun onNewMessage(topic : String, value : MqttMessage) {

        if(topic.contains(devName)) {

            //TODO: add discovery function
            if (topic.contains("counter")) {

                text_counter.visibility = View.VISIBLE
                switchSendPicture.visibility = View.VISIBLE
                text_counter.text = value.toString()
            }
            if (topic.contains("picture")) {

                imageView_picture.visibility = View.VISIBLE

                val pictureSize = Pair(640, 480)
                text_topic.text = "$topic $pictureSize, ${value.payload.size}"


                val image = Bitmap.createBitmap(pictureSize.first, pictureSize.second, Bitmap.Config.ARGB_8888)

                val argbData = rgbArrayToArgb(pictureSize, value.payload)

                image.copyPixelsFromBuffer(argbData)

                imageView_picture.setImageBitmap(image)
                imageView_picture.scaleType = ImageView.ScaleType.FIT_XY


            }
            if (topic.contains("humidity")) {
                text_hum.visibility = View.VISIBLE
                text_hum.text = value.toString() + " %"
            }
            if (topic.contains("temp")) {
                text_temp.visibility = View.VISIBLE
                text_temp.text = value.toString() + " C"

            }
        }
    }
}
