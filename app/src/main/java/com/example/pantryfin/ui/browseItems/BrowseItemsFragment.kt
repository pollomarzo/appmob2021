package com.example.pantryfin.ui.browseItems

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantryfin.databinding.FragmentBrowseBinding


class BrowseItemsFragment : Fragment() {
    private val model: BrowseItemsViewModel by activityViewModels()
    private lateinit var binding: FragmentBrowseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d("NEWACTIVITY", "alive!")
        binding = FragmentBrowseBinding.inflate(layoutInflater)
        val root = binding.root

        binding.browseVM = model
        val recyclerView = binding.recyclerItemsBrowse
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = BrowseItemAdapter()

        recyclerView.adapter = adapter
        model.itemList.observe(viewLifecycleOwner, { items ->
            // Update the cached copy of the items in the adapter.
            items?.let { adapter.submitList(it) }
        })
        return root
    }

}