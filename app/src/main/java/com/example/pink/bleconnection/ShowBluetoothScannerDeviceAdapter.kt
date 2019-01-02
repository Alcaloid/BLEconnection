package com.example.pink.bleconnection

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bluetoothinfo.view.*

class ShowBluetoothScannerDeviceAdapter (val itemsDeviceNameArray : ArrayList<String>,val itemsDeviceRSSIArray : ArrayList<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return itemsDeviceNameArray.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.bluetoothinfo, p0, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.devicename.text = itemsDeviceNameArray.get(position)
        holder.devicerssi.text = itemsDeviceRSSIArray.get(position)
//        holder.bg.setBackgroundColor()
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val devicename = view.devicename
    val devicerssi = view.devicerssi
    val bg = view.deviceinfobg
}
