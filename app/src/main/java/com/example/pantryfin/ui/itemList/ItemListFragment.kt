package com.example.pantryfin.ui.itemList

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pantryfin.R
import com.example.pantryfin.data.items.Item
import com.example.pantryfin.data.items.ItemViewModel
import com.example.pantryfin.data.items.ItemViewModelFactory
import com.example.pantryfin.data.items.ItemsApplication
import com.example.pantryfin.ui.addItem.AddItemActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ItemListFragment() : Fragment() {


    private lateinit var mContext: Context
    private lateinit var fab: View
    private val itemViewModel: ItemViewModel by activityViewModels {
        // todo: and if activity is null? like during rotation. maybe it just won't be
        ItemViewModelFactory((activity?.application as ItemsApplication).repository)
    }
    private val newItemActivityRequestCode = 1




    /**
     * Called when a fragment is first attached to its context.
     * needed to make sure we have a context for... something?
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_list_item, container, false)
        // val textView: TextView = root.findViewById(R.id.text_home)
        // homeViewModel.text.observe(viewLifecycleOwner, Observer {
        //    textView.text = it
        // })

        val recyclerView = root.findViewById<RecyclerView>(R.id.recycler_items)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = ItemListAdapter()
        recyclerView.adapter = adapter
        itemViewModel.allItem.observe(viewLifecycleOwner, Observer { items ->
            // Default method: onChanged
            // Update the cached copy of the items in the adapter.
            items?.let { adapter.submitList(it) }
        })

        fab = root.findViewById(R.id.addItemButton)
        fab.setOnClickListener {
            onAdd()
        }

        return root
    }

    private fun onAdd(){
        val intent = Intent(mContext, AddItemActivity::class.java)
        // todo: consider moving to contract registration
        // https://proandroiddev.com/is-onactivityresult-deprecated-in-activity-results-api-lets-deep-dive-into-it-302d5cf6edd
        startActivityForResult(intent, newItemActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newItemActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(AddItemActivity.EXTRA_REPLY)?.let {
                val item = Json.decodeFromString<Item>(it)
                itemViewModel.insert(item)
            }
            Toast.makeText(
                activity?.applicationContext,
                "nice",
                Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                activity?.applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }

}