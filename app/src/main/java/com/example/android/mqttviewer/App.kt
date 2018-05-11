package com.example.android.mqttviewer

import android.app.Application
import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

interface OnTopicChangeInterface {
    fun onTopicChange()
}

class MqttEngine : TopicsProvider, MqttCallbackExtended {
    private val TAG = "Mqtt engine"

    private lateinit var mqttAndroidClient: MqttAndroidClient

    private var topicChangeListener : OnTopicChangeInterface? = null

    val topics = mutableMapOf<String, String>()

    fun addTopic(topic : String, value : String) {
        val old : String? = topics[topic]
        topics[topic] = value

        if(old != value) {
            topicChangeListener?.onTopicChange()
        }
    }

    fun setTopicChangeListener(listener : OnTopicChangeInterface) {
        topicChangeListener = listener
    }

    override fun getTopic(position: Int) : Pair<String, String>? {
        try {
            val sequence = topics.asSequence()
            return Pair(sequence.elementAt(position).key, sequence.elementAt(position).value)
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }
        return null
    }

    override fun getSize() : Int {
        return topics.size
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String) {
        Log.d(TAG, "Connection")

        mqttAndroidClient.subscribe("\$SYS/#", 0)
        mqttAndroidClient.subscribe("hello/#", 0)
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        Log.d(TAG, "Message: $topic $message")

        if(topic != null) {

            if(topic == "\$SYS/broker/log/N") {
                Log.d(TAG, message.toString())
            }
            else {
                addTopic(topic, message.toString())
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

class App : Application() {
    val mqttEngine = MqttEngine()
}