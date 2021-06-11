package com.example.pantryfin.ui.addItem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.example.pantryfin.R
import com.example.pantryfin.data.NetworkOp
import com.example.pantryfin.data.items.Item
import com.example.pantryfin.databinding.ActivityAddItemBinding
import com.example.pantryfin.ui.browseItems.BrowseItemsFragment
import com.example.pantryfin.ui.browseItems.AddItemViewModel
import com.example.pantryfin.ui.login.LoginInfo
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val itemsURL = "https://lam21.modron.network/products?barcode="
const val addItemURL = "https://lam21.modron.network/products"

class AddItemActivity : AppCompatActivity() {
    private val newItemRequestCode: Int = 2
    private val model: AddItemViewModel by viewModels()
    private lateinit var binding: ActivityAddItemBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        // this next line stole one whole ass hour from my day
        // so much for compile time safety...
        binding.lifecycleOwner = this

        val toolbar: Toolbar = binding.addItemToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        setContentView(binding.root)
        binding.browseVM = model

        model.state.observe(this@AddItemActivity, {
            model.updateInfoText(
                getString(R.string.search_init),
                getString(R.string.search_start),
                getString(R.string.search_empty_response),
                getString(R.string.search_real_response),
                getString(R.string.search_error)
            )
        })

        // XML declarative wasn't cooperating so it's here.
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<BrowseItemsFragment>(R.id.fragment_container_view)
        }


        val button = binding.buttonSave
        button.setOnClickListener {
            val token = getAccessToken()
            if (token != "") {
                model.fetchItems(
                    // i know this isn't empty or the button would be disabled
                    model.code.value.toString(),
                    token,
                    NetworkOp.getInstance(applicationContext)
                )
            } else {
                val replyIntent = Intent()
                setResult(Activity.RESULT_CANCELED, replyIntent)
                Toast.makeText(
                    this,
                    getString(R.string.not_logged_in),
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
        model.selected.observe(this, Observer {
            invalidateOptionsMenu()
        })
        model.code.observe(this, {
            invalidateOptionsMenu()
        })
        model.isCodeValid.observe(this, {
            if (!it!!) {
                binding.editItemCode.error = getString(R.string.bad_barcode)
                button.isEnabled = false
            } else button.isEnabled = true
        })

        val cameraButton = binding.cameraButton
        cameraButton.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this@AddItemActivity)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()
        }
    }

    // inflate icon (s?) and add them to toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_item_toolbar_menu, menu)
        if (model.selected.value == null) {
            menu?.findItem(R.id.toolbar_menu_item_new)?.title =
                getString(R.string.add_item_button_toolbar)
            menu?.findItem(R.id.toolbar_menu_item_new)?.isEnabled =
                model.code.value.toString().isNotEmpty()
        } else menu?.findItem(R.id.toolbar_menu_item_new)?.title =
            getString(R.string.confirm_item_button_toolbar)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home && model.selected.value == null) {
            // create intent to make new item
            val intent = Intent(this, NewItemActivity::class.java)
            intent.putExtra(NewItemActivity.NEW_ITEM_CODE_KEY, model.code.value.toString())
            startActivityForResult(intent, newItemRequestCode)
        } else if (item.itemId != android.R.id.home && model.selected.value != null) {
            // confirm choice, notify server and go back to main activity
            model.rateProduct(getAccessToken(), NetworkOp.getInstance(applicationContext))
            returnToMain(model.selected.value!!.copy(type = getString(R.string.default_type)))
        } else {
            // we're not adding anything
            returnToMain(null)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val cameraResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (cameraResult != null){
            if (cameraResult.contents == null) {
                Toast.makeText(
                    this,
                    "cancelled",
                    Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this,
                    "Scanned -> " + cameraResult.contents,
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
        else if (requestCode == newItemRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewItemActivity.NEW_ITEM_REPLY)?.let {
                val item = Json.decodeFromString<Item>(it)
                returnToMain(item, true)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun returnToMain(item: Item?, newItem: Boolean = false) {
        val backIntent = Intent()
        if (item != null) {
            val token = getAccessToken()
            if (newItem && model.sessionToken != "")
                model.addItemToRemote(
                    item,
                    token,
                    NetworkOp.getInstance(applicationContext)
                )
            backIntent.putExtra(ADDED_ITEM_EXTRA_REPLY, Json.encodeToString(item))
            setResult(Activity.RESULT_OK, backIntent)
        } else setResult(Activity.RESULT_CANCELED, backIntent)
        Log.d("ADD_ITEM", "adding item $item and returning to main")
        finish()
    }

    companion object {
        const val ADDED_ITEM_EXTRA_REPLY = "com.example.android.itemlistsql.REPLY"
    }

    private fun getAccessToken(): String {
        val sharedPref =
            getSharedPreferences(getString(R.string.auth_data_file), Context.MODE_PRIVATE)
        val encodedInfo = sharedPref.getString(getString(R.string.access_token_key), null)
        if (encodedInfo != null) return Json.decodeFromString<LoginInfo>(encodedInfo).token
        return ""
    }
}
