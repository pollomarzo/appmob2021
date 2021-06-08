package com.example.pantryfin.ui.addItem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.pantryfin.R
import com.example.pantryfin.data.NetworkOp
import com.example.pantryfin.ui.browseItems.BrowseItemsActivity
import kotlinx.coroutines.MainScope
import org.json.JSONObject

const val itemsURL = "https://lam21.modron.network/products?code="

class AddItemActivity : AppCompatActivity() {

    private val scope = MainScope()
    private lateinit var editItemCodeView: EditText

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        editItemCodeView = findViewById(R.id.edit_item_code)

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editItemCodeView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                // simple for now. may move to ViewModel
               /* val returned = Item(
                    "0000",
                    editItemCodeView.text.toString(),
                    0,
                    "",
                    1,
                    "meat"
                )
                replyIntent.putExtra(EXTRA_REPLY, Json.encodeToString(returned))
                setResult(Activity.RESULT_OK, replyIntent)*/
                browseResults()
            }
        }
    }

    private fun browseResults() {
        val intent = Intent(this.applicationContext, BrowseItemsActivity::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, editItemCodeView.text.toString())
        startActivity(intent)
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