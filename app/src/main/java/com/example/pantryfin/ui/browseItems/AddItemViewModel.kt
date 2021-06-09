package com.example.pantryfin.ui.browseItems

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.pantryfin.data.NetworkOp
import com.example.pantryfin.data.items.Item
import com.example.pantryfin.ui.addItem.itemsURL
import com.example.pantryfin.ui.login.BrowseResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class AddItemViewModel() : ViewModel() {
    private var _itemList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>> = _itemList

    private var _state: MutableLiveData<SearchState> = MutableLiveData(SearchState.INIT)
    val state: LiveData<SearchState> = _state
    private var _response: MutableLiveData<String> = MutableLiveData("default")
    var response: LiveData<String> = _response

    private var _code: MutableLiveData<String> = MutableLiveData("")
    var code: LiveData<String> = _code

    var isCodeValid : MutableLiveData<Boolean?> = MutableLiveData(true)

    fun setCode(s: Editable){
        _code.value = s.toString()
        validateCode()
    }

    private fun validateCode(){
        try {
            Integer.parseInt(code.value)
            isCodeValid.value = true
        } catch(e: Exception){
            isCodeValid.value = false
        }
    }

    var selected: MutableLiveData<Item?> = MutableLiveData()

    private fun getItems(items: BrowseResponse): List<Item> {
        val it = mutableListOf<Item>()
        for (i in items.products) {
            it.add(Item(i.barcode, i.name, 0, i.description, 1, "DEFAULT"))
        }
        return it
    }

    fun fetchItems(
        itemCode: String, accessToken: String, instance: NetworkOp,
    ) {
        _state.value = SearchState.SEARCHING
        val reqURL = itemsURL + itemCode
        Log.d("NETWORKING", "asking for products with code $reqURL with token $accessToken")
        //todo handle no access token? or should it be above?
        instance.addToRequestQueue(
            ItemRequest(
                reqURL,
                { response ->
                    Log.d("NETWORKING", response.toString())
                    _itemList.value =
                        getItems(Json.decodeFromString<BrowseResponse>(response.toString()))
                    _state.value =
                        if (itemList.value?.size == 0) SearchState.NO_ITEMS else SearchState.FOUND_ITEMS
                },
                { error ->
                    Log.d("NETWORKING", error.toString())
                    _state.value = SearchState.ERROR
                    // idfk
                },
                accessToken
            )
        )
    }

    fun updateInfoText(
        initSearch: String, startSearch: String,
        emptyResponse: String, realResponse: String,
        errorSearch: String
    ) {
        _response.value = when (_state.value) {
            SearchState.INIT -> initSearch
            SearchState.SEARCHING -> startSearch
            SearchState.FOUND_ITEMS -> realResponse
            SearchState.NO_ITEMS -> emptyResponse
            SearchState.ERROR -> errorSearch
            else -> "WHAT THE HELL"
        }
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

    enum class SearchState {
        INIT, SEARCHING, FOUND_ITEMS, NO_ITEMS, ERROR
    }

}