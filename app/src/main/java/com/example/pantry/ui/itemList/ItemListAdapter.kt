package com.example.pantry.ui.itemList

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.example.pantry.R
import com.example.pantry.data.items.Item


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
/**
 * default constructor initializes the dataset of the Adapter.
 *
 * @param dataSet Array<Item> containing the data to populate views to be used by RecyclerView.
 */


class ItemListAdapter(
    val increaseAmount: (Item) -> Int,
    val lowerAmount: (Item) -> Int,
    val deleteItem: (Item) -> Unit,
    val getImageId: (String) -> Int,
    val editItem: (Item) -> Unit
) :
    ListAdapter<Item, ItemListAdapter.ItemViewHolder>(ItemsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(holder, current, increaseAmount, lowerAmount, deleteItem, getImageId, editItem)
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
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        private val imageView: ImageView = itemView.findViewById(R.id.type_icon)

        // this handles replacing the values in the create()d item
        fun bind(
            holder: ItemViewHolder, item: Item,
            increaseAmount: (Item) -> Int,
            lowerAmount: (Item) -> Int,
            deleteItem: (Item) -> Unit,
            getImageId: (String) -> Int,
            editItem: (Item) -> Unit
        ) {
            itemName.setOnLongClickListener {
                editItem(item)
                true
            }
            itemAmount.text = item.amount.toString()
            itemName.text = item.name
            itemType.text = item.type
            itemDescription.text = item.description

            moreButton.setOnClickListener {
                itemAmount.text = increaseAmount(item).toString()
            }
            lessButton.setOnClickListener {
                itemAmount.text = lowerAmount(item).toString()
            }
            deleteButton.setOnClickListener {
                deleteItem(item)
            }
            itemName.setOnClickListener {
                //hide or show rest
                switchVisibility(hiddenView, moreButton, lessButton, deleteButton)
            }
            hiddenView.setOnClickListener {
                switchVisibility(hiddenView, moreButton, lessButton, deleteButton)
            }
            Log.d("IMAGES", "asking for image with type ${item.type}," +
                    " got ${getImageId(item.type)}")
            imageView.setImageResource(getImageId(item.type))
        }

        private fun switchVisibility(
            hiddenView: LinearLayout,
            moreButton: ImageButton,
            lessButton: ImageButton,
            deleteButton: ImageButton
        ) {
            if (hiddenView.visibility == View.GONE) {
                val transition = AutoTransition()
                transition.duration = 35
                TransitionManager.beginDelayedTransition(
                    cardView,
                    transition
                )
                hiddenView.visibility = View.VISIBLE
                moreButton.visibility = View.GONE
                lessButton.visibility = View.GONE
                deleteButton.visibility = View.VISIBLE
                hiddenView.alpha = 1F
            } else {
                val transition = AutoTransition()
                transition.addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        hiddenView.visibility = View.GONE
                        moreButton.visibility = View.VISIBLE
                        lessButton.visibility = View.VISIBLE
                        deleteButton.visibility = View.GONE
                    }
                })
                TransitionManager.beginDelayedTransition(
                    cardView,
                    transition
                )
                // need to trigger the transition... look i don't like this either
                // alternative was to find out how Scenes work.. no thanks
                hiddenView.alpha = 0.99F
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
            return oldItem.id == newItem.id &&
                    oldItem.amount == newItem.amount &&
                    oldItem.type == newItem.type &&
                    oldItem.description == newItem.description
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return  oldItem.name == newItem.name &&
                    oldItem.amount == newItem.amount &&
                    oldItem.type == newItem.type &&
                    oldItem.description == newItem.description
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