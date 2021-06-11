package com.uni.pantry.ui.login

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.uni.pantry.data.LoginRepository
import com.uni.pantry.data.Result

import com.uni.pantry.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val coroutineScope: CoroutineScope,
    private val sharedPref: android.content.SharedPreferences,
    private val tokenKey: String,
) : ViewModel() {

    var registering = false
    private var _usernameText = MutableLiveData<String?>()
    var usernameText: LiveData<String?> = _usernameText

    private var _emailText = MutableLiveData<String?>()
    var emailText: LiveData<String?> = _emailText

    private var _passwordText = MutableLiveData<String?>()
    var passwordText: LiveData<String?> = _passwordText

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun setUsernameText(s: Editable) {
        _usernameText.value = s.toString()
        loginDataChanged()
    }

    fun setEmailText(s: Editable) {
        _emailText.value = s.toString()
        loginDataChanged()
    }

    fun setPasswordText(s: Editable) {
        _passwordText.value = s.toString()
        loginDataChanged()
    }

    fun login() {
        // can be launched in a separate asynchronous job
        coroutineScope.launch {
            val result = loginRepository.login(
                emailText.value.toString(),
                passwordText.value.toString()
            )
            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))

                with (sharedPref.edit()) {
                    putString(
                        tokenKey,
                        Json.encodeToString(LoginInfo(
                            result.data.displayName,
                            result.data.token,
                            getDate())))
                    apply()
                }
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun register() {
        coroutineScope.launch {
            val result = loginRepository.register(
                usernameText.value.toString(),
                emailText.value.toString(),
                passwordText.value.toString()
            )

            if (result is Result.Success) {
                _registerResult.value = RegisterResult(
                    success = RegisteredUser(
                        result.data.id,
                        result.data.username,
                        result.data.email
                    )
                )
            } else {
                _registerResult.value = RegisterResult(error = R.string.register_failed)
            }
        }
    }

    fun loginDataChanged() {
        if (!isUserNameValid(usernameText.value.toString())) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(passwordText.value.toString())) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isEmailValid(emailText.value.toString())) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A simple username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A simple password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isEmailValid(email: String): Boolean {
        return !registering || Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

@Serializable
data class LoginInfo(
    val email: String,
    val token: String,
    val dateLogged: String,
)

fun getDate():String {
    // screw local formatting <.<
    val sdf = SimpleDateFormat("yyyyMMdd")
    val date = Calendar.getInstance().time
    return sdf.format(date)
}