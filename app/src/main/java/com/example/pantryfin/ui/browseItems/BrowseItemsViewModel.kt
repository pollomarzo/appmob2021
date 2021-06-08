package com.example.pantryfin.ui.browseItems

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.pantryfin.data.NetworkOp
import com.example.pantryfin.data.items.Item
import com.example.pantryfin.ui.addItem.AddItemActivity
import com.example.pantryfin.ui.addItem.itemsURL
import com.example.pantryfin.ui.login.BrowseResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class BrowseItemsViewModel(private val itemCode: String) : ViewModel() {
    var itemList: MutableList<Item>? = null

    private fun getItems(items: BrowseResponse): MutableList<Item> {
        val it = mutableListOf<Item>()
        var idx: Int = 0
        for (i in items.products) {
            idx++
            it.add(Item(i.barcode, i.name, 0, i.description, 1, "DEFAULT"))
        }
        itemList = it
        return it
    }

    fun fetchItems(accessToken: String, instance: NetworkOp, adapter: BrowseItemAdapter) {
        val reqURL = itemsURL + itemCode
        Log.d("NETWORKING", "asking with token $accessToken")
        //todo handle no access token? or should it be above?
        instance.addToRequestQueue(
            ItemRequest(
                reqURL,
                { response ->
                    Log.d("NETWORKING", response.toString())
                    getItems(Json.decodeFromString<BrowseResponse>(response.toString()))
                    adapter.submitList(itemList)
                },
                { error ->
                    Log.d("NETWORKING", error.toString())
                    // idfk
                },
                accessToken
            )
        )
    }

    class ItemRequest(
        url: String,
        response: Response.Listener<org.json.JSONObject>,
        error: Response.ErrorListener,
        private val accessToken: String
    ) : JsonObjectRequest(url, null, response, error) {
        override fun getHeaders(): MutableMap<String, String> {
            val params = HashMap<String, String>()
            params["Authorization"] = "Bearer $accessToken"
            return params
        }
    }

}