package com.example.pantryfin.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null
)

data class RegisterResult(
    val success: RegisteredUser? = null,
    val error: Int? = null
)

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String
)

data class RegisteredUser(
    val id: String,
    val username: String,
    val email: String,
)