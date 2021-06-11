package com.example.myboards.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.myboards.R
import com.example.myboards.data.FireBaseStorageServiceImpl
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Image
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.Event
import com.example.myboards.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseStorageServiceImpl: FireBaseStorageServiceImpl

    @Inject
    lateinit var glideServiceImpl: GlideServiceImpl

    private val REQUEST_CODE = 100

    var currentImageUpdated = MutableLiveData<Event<DelayedResult<Image>>>()

    private lateinit var bottomBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionCheck = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        initActivityPermissions()
        setBottomBarNavigation()
    }

    private fun setBottomBarNavigation() {
        bottomBar = findViewById(R.id.bottomNavigationView)
        bottomBar.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.explore -> {
                    when (bottomBar.selectedItemId) {
                        R.id.search -> {
                            navigatorAction(R.id.action_searchFragment_to_exploreFragment)
                            true
                        }
                        R.id.followed -> {
                            navigatorAction(R.id.action_followedFragment_to_exploreFragment)
                            true
                        }
                        R.id.profile -> {
                            navigatorAction(R.id.action_profileFragment_to_exploreFragment)
                            true
                        }
                        else -> false
                    }
                }

                R.id.search -> {
                    when (bottomBar.selectedItemId) {
                        R.id.explore -> {
                            if (findNavController(R.id.main_nav_host_fragment).currentDestination?.id!! == R.id.boardDetailFragment) {
                                navigatorAction(R.id.action_boardDetailFragment_to_searchFragment)
                            } else {
                                navigatorAction(R.id.action_exploreFragment_to_searchFragment)
                            }
                            true
                        }
                        R.id.followed -> {
                            navigatorAction(R.id.action_followedFragment_to_searchFragment)
                            true
                        }
                        R.id.profile -> {
                            navigatorAction(R.id.action_profileFragment_to_searchFragment)
                            true
                        }
                        else -> false
                    }
                }

                R.id.followed -> {
                    when (bottomBar.selectedItemId) {
                        R.id.explore -> {
                            if (findNavController(R.id.main_nav_host_fragment).currentDestination?.id!! == R.id.boardDetailFragment) {
                                navigatorAction(R.id.action_boardDetailFragment_to_followedFragment)
                            } else {
                                navigatorAction(R.id.action_exploreFragment_to_followedFragment)
                            }
                            true
                        }
                        R.id.search -> {
                            navigatorAction(R.id.action_searchFragment_to_followedFragment)
                            true
                        }
                        R.id.profile -> {
                            navigatorAction(R.id.action_profileFragment_to_followedFragment)
                            true
                        }
                        else -> false
                    }
                }

                R.id.profile -> {
                    when (bottomBar.selectedItemId) {
                        R.id.explore -> {

                            if (findNavController(R.id.main_nav_host_fragment).currentDestination?.id!! == R.id.boardDetailFragment) {
                                navigatorAction(R.id.action_boardDetailFragment_to_profileFragment)
                            } else {
                                navigatorAction(R.id.action_exploreFragment_to_profileFragment)
                            }
                            true
                        }
                        R.id.search -> {
                            navigatorAction(R.id.action_searchFragment_to_profileFragment)
                            true
                        }
                        R.id.followed -> {
                            navigatorAction(R.id.action_followedFragment_to_profileFragment)
                            true
                        }
                        else -> false
                    }
                }
                else -> false
            }

        }
    }

    private fun navigatorAction(actionId: Int) =
        findNavController(R.id.main_nav_host_fragment).navigate(actionId)

    fun navigatorAction(actionId: NavDirections) =
        findNavController(R.id.main_nav_host_fragment).navigate(actionId)

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            MainScope().launch {
                val imagePath = firebaseStorageServiceImpl.generatePath();
                val bitmap = glideServiceImpl.toBitmap(contentResolver, data?.data!!)

                currentImageUpdated.value = Event(DelayedResult.of(Image(imagePath, bitmap)))
            }
        }
    }

    private fun initActivityPermissions() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                225
            )
        }
    }

    override fun onBackPressed() {
        val fragmentId: Int =
            findNavController(R.id.main_nav_host_fragment).currentDestination?.id!!

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment)?.childFragmentManager?.fragments?.get(
                0
            )


        if (fragmentId == R.id.profileFragment || fragmentId == R.id.exploreFragment || fragmentId == R.id.searchFragment || fragmentId == R.id.followedFragment) {
            moveTaskToBack(true)
        } else {
            super.onBackPressed()
        }
    }
}
