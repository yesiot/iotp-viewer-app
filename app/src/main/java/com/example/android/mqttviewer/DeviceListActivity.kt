package com.example.android.mqttviewer

import kotlinx.android.synthetic.main.activity_device_list.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView



class DeviceListActivity : AppCompatActivity(), OnTopicChangeInterface {
    private lateinit var viewAdapter: MqttDeviceAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onTopicChange() {
        viewAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewManager = LinearLayoutManager(this)

        val globalApp = applicationContext as App


        viewAdapter = MqttDeviceAdapter(globalApp.mqttEngine as TopicsProvider)
        globalApp.mqttEngine.setTopicChangeListener(this)

        recview_device_list.apply {

            //setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }
}
