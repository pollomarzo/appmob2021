package com.example.pantryfin.ui.browseItems

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

class BrowseItemsViewModel() : ViewModel() {
    private var _itemList =  MutableLiveData<List<Item>>()
    public val itemList: LiveData<List<Item>> = _itemList

    private fun getItems(items: BrowseResponse): List<Item> {
        val it = mutableListOf<Item>()
        var idx: Int = 0
        for (i in items.products) {
            idx++
            it.add(Item(i.barcode, i.name, 0, i.description, 1, "DEFAULT"))
        }
        return it
    }

    fun fetchItems(itemCode: String, accessToken: String, instance: NetworkOp) {
        val reqURL = itemsURL + itemCode
        Log.d("NETWORKING", "asking for products with code $reqURL with token $accessToken")
        //todo handle no access token? or should it be above?
        instance.addToRequestQueue(
            ItemRequest(
                reqURL,
                { response ->
                    Log.d("NETWORKING", response.toString())
                    _itemList.value = getItems(Json.decodeFromString<BrowseResponse>(response.toString()))
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