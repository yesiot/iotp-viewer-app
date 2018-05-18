package com.example.android.mqttviewer

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

enum class DeviceStatus {
    UNKNOWN, ALIVE, DEAD,
}

typealias DeviceStatusHandler = (String, DeviceStatus) -> Unit
typealias MessageHandler = (String, MqttMessage) -> Unit
typealias ConnectionStatusHandler = () -> Unit

interface MqttEngineInterface {
    fun setMessageHandler(handler : MessageHandler)
    fun setDeviceStatusHandler(handler : DeviceStatusHandler)
    fun setConnectionStatusHandler(handler : ConnectionStatusHandler)
    fun subscribeToDevice(devName : String)
    fun connect(context : Context, uri : String, user : String, password : CharArray)
}

class MqttEngine :  MqttCallbackExtended, MqttEngineInterface{

    private val TAG = "Mqtt engine"
    private val STATUS_ID = "status"

    private lateinit var mqttAndroidClient: MqttAndroidClient

    private var onConnectionStatusChanged: ConnectionStatusHandler? = null

    private var onDeviceStatusChanged: DeviceStatusHandler? = null

    private var onNewMessage: MessageHandler? = null


    override fun setConnectionStatusHandler(handler : ConnectionStatusHandler) {
        onConnectionStatusChanged = handler
    }

    override fun setMessageHandler(handler : MessageHandler) {
        onNewMessage = handler
    }

    override fun setDeviceStatusHandler(handler : DeviceStatusHandler) {
        onDeviceStatusChanged = handler
    }

    override fun subscribeToDevice(devName : String) {
        if(devName == "rpi0Test2")
            mqttAndroidClient.unsubscribe("rpi0Test/picture")
        else
            mqttAndroidClient.unsubscribe("rpi0Test2/picture")

        mqttAndroidClient.subscribe("+/$STATUS_ID", 0)
        mqttAndroidClient.subscribe("$devName/#", 0)
    }

    override fun connect(context : Context, uri : String, user : String, password : CharArray) {

        mqttAndroidClient = MqttAndroidClient(context, uri, "mqtt viewer")

        mqttAndroidClient.setCallback(this)

        var options = MqttConnectOptions();

        options.keepAliveInterval = 20
        options.isCleanSession = true
        if(!user.isBlank()) {
            options.userName = user
            options.password = password
        }


        try {
            mqttAndroidClient.connect(options)
        }
        catch(e : MqttException)
        {
            Log.e(TAG, e.message)
        }
    }


    override fun connectComplete(reconnect: Boolean, serverURI: String) {
        Log.d(TAG, "Connection")

        //mqttAndroidClient.subscribe("\$SYS/#", 0)
        mqttAndroidClient.subscribe("+/$STATUS_ID", 0)
        onConnectionStatusChanged?.invoke()
    }

    fun getDeviceName(topic: String): String {
        return topic.substringBefore("/")
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        Log.d(TAG, "Message: $topic $message")

        if(topic != null && message != null) {

            if(topic == "\$SYS/broker/log/N") {
                Log.d(TAG, message.toString())
            }
            if(topic.contains(STATUS_ID)) {

                val status : DeviceStatus = when (message.toString()) {
                    "ALIVE" -> DeviceStatus.ALIVE
                    "DEAD"  -> DeviceStatus.DEAD
                    else    -> DeviceStatus.UNKNOWN
                }

                onDeviceStatusChanged?.invoke(getDeviceName(topic), status)
            }
            else {
                onNewMessage?.invoke(topic, message)
            }
        }
    }

    override fun connectionLost(cause: Throwable?) {
        Log.d(TAG, "Disconnected")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
    }
}
