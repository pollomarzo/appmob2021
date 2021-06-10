package com.example.pantryfin.ui.browseItems

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pantryfin.R
import com.example.pantryfin.data.items.Item

class BrowseItemAdapter(private val selectedColor: Int, private val defaultColor: Int) :
    ListAdapter<Item, BrowseItemAdapter.BrowseItemViewHolder>(ItemsComparator()) {
    var selected: MutableLiveData<Item?> = MutableLiveData()

    private fun setSelected(item: Item): Boolean{
        Log.d("BROWSE_LIST", "currently ${selected.value}, setting selected to $item")
        val alreadyInside = (selected.value == item)

        if (alreadyInside) selected.value = null else selected.value = item
        return !alreadyInside
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrowseItemViewHolder {
        return BrowseItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: BrowseItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(selected.value, this::setSelected, holder, current,selectedColor, defaultColor)
    }

    class BrowseItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.item_browse_name)
        private val itemType: TextView = itemView.findViewById(R.id.item_browse_type)
        private val cardView: CardView = itemView.findViewById(R.id.card_view)

        // this handles replacing the values in the create()d item
        fun bind(selected: Item?, setSelected: (Item) -> Boolean, holder: BrowseItemViewHolder, item: Item,
                 selectedColor: Int, defaultColor: Int) {
            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            // Use hijacked field item.type
            val color = if (selected?.type == item.type) selectedColor else defaultColor
            cardView.setCardBackgroundColor(color)

            itemView.setOnClickListener {
                setSelected(item)
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