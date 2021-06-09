package com.example.pantryfin.ui.addItem

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pantryfin.R

class NewItemViewModel : ViewModel() {

    private var _name = MutableLiveData<String>("")
    var name: LiveData<String> = _name

    private var _description = MutableLiveData<String>("")
    var description: LiveData<String> = _description

    private var _amount = MutableLiveData<String>("")
    var amount: LiveData<String> = _amount

    private var _type = MutableLiveData<String>("")
    var type: LiveData<String> = _type

    private var _newItemFormState = MutableLiveData<NewItemFormState>()
    var newItemFormState: LiveData<NewItemFormState> = _newItemFormState

    fun setName(s: Editable) {
        _name.value = s.toString()
        validateForm()
    }

    fun setDescription(s: Editable) {
        _description.value = s.toString()
        validateForm()
    }

    fun setAmount(s: Editable) {
        _amount.value = s.toString()
        validateForm()
    }

    fun setType(s: Editable) {
        _type.value = s.toString()
        validateForm()
    }


    private fun validateForm() {
        if (!isNameValid(name.value.toString())) _newItemFormState.value =
            NewItemFormState(nameError = R.string.name_too_short)
        else if (!amount.value?.let { isAmountValid(it) }!!) _newItemFormState.value =
            NewItemFormState(amountError = R.string.low_amount)
        else _newItemFormState.value = NewItemFormState(isDataValid = true)

    }

    private fun isNameValid(name: String): Boolean {
        return name.length > 3
    }

    private fun isDescriptionValid(desc: String): Boolean {
        // can't really think of any problem
        return true
    }

    private fun isAmountValid(amount: String): Boolean {
        // found out about toIntOrNull too late. handling it weird now.
        val parsed = amount.toIntOrNull() ?: return false
        return amount == "" || parsed > 0
    }


}

data class NewItemFormState(
    val nameError: Int? = null,
    val descriptionError: Int? = null,
    val amountError: Int? = null,
    val isDataValid: Boolean = false,
)
