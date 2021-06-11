package com.uni.pantry.ui.browseItems

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.uni.pantry.R
import com.uni.pantry.data.items.Item

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
        private val itemDescription: TextView = itemView.findViewById(R.id.item_browse_description)
        private val cardView: CardView = itemView.findViewById(R.id.card_view)
        private val button: ImageButton = itemView.findViewById(R.id.collapse_icon)
        private val hiddenView: LinearLayout = itemView.findViewById(R.id.hidden_section)

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
            }
            button.setOnClickListener { switchVisibility() }
            itemName.text = item.name
            itemDescription.text = item.description
        }

        private fun switchVisibility(){
            if (hiddenView.visibility == View.GONE) {
                val transition = AutoTransition()
                transition.duration = 35
                TransitionManager.beginDelayedTransition(
                    cardView,
                    transition
                )
                hiddenView.visibility = View.VISIBLE
                button.setImageResource(R.drawable.ic_baseline_expand_less_24)
            } else {
                val transition = AutoTransition()
                transition.addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        hiddenView.visibility = View.GONE
                    }
                })
                TransitionManager.beginDelayedTransition(
                    cardView,
                    transition
                )
                button.setImageResource(R.drawable.ic_baseline_expand_more_24)
            }
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