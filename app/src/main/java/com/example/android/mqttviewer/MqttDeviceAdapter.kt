package com.example.android.mqttviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.Serializable

interface DeviceInfoProvider {
    fun getDeviceInfo(position: Int) : Pair<String, DeviceStatus>?
    fun getNumberOfDevices() : Int
}

interface ListItemClickListener {
    fun onListItemClick(clickedItemIndex: Int)
}

class MqttDeviceAdapter(deviceInfoProvider : DeviceInfoProvider, onClickListener : ListItemClickListener) :
        RecyclerView.Adapter<MqttDeviceAdapter.DeviceViewHolder>(), Serializable {

    val deviceInfoProvider = deviceInfoProvider

    protected val onClickListener = onClickListener


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
            when(deviceInfo.second) {
                DeviceStatus.ALIVE -> holder.status.text = ":)" //TODO show status icon
                DeviceStatus.DEAD -> holder.status.text = ":("
                DeviceStatus.UNKNOWN -> holder.status.text = "?"
            }
        }
    }

    override fun getItemCount() = deviceInfoProvider.getNumberOfDevices()

    inner class DeviceViewHolder(val deviceItemView: View) : RecyclerView.ViewHolder(deviceItemView), View.OnClickListener
    {
        val name : TextView = deviceItemView.findViewById(R.id.tv_device_name) as TextView
        val status : TextView = deviceItemView.findViewById(R.id.tv_device_status) as TextView

        init {
            deviceItemView.setOnClickListener(this)
        }

        override fun onClick(v : View) {
            onClickListener.onListItemClick(adapterPosition)
        }
    }
}