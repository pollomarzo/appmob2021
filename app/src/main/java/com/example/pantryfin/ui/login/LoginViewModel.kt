package com.example.pantryfin.ui.login

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.pantryfin.data.LoginRepository
import com.example.pantryfin.data.Result

import com.example.pantryfin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
                    putString(tokenKey, result.data.token)
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
//
///**
// * ViewModel provider factory to instantiate LoginViewModel.
// * Required given LoginViewModel has a non-empty constructor
// */
//class LoginViewModelFactory : ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
//            return LoginViewModel(
//                loginRepository = LoginRepository(
//                    dataSource = LoginDataSource()
//                )
//            ) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}