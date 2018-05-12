package com.example.android.mqttviewer

import kotlinx.android.synthetic.main.activity_device_list.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log


class DeviceListActivity : AppCompatActivity(), TopicsProvider {

    private val TAG = "DeviceListActivity"

    private lateinit var viewAdapter: MqttDeviceAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val topics = mutableMapOf<String, String>()

    fun onNewMessage(topic : String, value : String) {
        val old : String? = topics[topic]
        topics[topic] = value

        if(old != value) {
            viewAdapter.notifyDataSetChanged()
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewManager = LinearLayoutManager(this)

        val globalApp = applicationContext as App


        viewAdapter = MqttDeviceAdapter(this as TopicsProvider)
        globalApp.setMessageHandler(::onNewMessage)

        recview_device_list.apply {

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }
}
