package com.example.android.mqttviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.Serializable

interface DeviceInfoProvider {
    fun getDeviceInfo(position: Int) : Pair<String, String>?
    fun getNumberOfDevices() : Int
}

class MqttDeviceAdapter(deviceInfoProvider : DeviceInfoProvider) : RecyclerView.Adapter<MqttDeviceAdapter.DeviceViewHolder>(), Serializable {

    val deviceInfoProvider = deviceInfoProvider

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MqttDeviceAdapter.DeviceViewHolder {

        val view = LayoutInflater.from(parent.context)
                  .inflate(R.layout.mqtt_device_item, parent, false)


        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {

        val deviceInfo = deviceInfoProvider.getDeviceInfo(position)
        if(deviceInfo != null) {
            holder.name.text = deviceInfo.first
            holder.status.text = deviceInfo.second
        }
    }

    override fun getItemCount() = deviceInfoProvider.getNumberOfDevices()

    class DeviceViewHolder(val deviceItemView: View) : RecyclerView.ViewHolder(deviceItemView)
    {
        val name : TextView = deviceItemView.findViewById(R.id.tv_device_name) as TextView
        val status : TextView = deviceItemView.findViewById(R.id.tv_device_status) as TextView
    }
}