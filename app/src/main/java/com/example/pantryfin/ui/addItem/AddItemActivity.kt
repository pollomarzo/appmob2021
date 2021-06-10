package com.example.pantryfin.ui.addItem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
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
import kotlinx.coroutines.MainScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val itemsURL = "https://lam21.modron.network/products?barcode="

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
            val sharedPref =
                getSharedPreferences(getString(R.string.auth_data_file), Context.MODE_PRIVATE)
            val encodedInfo = sharedPref.getString(getString(R.string.access_token_key), null)
            if (encodedInfo != null) {
                val accessToken = Json.decodeFromString<LoginInfo>(encodedInfo).token
                model.fetchItems(
                    // i know this isn't empty or the button would be disabled
                    model.code.value.toString(),
                    accessToken,
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
        // maybe there's a better way to handle this
        Log.d("SELECTED", "selected is ${model.selected.value}")
        if (model.selected.value == null) {
            // create intent to make new item
            val intent = Intent(this, NewItemActivity::class.java)
            intent.putExtra(NewItemActivity.NEW_ITEM_CODE_KEY, model.code.value.toString())
            startActivityForResult(intent, newItemRequestCode)
        } else {
            // confirm choice and go back to main activity
            returnToMain(model.selected.value!!)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newItemRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewItemActivity.NEW_ITEM_REPLY)?.let {
                val item = Json.decodeFromString<Item>(it)
                returnToMain(item)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun returnToMain(item: Item) {
        val backIntent = Intent()
        backIntent.putExtra(EXTRA_REPLY, Json.encodeToString(item))
        setResult(Activity.RESULT_OK, backIntent)
        Log.d("ADD_ITEM", "adding item $item and returning to main")
        finish()
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.itemlistsql.REPLY"
    }
}


//class AddItem : AppCompatActivity() {
//    // delegate (vieModels) and delay init (by)
//    private val addItemViewModel: AddItemViewModel by viewModels {
//        ItemViewModelFactory((application as ItemsApplication).repository)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_add_item)
//
//        val code = findViewById<EditText>(R.id.code)
//        val name = findViewById<EditText>(R.id.name)
//        val addItemButton = findViewById<Button>(R.id.addItem)
//        val loading = findViewById<ProgressBar>(R.id.loading)
//
//
//        addItemViewModel.itemFormState.observe(this@AddItem, Observer {
//            val loginState = it ?: return@Observer
//
//            // disable login button unless both username / password is valid
//            addItemButton.isEnabled = loginState.isDataValid
//
//            if (loginState.usernameError != null) {
//                code.error = getString(loginState.usernameError)
//            }
//            if (loginState.passwordError != null) {
//                name.error = getString(loginState.passwordError)
//            }
//        })
//
//        addItemViewModel.itemResult.observe(this@AddItem, Observer {
//            val loginResult = it ?: return@Observer
//
//            loading.visibility = View.GONE
//            if (loginResult.error != null) {
//                showLoginFailed(loginResult.error)
//            }
//            if (loginResult.success != null) {
//                updateUiWithUser(loginResult.success)
//            }
//            setResult(Activity.RESULT_OK)
//
//            //Complete and destroy addItem activity once successful
//            finish()
//        })
//
//        code.afterTextChanged {
//            addItemViewModel.itemDataChanged(
//                    code.text.toString(),
//                    name.text.toString()
//            )
//        }
//
//        name.apply {
//            afterTextChanged {
//                addItemViewModel.itemDataChanged(
//                        code.text.toString(),
//                        name.text.toString()
//                )
//            }
//
//            setOnEditorActionListener { _, actionId, _ ->
//                when (actionId) {
//                    EditorInfo.IME_ACTION_DONE ->
//                        addItemViewModel.addItem(
//                                code.text.toString(),
//                                name.text.toString()
//                        )
//                }
//                false
//            }
//
//            addItemButton.setOnClickListener {
//                loading.visibility = View.VISIBLE
//                addItemViewModel.run { addItem(code.text.toString(), name.text.toString()) }
//            }
//        }
//    }
//
//    private fun updateUiWithUser(model: AddedItemView) {
//        val welcome = getString(R.string.welcome)
//        val displayName = model.displayName
//        // TODO : initiate successful logged in experience
//        Toast.makeText(
//                applicationContext,
//                "$welcome $displayName",
//                Toast.LENGTH_LONG
//        ).show()
//    }
//
//    private fun showLoginFailed(@StringRes errorString: Int) {
//        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
//    }
//}
//
///**
// * Extension function to simplify setting an afterTextChanged action to EditText components.
// */
//fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
//    this.addTextChangedListener(object : TextWatcher {
//        override fun afterTextChanged(editable: Editable?) {
//            afterTextChanged.invoke(editable.toString())
//        }
//
//        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//    })
//}