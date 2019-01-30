package com.example.pink.bleconnection

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_search_system.*

class SearchSystemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_system)
        val itemPlaceList = arrayOf("Alcatiob","AlBoBara","Action","BioHaJun")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemPlaceList)
        mListView.setAdapter(adapter)

        serachBackground.setOnClickListener {
            mListView.visibility = View.INVISIBLE
        }
        searchBar.addTextChangeListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                mListView.visibility = View.VISIBLE
            }
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                adapter.getFilter().filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        searchBar.clearSuggestions()
        mListView.setOnItemClickListener { parent, view, position, id ->
            searchBar.text = itemPlaceList[position]
        }

    }
}
