package com.example.pantryfin.ui.browseItems

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pantryfin.R
import com.example.pantryfin.data.items.Item

class BrowseItemAdapter :
    ListAdapter<Item, BrowseItemAdapter.BrowseItemViewHolder>(ItemsComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseItemViewHolder {
        return BrowseItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BrowseItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(holder, current)
    }

    class BrowseItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.item_browse_name)
        private val itemType: TextView = itemView.findViewById(R.id.item_browse_type)

        // this handles replacing the values in the create()d item
        fun bind(holder: BrowseItemViewHolder, item: Item) {
            // Get element from your dataset at this position and replace the contents of the view
            // with that element

            itemView.setOnClickListener {
                Log.d(
                    "BROWSE_LIST",
                    "Element $adapterPosition clicked."
                )
            }
            itemName.text = item.name
            itemType.text = item.type
        }

        companion object {
            // this handles creating the layout
            fun create(parent: ViewGroup): BrowseItemViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_browse, parent, false)
                return BrowseItemViewHolder(view)
            }
        }
    }

    class ItemsComparator : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.code == newItem.code && oldItem.name == newItem.name
        }
    }
}