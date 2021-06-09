package com.example.pantryfin.ui.addItem

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.pantryfin.R
import com.example.pantryfin.data.items.Item
import com.example.pantryfin.databinding.ActivityNewItemBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewItemBinding
    private lateinit var model: NewItemViewModel
    private lateinit var barcode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewItemBinding.inflate(layoutInflater)

        barcode = intent.getStringExtra(NEW_ITEM_CODE_KEY) ?: ""
        val codeView = binding.codeView
        codeView.text = getString(R.string.barcode,barcode)


        model = NewItemViewModel()

        binding.lifecycleOwner = this

        binding.model = model
        setContentView(binding.root)

        model.newItemFormState.observe(this, Observer {
            val state = it ?: return@Observer
            binding.confirmButton.isEnabled = state.isDataValid

            if (state.nameError != null) binding.itemNameField.error = getString(state.nameError)
            if (state.descriptionError != null) binding.itemDescriptionField.error =
                getString(state.descriptionError)
            if (state.amountError != null) binding.itemAmountField.error =
                getString(state.amountError)
        })
        binding.confirmButton.setOnClickListener {
            onComplete()
        }
    }

    fun onComplete() {
        val item = Item(
            barcode,
            model.name.value.toString(),
            0,
            model.description.value.toString(),
            Integer.parseInt(model.amount.value.toString()),
            model.type.value.toString()
        )
        val backIntent = Intent()
        backIntent.putExtra(NEW_ITEM_REPLY, Json.encodeToString(item))
        setResult(Activity.RESULT_OK, backIntent)
        finish()
    }

    companion object {
        const val NEW_ITEM_REPLY = "com.example.android.newitem.REPLY"
        const val NEW_ITEM_CODE_KEY = "com.example.android.newitem.code"
    }

}