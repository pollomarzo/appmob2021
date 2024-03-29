package com.uni.pantry.ui.addItem

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.uni.pantry.data.NetworkOp
import com.uni.pantry.data.items.Item
import com.uni.pantry.ui.login.BrowseResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.json.JSONObject

const val rateItemURL = "https://lam21.modron.network/votes"

class AddItemViewModel() : ViewModel() {
    var sessionToken: String = ""

    private var _itemList = MutableLiveData<List<Item>>()
    val itemList: LiveData<List<Item>> = _itemList

    private var _state: MutableLiveData<SearchState> = MutableLiveData(SearchState.INIT)
    val state: LiveData<SearchState> = _state
    private var _response: MutableLiveData<String> = MutableLiveData("default")
    var response: LiveData<String> = _response

    private var _code: MutableLiveData<String> = MutableLiveData("")
    var code: LiveData<String> = _code

    var isCodeValid: MutableLiveData<Boolean?> = MutableLiveData(false)

    var selected: MutableLiveData<Item?> = MutableLiveData(null)

    fun setCode(s: Editable) {
        _code.value = s.toString()
        validateCode()
    }

    private fun validateCode() {
        isCodeValid.value = code.value?.matches(Regex("\\d+"))
    }


    private fun getItems(items: BrowseResponse): List<Item> {
        val it = mutableListOf<Item>()
        for (i in items.products) {
            // hijack type field! pirates babyyy
            it.add(Item(i.barcode, i.name, 0, i.description, 1, i.id))
        }
        return it
    }

    fun fetchItems(
        itemCode: String, accessToken: String, instance: NetworkOp,
    ) {
        _state.value = SearchState.SEARCHING
        val reqURL = itemsURL + itemCode
        instance.addToRequestQueue(
            AuthorizedRequest(
                reqURL,
                { response ->
                    Log.d("NETWORKING", response.toString())
                    val responseObj = Json.decodeFromString<BrowseResponse>(response.toString())
                    _itemList.value =
                        getItems(responseObj)
                    _state.value =
                        if (itemList.value?.size == 0) SearchState.NO_ITEMS else SearchState.FOUND_ITEMS
                    sessionToken = responseObj.token
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

    fun addItemToRemote(item: Item, token: String, instance: NetworkOp) {
        val json = JSONObject()
            .put("token", sessionToken)
            .put("name", item.name)
            .put("description", item.description)
            .put("barcode", item.code)
            .put("test", true)
        val jsonObjectRequest = AuthorizedRequest(
            addItemURL,
            { response ->
                Log.d("NETWORKING", "added item $item, received response $response")
            },
            { error ->
                Log.d("NETWORKING", "something went wrong, received error $error")
            },
            token,
            json
        )
        instance.addToRequestQueue(jsonObjectRequest)
    }

    fun rateProduct(accessToken: String, instance: NetworkOp) {
        val json = JSONObject()
            .put("token", sessionToken)
            .put("rating", 1)
            .put("productId", selected.value?.id)
        instance.addToRequestQueue(
            AuthorizedRequest(
                rateItemURL,
                { response ->
                    Log.d("NETWORKING", "rated item ${selected.value}, received $response")
                },
                { error ->
                    Log.d("NETWORKING", "something went wrong while rating, received error $error")
                },
                accessToken,
                json
            )
        )
    }

    class AuthorizedRequest(
        url: String,
        response: Response.Listener<JSONObject>,
        error: Response.ErrorListener,
        private val accessToken: String,
        jsonObject: JSONObject? = null
    ) : JsonObjectRequest(url, jsonObject, response, error) {
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