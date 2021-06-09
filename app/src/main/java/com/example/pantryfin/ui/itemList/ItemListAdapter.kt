package com.example.pantryfin.ui.itemList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.example.pantryfin.R
import com.example.pantryfin.data.items.Item


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
/**
 * default constructor initializes the dataset of the Adapter.
 *
 * @param dataSet Array<Item> containing the data to populate views to be used by RecyclerView.
 */


class ItemListAdapter : ListAdapter<Item, ItemListAdapter.ItemViewHolder>(ItemsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(holder, current)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemAmount: TextView = itemView.findViewById(R.id.item_amount)
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val itemDescription: TextView = itemView.findViewById(R.id.item_description)
        private val itemType: TextView = itemView.findViewById(R.id.item_type)
        private val moreButton: ImageButton = itemView.findViewById(R.id.more_button)
        private val lessButton: ImageButton = itemView.findViewById(R.id.less_button)
        private val hiddenView: LinearLayout = itemView.findViewById(R.id.hidden_view);
        private val cardView: CardView = itemView.findViewById(R.id.card_view)

        // this handles replacing the values in the create()d item
        fun bind(holder: ItemViewHolder, item: Item) {
            itemAmount.text = item.amount.toString()
            itemName.text = item.name
            itemType.text = item.type
            itemDescription.text = item.description

            moreButton.setOnClickListener {
                itemAmount.text = (itemAmount.text.toString().toInt() + 1).toString()
            }
            lessButton.setOnClickListener {
                itemAmount.text = (itemAmount.text.toString().toInt() - 1).toString()
            }
            itemName.setOnClickListener {
                //hide or show rest
                switchVisibility(hiddenView)
            }
            hiddenView.setOnClickListener {
                switchVisibility(hiddenView)
            }
        }

        private fun switchVisibility(hiddenView: LinearLayout) {
            if (hiddenView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(
                    cardView,
                    AutoTransition()
                )
                hiddenView.visibility = View.VISIBLE
                hiddenView.alpha = 1F
            } else {
                TransitionManager.beginDelayedTransition(
                    cardView,
                    AutoTransition().addListener(object : TransitionListenerAdapter() {
                        override fun onTransitionEnd(transition: Transition) {
                            hiddenView.visibility = View.GONE
                        }
                    }
                    ))
                // need to trigger the transition... look i don't like this either
                hiddenView.alpha = 0.99F
            }
            /*TransitionListener {
                hiddenView.visibility = View.GONE
            })*/
        }

        class TransitionListener(private val onEnd: () -> Unit) : TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                onEnd()
                Log.d("TRANSITIONS", "i just called it.")
                super.onTransitionEnd(transition)
            }
        }

        companion object {
            // this handles creating the layout
            fun create(parent: ViewGroup): ItemViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item, parent, false)
                return ItemViewHolder(view)
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


//class ItemAdapter (private val mDataSet: Array<Item>) :
//    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
//    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
//    /**
//     * Provide a reference to the type of views that you are using (custom ViewHolder)
//     */
//    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
//        val itemCode: TextView
//        val itemName: TextView
//
//        init {
//            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    Log.d(TAG, "Element $adapterPosition clicked.")
//                }
//            })
//            itemCode = v.findViewById(R.id.item_code)
//            itemName = v.findViewById(R.id.item_name)
//        }
//    }
//
//    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
//    // Create new views (invoked by the layout manager)
//    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
//        // Create a new view.
//        val v: View = LayoutInflater.from(viewGroup.context)
//            .inflate(R.layout.item, viewGroup, false)
//        return ViewHolder(v)
//    }
//
//    // END_INCLUDE(recyclerViewOnCreateViewHolder)
//    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
//    // Replace the contents of a view (invoked by the layout manager)
//    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
//        Log.d(TAG, "Element $position set.")
//
//        // Get element from your dataset at this position and replace the contents of the view
//        // with that element
//        val (code, name) = mDataSet[position]
//        viewHolder.itemCode.text = code
//        viewHolder.itemName.text = name
//    }
//
//    // END_INCLUDE(recyclerViewOnBindViewHolder)
//    // Return the size of your dataset (invoked by the layout manager)
//    override fun getItemCount(): Int {
//        return mDataSet.size
//    }
//
//    companion object {
//        private const val TAG = "CustomAdapter"
//    }
//    // END_INCLUDE(recyclerViewSampleViewHolder)
//}