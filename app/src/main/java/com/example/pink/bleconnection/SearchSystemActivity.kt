package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_search_system.*

class SearchSystemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_system)
        val itemPlaceList = arrayOf("Alcatiob","AlBoBara","Action","BioHaJun")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemPlaceList)
        mListView.setAdapter(adapter)

        searchBar.addTextChangeListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                mListView.visibility = View.VISIBLE
            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                adapter.getFilter().filter(charSequence)
            }
            override fun afterTextChanged(editable: Editable) {
            }
        })
        searchBar.setOnKeyListener { v, keyCode, event ->  if ((event.action == KeyEvent.KEYCODE_ENTER)||(event.action == KeyEvent.ACTION_DOWN)){
            toast("TheValue is "+ v)
            true
        }else false}
        editText_search.setOnClickListener {
            mListView.visibility = View.VISIBLE
        }
        editText_search.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                mListView.visibility = View.VISIBLE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.getFilter().filter(s)
            }

        })
        editText_search.setOnKeyListener { v, keyCode, event ->  if ((event.action == KeyEvent.KEYCODE_ENTER)||(event.action == KeyEvent.ACTION_DOWN)){
            mListView.visibility = View.GONE
            true
        }else false}
        mListView.setOnItemClickListener { parent, view, position, id ->
            searchBar.text = itemPlaceList[position]
            editText_search.setText(itemPlaceList[position])
            mListView.visibility = View.GONE
        }

    }

    fun toast(text : String){
        Toast.makeText(this,text, Toast.LENGTH_SHORT).show()
    }
}

