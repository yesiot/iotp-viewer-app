package com.example.android.mqttviewer

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val PREFERENCES_NAME = "mqtt app preferences"

    private fun saveSettings() {
        val editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit()
        editor.putString("mqttServer", text_mqttServer.text.toString())
        editor.putInt("mqttPortNumber", text_mqttPortNumber.text.toString().toInt())
        editor.putString("mqttUserName", text_mqttUserName.text.toString())
        editor.putString("mqttUserPassword", text_mqttPassword.text.toString())
        editor.apply()
    }

    private fun restoreSetting() {
        val prefs = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
        text_mqttServer.setText(prefs.getString("mqttServer", ""))
        text_mqttPortNumber.setText(prefs.getInt("mqttPortNumber", 1883).toString())
        text_mqttUserName.setText(prefs.getString("mqttUserName", ""))
        text_mqttPassword.setText(prefs.getString("mqttUserPassword", ""))
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        val globalApp = applicationContext as App


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        restoreSetting()

        button_connect.setOnClickListener() {
            saveSettings()

            val serverURI = "tcp://" + text_mqttServer.text.toString() + ":" + text_mqttPortNumber.text.toString()

            globalApp.getMqttEngine().connect(applicationContext, serverURI, text_mqttUserName.text.toString(), text_mqttPassword.text.toString().toCharArray())

            val intent = Intent(this, DeviceListActivity::class.java)
            startActivity(intent)
        }
    }
}
