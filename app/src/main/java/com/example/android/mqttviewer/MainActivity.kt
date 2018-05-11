package com.example.android.mqttviewer

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MyActivity"
    private val PREFERENCES_NAME = "mqtt app preferences"

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



    override fun onCreate(savedInstanceState: Bundle?) {

        val globalApp = applicationContext as App


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        restoreSetting()

        button_connect.setOnClickListener() {
            saveSettings()

            val serverURI = "tcp://" + mqttServer.text.toString() + ":" + mqttPortNumber.text.toString()

            globalApp.mqttEngine.connect(applicationContext, serverURI, mqttUserName.text.toString(), mqttPassword.text.toString().toCharArray())

            val intent = Intent(this, DeviceListActivity::class.java)
            startActivity(intent)
        }
    }
}
