package com.example.android.mqttviewer

import android.app.Application
import android.content.Context

class App : Application() {
    private val mqttEngine = MqttEngine()

    fun getMqttEngine () : MqttEngineInterface{
        return mqttEngine
    }
}