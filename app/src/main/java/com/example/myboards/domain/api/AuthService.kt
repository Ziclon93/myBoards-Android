package com.example.myboards.domain.api

import com.example.myboards.data.model.auth.AuthCredentials

interface AuthService {
    fun setAuthCredentials(credentials: AuthCredentials)
    fun getAuthCredentials(): AuthCredentials
    fun loadLocalAuthCredentials()
    fun clearAuthCredentials()
    fun getLocalValidAuth(): Boolean
}
