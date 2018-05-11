package com.example.android.mqttviewer

import android.app.Application
import android.content.Context

class App : Application() {
    private val mqttEngine = MqttEngine()

    fun setMessageConsumer(consumer : MessageConsumerInterface) {
        mqttEngine.setMessageConsumer(consumer)
    }

    fun connectToMqttSetrver(context : Context, uri : String, user : String, password : CharArray) {
        mqttEngine.connect(context, uri, user, password)
    }
}