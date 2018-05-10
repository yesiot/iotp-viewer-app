package com.example.android.mqttviewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import kotlinx.android.synthetic.main.activity_main.*

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

class MainActivity : AppCompatActivity() {

    private val TAG = "MyActivity"
    private val PREFERENCES_NAME = "mqtt app preferences"

    private lateinit var mqttAndroidClient:MqttAndroidClient

    private fun saveSettings() {
        val editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit()
        editor.putString("mqttServer", mqttServer.text.toString())
        editor.putInt("mqttPortNumber", mqttPortNumber.text.toString().toInt())
        editor.putString("mqttUserName", mqttUserName.text.toString())
        editor.putString("mqttUserPassword", mqttPassword.text.toString())
        editor.apply()
    }

    private fun restoreSetting() {
        val prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        mqttServer.setText(prefs.getString("mqttServer", ""))
        mqttPortNumber.setText(prefs.getInt("mqttPortNumber", 1883).toString())
        mqttUserName.setText(prefs.getString("mqttUserName", ""))
        mqttPassword.setText(prefs.getString("mqttUserPassword", ""))
    }


    private fun connect() {

        saveSettings()

        val serverURI = "tcp://" + mqttServer.text.toString() + ":" + mqttPortNumber.text.toString()

        mqttAndroidClient = MqttAndroidClient(applicationContext, serverURI, "mqtt viewer")

        mqttAndroidClient.setCallback(object : MqttCallbackExtended {

            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                Log.d(TAG, "Connection")

                button_connect.text = "Connected"

                mqttAndroidClient.subscribe("\$SYS/#", 0)
                mqttAndroidClient.subscribe("hello/#", 0)
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Message " + topic + " " + message)

                if(topic == "hello/countdown")
                    text_hello_topic.text = message.toString()
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connected")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })


        var options = MqttConnectOptions();

        options.keepAliveInterval = 20
        options.isCleanSession = true
        options.userName = mqttUserName.text.toString()
        options.password = mqttPassword.text.toString().toCharArray()

        try {
            var token = mqttAndroidClient.connect(options)

            token.waitForCompletion(1000)
        }
        catch(e : MqttException)
        {
            Log.e(TAG, e.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        restoreSetting()

        button_connect.setOnClickListener() {
            connect()
        }
    }
}
