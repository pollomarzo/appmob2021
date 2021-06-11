package com.uni.pantry.ui.addItem

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import com.uni.pantry.R
import com.uni.pantry.data.items.Item
import com.uni.pantry.databinding.ActivityNewItemBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NewItemActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityNewItemBinding
    private lateinit var model: NewItemViewModel
    private lateinit var barcode: String
    private var defaultBarcode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewItemBinding.inflate(layoutInflater)

        val toolbar: Toolbar = binding.newItemToolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        model = NewItemViewModel()

        binding.lifecycleOwner = this

        binding.model = model
        setContentView(binding.root)

        val spinner = binding.itemTypeField
        ArrayAdapter.createFromResource(
            this,
            R.array.types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this


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
        val prevValue = intent.getStringExtra(EDITED_ITEM_KEY)

        if (prevValue != null){
            Log.d("PRESS", "found edit")
            val item = Json.decodeFromString<Item>(prevValue)
            // just convert them to any editable and we're good
            val name = SpannableStringBuilder(item.name)
            val description = SpannableStringBuilder(item.description)
            val amount = SpannableStringBuilder(item.amount.toString())
            val type = item.type

            model.setName(name)
            binding.itemNameField.text = name

            model.setDescription(SpannableStringBuilder(description))
            binding.itemDescriptionField.text = description

            model.setAmount(SpannableStringBuilder(item.amount.toString()))
            binding.itemAmountField.text = amount

            model.setType(type)
            val index = resources.getStringArray(R.array.types_array).indexOf(type)
            spinner.setSelection(index)

            // save this for updating DB
            model.prevID = item.id
            Log.d("PRESS", "changing defaultBarcode to ${item.code}")
            defaultBarcode = item.code
        }
        val codeView = binding.codeView
        barcode = intent.getStringExtra(NEW_ITEM_CODE_KEY) ?: defaultBarcode
        codeView.text = getString(R.string.barcode,barcode)

    }

    private fun onComplete() {
        val item = Item(
            barcode,
            model.name.value.toString(),
            model.prevID,
            model.description.value.toString(),
            Integer.parseInt(model.amount.value.toString()),
            model.type.value.toString()
        )
        val backIntent = Intent()
        backIntent.putExtra(NEW_ITEM_REPLY, Json.encodeToString(item))
        setResult(Activity.RESULT_OK, backIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            val backIntent = Intent()
            setResult(Activity.RESULT_CANCELED, backIntent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    // spinner functions
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        model.setType(parent.getItemAtPosition(pos).toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        model.setType(parent.getItemAtPosition(0).toString())
    }


    companion object {
        const val NEW_ITEM_REPLY = "com.example.android.newitem.REPLY"
        const val NEW_ITEM_CODE_KEY = "com.example.android.newitem.code"
        const val EDITED_ITEM_KEY = "com.example.android.newitem.edited"
    }

}