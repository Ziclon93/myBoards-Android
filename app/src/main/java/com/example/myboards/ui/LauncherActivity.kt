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

    override fun onResume() {
            val fragmentId: Int =
                findNavController(R.id.nav_host_fragment).currentDestination?.id!!
            if (fragmentId == R.id.launchingFragment) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_launchingFragment_to_loginFragment)
            } else if (fragmentId == R.id.registerFragment) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_registerFragment_to_loginFragment)
            }
        super.onResume()
    }

    override fun onBackPressed() {
        val fragmentId: Int =
            findNavController(R.id.nav_host_fragment).currentDestination?.id!!
        if (fragmentId == R.id.registerFragment) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_registerFragment_to_loginFragment)
        } else {
            super.onBackPressed()
        }
    }
}
