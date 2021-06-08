package com.example.pantryfin.ui.browseItems

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pantryfin.R
import com.example.pantryfin.data.NetworkOp
import com.example.pantryfin.databinding.ActivityBrowseBinding


class BrowseItemsActivity : AppCompatActivity() {
    private lateinit var model: BrowseItemsViewModel
    private lateinit var binding: ActivityBrowseBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("NEWACTIVITY", "alive!")
        binding = ActivityBrowseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var code = intent.getStringExtra(Intent.EXTRA_TEXT)?:""

        val sharedPref = getSharedPreferences(getString(R.string.auth_data_file), Context.MODE_PRIVATE)
        val accessToken = sharedPref.getString(getString(R.string.access_token_key), "")?:""

        model = BrowseItemsViewModel(
            code)
        binding.browseVM = model
        val recyclerView = binding.recyclerItemsBrowse
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = BrowseItemAdapter()
        recyclerView.adapter = adapter
        model.fetchItems(accessToken,
            NetworkOp.getInstance(applicationContext),adapter)


        adapter.submitList(model.itemList)
        // don't need to observe anything as the list is static
    }

}