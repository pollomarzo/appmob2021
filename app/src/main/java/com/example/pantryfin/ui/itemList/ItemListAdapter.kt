package com.example.pantryfin.ui.itemList

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
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
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val itemType: TextView = itemView.findViewById(R.id.item_type)

        // this handles replacing the values in the create()d item
        fun bind(holder: ItemViewHolder, item: Item) {
            // Get element from your dataset at this position and replace the contents of the view
            // with that element

            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    Log.d(TAG, "Element $adapterPosition clicked.")
//                }
//            })
            itemName.text = item.name
            itemType.text = item.type
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