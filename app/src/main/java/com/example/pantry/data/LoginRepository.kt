package com.example.pantry.data

import com.example.pantry.data.model.LoggedInUser
import com.example.pantry.ui.login.RegisteredUser

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object.. even though i never use it
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun login(email: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(email, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    suspend fun register(username: String, email: String, password: String): Result<RegisteredUser> {
        val result = dataSource.register(username, email, password)
        if (result is Result.Success) login(email, password)
        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}