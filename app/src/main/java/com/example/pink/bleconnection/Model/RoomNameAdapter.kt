package com.example.pink.bleconnection.Model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.pink.bleconnection.R
import kotlinx.android.synthetic.main.roomdetail_listview_show.view.*

class RoomNameAdapter(roomArray: ArrayList<RoomDetail>,context: Context)
    :ArrayAdapter<RoomDetail>(context, R.layout.roomdetail_listview_show,roomArray){

    private class RoomDetailItemViewHolder{
        internal var rooeName: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder : RoomDetailItemViewHolder
        if (view == null){
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.roomdetail_listview_show, parent, false)
            viewHolder = RoomDetailItemViewHolder()
            viewHolder.rooeName = view.text_roomdetail_listview_1
        }else{
            viewHolder = view.tag as RoomDetailItemViewHolder
        }
        val item = getItem(position)
        viewHolder.rooeName!!.text = item!!.getRoomName()
        viewHolder.rooeName!!.setOnClickListener {
            
        }
        view!!.tag = viewHolder
        return super.getView(position, convertView, parent)

    }
}