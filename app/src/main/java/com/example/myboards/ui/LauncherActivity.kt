package com.example.myboards.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.myboards.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
    }

    fun toMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }


    fun toLogin() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_launchingFragment_to_loginFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        toLogin()
        super.onActivityResult(requestCode, resultCode, data)
    }
}
