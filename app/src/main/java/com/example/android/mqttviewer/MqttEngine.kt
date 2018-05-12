package com.example.android.mqttviewer

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

typealias NewDeviceHandler = (String) -> Unit
typealias MessageHandler = (String, String) -> Unit

class MqttEngine :  MqttCallbackExtended {
    private val TAG = "Mqtt engine"

    private lateinit var    mqttAndroidClient: MqttAndroidClient

    private var onNewDevice: NewDeviceHandler? = null

    private var onNewMessage: MessageHandler? = null


    fun setMessageHandler(handler : MessageHandler) {
        onNewMessage = handler
    }

    fun setNewDeviceHandler(handler : NewDeviceHandler) {
        onNewDevice = handler
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String) {
        Log.d(TAG, "Connection")

        //mqttAndroidClient.subscribe("\$SYS/#", 0)
        mqttAndroidClient.subscribe("+/discovery", 0)
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        Log.d(TAG, "Message: $topic $message")

        if(topic != null) {

            if(topic == "\$SYS/broker/log/N") {
                Log.d(TAG, message.toString())
            }
            if(topic.contains("discovery")) {
                onNewDevice?.invoke(topic.substringBefore("/"))
            }
            else {
                onNewMessage?.invoke(topic, message.toString())
            }
        }
    }

    override fun connectionLost(cause: Throwable?) {
        Log.d(TAG, "Disconnected")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }

    fun connect(context : Context, uri : String, user : String, password : CharArray) {

        mqttAndroidClient = MqttAndroidClient(context, uri, "mqtt viewer")

        mqttAndroidClient.setCallback(this)

        var options = MqttConnectOptions();

        options.keepAliveInterval = 20
        options.isCleanSession = true
        options.userName = user
        options.password = password

        try {
            mqttAndroidClient.connect(options)
        }
        catch(e : MqttException)
        {
            Log.e(TAG, e.message)
        }
    }
}
