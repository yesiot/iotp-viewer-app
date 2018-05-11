package com.example.android.mqttviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.Serializable

interface TopicsProvider {
    fun getTopic(position: Int) : Pair<String, String>?
    fun getSize() : Int
}

class MqttDeviceAdapter(topicsProvider : TopicsProvider) : RecyclerView.Adapter<MqttDeviceAdapter.DeviceViewHolder>(), Serializable {

    val topicsProvider = topicsProvider

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MqttDeviceAdapter.DeviceViewHolder {

        val view = LayoutInflater.from(parent.context)
                  .inflate(R.layout.mqtt_device_item, parent, false)


        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {

        val topic = topicsProvider.getTopic(position)
        if(topic != null) {
            holder.listItemNumberView.text = topic.first
            holder.viewHolderIndex.text = topic.second
        }
    }

    override fun getItemCount() = topicsProvider.getSize()

    class DeviceViewHolder(val deviceItemView: View) : RecyclerView.ViewHolder(deviceItemView)
    {
        val listItemNumberView : TextView = deviceItemView.findViewById(R.id.tv_item_number) as TextView
        val viewHolderIndex : TextView = deviceItemView.findViewById(R.id.tv_view_holder_instance) as TextView
    }
}