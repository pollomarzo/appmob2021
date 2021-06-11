package com.example.pantry.ui.browseItems

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pantry.R
import com.example.pantry.data.items.Item
import com.example.pantry.ui.itemList.ItemListAdapter


class SimpleAdapter(
    private val itemsToText: (Item)->String) : ListAdapter<Item, SimpleAdapter.SimpleHolder>(
    ItemListAdapter.ItemsComparator())  {

    class SimpleHolder(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v
        private var item: Item? = null
        private val textView: TextView = view.findViewById<TextView>(R.id.simple_text_view)

        fun bind(item:Item, itemsToText: (Item)->String){
            this.item = item
            // why oh why is the binding not working?????????
            textView.text = itemsToText(item)
        }
        companion object {
            // this handles creating the layout
            fun create(parent: ViewGroup): SimpleAdapter.SimpleHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.simple_item, parent, false)
                return SimpleAdapter.SimpleHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleHolder {
        return SimpleHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SimpleHolder, position: Int) {
        holder.bind(getItem(position), itemsToText)
    }

}