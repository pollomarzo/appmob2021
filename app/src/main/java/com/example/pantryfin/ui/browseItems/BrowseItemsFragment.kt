package com.example.pantryfin.ui.browseItems

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantryfin.R
import com.example.pantryfin.databinding.FragmentBrowseBinding


class BrowseItemsFragment : Fragment() {
    private val model: AddItemViewModel by activityViewModels()
    private lateinit var binding: FragmentBrowseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentBrowseBinding.inflate(layoutInflater)
        val root = binding.root

        val recyclerView = binding.recyclerItemsBrowse
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = BrowseItemAdapter(
            selectedColor = resources.getColor(R.color.blueCrayola),
            defaultColor = resources.getColor(R.color.white)
        )
        adapter.selected.observe(viewLifecycleOwner, {
            adapter.notifyDataSetChanged()
        })

        recyclerView.adapter = adapter
        model.itemList.observe(viewLifecycleOwner, { items ->
            // Update the cached copy of the items in the adapter.
            items?.let { adapter.submitList(it) }
        })
        return root
    }

}