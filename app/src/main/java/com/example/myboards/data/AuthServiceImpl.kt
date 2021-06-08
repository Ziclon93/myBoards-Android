package com.example.myboards.data

import android.content.SharedPreferences
import com.example.myboards.data.model.auth.AuthCredentials
import com.example.myboards.domain.api.ApiService
import com.example.myboards.domain.api.AuthService
import com.google.firebase.auth.EmailAuthProvider


/**
 * Implementation of [AuthService] that uses retrofit to perform api calls
 */
class AuthServiceImpl(
    private val sharedPreferences: SharedPreferences
) : AuthService {

    private var authCredentials: AuthCredentials = AuthCredentials("", "", "")

    override fun setAuthCredentials(credentials: AuthCredentials) {
        val editor = sharedPreferences.edit()
        editor.putString(Config.KEY_USER, credentials.userKey)
        editor.putString(Config.KEY_USERNAME, credentials.username)
        editor.putString(Config.KEY_PASSWORD, credentials.password)
        editor.apply()

        authCredentials = credentials
    }

    override fun getAuthCredentials(): AuthCredentials = this.authCredentials


    override fun loadLocalAuthCredentials() {
        val userKey = sharedPreferences.getString(Config.KEY_USER, "")
        val username = sharedPreferences.getString(Config.KEY_USERNAME, "")
        val password = sharedPreferences.getString(Config.KEY_PASSWORD, "")

        return if (userKey.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            setAuthCredentials(
                AuthCredentials(
                    "",
                    "",
                    ""
                )
            )

        } else {
            setAuthCredentials(
                AuthCredentials(
                    userKey,
                    username,
                    password
                )
            )
        }
    }

    override fun clearAuthCredentials() {
        val editor = sharedPreferences.edit();
        editor.putString(Config.KEY_USER, "")
        editor.putString(Config.KEY_USERNAME, "")
        editor.putString(Config.KEY_PASSWORD, "")
        editor.apply()

        setAuthCredentials(
            AuthCredentials(
                "",
                "",
                ""
            )
        )
    }

    override fun getLocalValidAuth(): Boolean {
        loadLocalAuthCredentials()
        return authCredentials.userKey.isNotEmpty()
    }


    private object Config {
        const val NAMESPACE = "auth.repo"
        const val KEY_USER = "${NAMESPACE}.userId"
        const val KEY_USERNAME = "${NAMESPACE}.username"
        const val KEY_PASSWORD = "${NAMESPACE}.password"
    }

}