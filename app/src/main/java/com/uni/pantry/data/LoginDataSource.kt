package com.uni.pantry.data

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.uni.pantry.data.model.LoggedInUser
import com.uni.pantry.ui.login.LoginResponse
import com.uni.pantry.ui.login.RegisterResponse
import com.uni.pantry.ui.login.RegisteredUser
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val registerURL = "https://lam21.modron.network/users"
const val loginURL = "https://lam21.modron.network/auth/login"

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 * Needs context for network queue handling. has to be application context!
 */
class LoginDataSource(context: Context) {
    private val appContext: Context = context

    // isn't this mega pretty? now i can run this synchronously! how cool is that??
    suspend fun login(email: String, password: String) =
        suspendCoroutine<Result<LoggedInUser>> { cont ->
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, loginURL, jsonObj(null, email, password),
                { response ->
                    Log.d("NETWORKING", response.toString())
                    val res = Json.decodeFromString<LoginResponse>(response.toString())
                    cont.resume(
                        Result.Success(
                            LoggedInUser(
                                token = res.accessToken,
                                displayName = email
                            ),
                        )
                    )
                },
                { error ->
                    Log.d("NETWORKING", error.toString())
                    cont.resume(Result.Error(IOException("Error logging in")))
                }
            )

            NetworkOp.getInstance(appContext).addToRequestQueue(jsonObjectRequest)
        }

    fun logout() {
        // i don't think we can logout "for real"
    }

    suspend fun register(username: String, email: String, password: String) =
        suspendCoroutine<Result<RegisteredUser>> { cont ->
            val requestObj = jsonObj(username, email, password)
            Log.d("NETWORKING", requestObj.toString())
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, registerURL, jsonObj(username, email, password),
                { response ->
                    Log.d("NETWORKING", response.toString())
                    val res = Json.decodeFromString<RegisterResponse>(response.toString())
                    cont.resume(
                        Result.Success(
                            RegisteredUser(
                                res.id,
                                res.username,
                                res.email,
                            ),
                        )
                    )
                },
                { error ->
                    Log.d("NETWORKING", error.toString())
                    cont.resume(Result.Error(IOException("Error logging in")))
                }
            )

            NetworkOp.getInstance(appContext).addToRequestQueue(jsonObjectRequest)

        }

    private fun jsonObj(username: String?, email: String, password: String): JSONObject {
        val obj = JSONObject()
        if (username != null && username != "") obj.put("username", username)
        if (email != "") obj.put("email", email)
        if (password != "") obj.put("password", password)
        return obj
    }
}
