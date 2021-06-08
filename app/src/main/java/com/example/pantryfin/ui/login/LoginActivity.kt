package com.example.pantryfin.ui.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.pantryfin.R
import com.example.pantryfin.data.LoginDataSource
import com.example.pantryfin.data.LoginRepository
import com.example.pantryfin.databinding.ActivityLoginBinding
import kotlinx.coroutines.MainScope


class LoginActivity : AppCompatActivity() {

    // this is where we'll run our coroutines. should we pass this to the view model
    // or make them suspend until here? i think the first is prettier
    private val scope = MainScope()
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.email
        val username = binding.username
        val password = binding.password

        val signIn = binding.signIn
        val loading = binding.loading

        loginViewModel = LoginViewModel(
            loginRepository = LoginRepository(
                dataSource = LoginDataSource(context = this.applicationContext)
            ),
            coroutineScope = scope,
            sharedPref = getSharedPreferences(
                getString(R.string.auth_data_file),
                Context.MODE_PRIVATE
            ),
            tokenKey = getString(R.string.access_token_key),
        )
        binding.loginVM = loginViewModel

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            signIn.isEnabled = loginState.isDataValid
            Log.d("OBSERVER", "")

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
            if (loginState.emailError != null) {
                email.error = getString(loginState.emailError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
                setResult(Activity.RESULT_OK)
                finish()
            }


            //Complete and destroy login activity once successful
        })
        loginViewModel.registerResult.observe(this@LoginActivity, Observer {
            val registerResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (registerResult.error != null) {
                showFailed(registerResult.error)
            }
            if (registerResult.success != null) {
                successfulRegister(registerResult.success)
            }
            // set extra with user? put it into local storage?
            setResult(Activity.RESULT_OK)
        })

        password.apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        if (loginViewModel.registering) {
                            loginViewModel.register()
                        } else
                            loginViewModel.login()
                }
                false
            }

            signIn.setOnClickListener {
                loading.visibility = View.VISIBLE
                if (loginViewModel.registering) {
                    Log.d("LOGIN", "because registering is true, i shall call registerandlogin()")
                    loginViewModel.register()
                } else
                    loginViewModel.login()
            }
        }
    }


    // i decided to go with this. I understand other people may prefer two different fragments,
    // but I'm not convinced. So i picked what felt like the easier solution
    fun onSwitchToRegister(buttonView: View) {
        // update ViewModel
        loginViewModel.registering = true
        Log.d("VIEWMODEL", loginViewModel.emailText.value.toString())

        // change register button
        (buttonView as Button).text = resources.getString(R.string.switch_to_login)

        // make sign in button register
        findViewById<Button>(R.id.sign_in).text = resources.getString(R.string.action_register)

        // show email field
        findViewById<EditText>(R.id.username).visibility = View.VISIBLE

        buttonView.setOnClickListener {
            onSwitchToLogin(it)
        }
    }

    fun onSwitchToLogin(buttonView: View) {
        // update ViewModel
        loginViewModel.registering = false

        // change register button
        (buttonView as Button).text = resources.getString(R.string.switch_to_register)

        // make sign in button register
        findViewById<Button>(R.id.sign_in).text = resources.getString(R.string.action_sign_in)

        // show email field
        findViewById<EditText>(R.id.username).visibility = View.INVISIBLE

        buttonView.setOnClickListener {
            onSwitchToRegister(it)
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun successfulRegister(user: RegisteredUser) {
        onSwitchToLogin(findViewById<Button>(R.id.register))
        Toast.makeText(
            applicationContext,
            "Thank you for registering! Go ahead and login using your email ${user.email}",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showFailed(@StringRes errorString: Int) {
        Toast.makeText(
            applicationContext,
            "${getString(errorString)}",
            Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}