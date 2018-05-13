package com.example.android.mqttviewer

import android.content.Intent
import kotlinx.android.synthetic.main.activity_device_list.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View


class DeviceListActivity : AppCompatActivity(), DeviceInfoProvider, ListItemClickListener {

    private val TAG = "DeviceListActivity"

    private lateinit var viewAdapter: MqttDeviceAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val devices = mutableMapOf<String, DeviceStatus>()

    override fun onListItemClick(clickedItemIndex: Int) {
        val intent = Intent(this, DeviceViewActivity::class.java)
        intent.putExtra("DEVICE_NAME", getDeviceInfo(clickedItemIndex)?.first)
        startActivity(intent)
    }

    fun onNewDevice(devName : String, status : DeviceStatus ) {
        val old : DeviceStatus? = devices[devName]
        devices[devName] = status

        if(old != status) {
            viewAdapter.notifyDataSetChanged()
        }
    }


    override fun getDeviceInfo(position: Int) : Pair<String, DeviceStatus>? {
        try {
            val sequence = devices.asSequence()
            return Pair(sequence.elementAt(position).key, sequence.elementAt(position).value)
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }
        return null
    }

    override fun getNumberOfDevices(): Int {
        return devices.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        viewManager = LinearLayoutManager(this)

        val globalApp = applicationContext as App


        viewAdapter = MqttDeviceAdapter(this as DeviceInfoProvider, this as ListItemClickListener)
        globalApp.getMqttEngine().setDeviceStatusHandler(::onNewDevice)

        recview_device_list.apply {

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }
}
