package com.example.myboards.data

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


class FireBaseStorageServiceImpl {


    fun updateImageAnonymously(
        dataBitmap: Bitmap,
        imagePath: String,
        eventFunction: (Boolean) -> (Unit)
    ) {

        launchAnonymously {
            if (it) {
                val storageRef = FirebaseStorage.getInstance().reference
                val ref = storageRef.child(imagePath)

                val baos = ByteArrayOutputStream()
                dataBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val imageData = baos.toByteArray()
                val uploadTask = ref.putBytes(imageData)

                uploadTask.addOnFailureListener {
                    Log.e("TAG", it.message.toString())
                }
                eventFunction(true)
            } else {
                eventFunction(false)
            }
        }
    }

    fun runWhenFileExist(path: String, function: () -> (Unit)) {
        val storageRef = FirebaseStorage.getInstance().reference
        storageRef.child(path).downloadUrl.addOnSuccessListener {
            function()
        }.addOnFailureListener {
            runWhenFileExist(path, function)
        }
    }

    fun launchAnonymously(function: (Boolean) -> (Unit)) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInAnonymously().addOnCompleteListener {
            function(true)
        }.addOnFailureListener {
            function(false)
        }

    }

    fun generatePath(): String = "images/" + UUID.randomUUID().toString()
}