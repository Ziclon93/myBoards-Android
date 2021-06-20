package com.example.myboards.data

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.signature.MediaStoreSignature
import com.bumptech.glide.signature.ObjectKey
import com.example.myboards.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import java.io.ByteArrayOutputStream
import java.lang.Exception


class GlideServiceImpl(
    private val context: Context,
    private val fireBaseStorageServiceImpl: FireBaseStorageServiceImpl
) {

    fun showFromFireBaseUri(uri: String, imageView: ImageView) {

        fireBaseStorageServiceImpl.launchAnonymously {
            if (it) {
                val storageRef = FirebaseStorage.getInstance()
                val imageRef = storageRef.getReference(uri)

                Glide.with(context)
                    .load(imageRef)
                    .transform(CircleCrop())
                    .into(imageView)
            }
        }
    }

    fun showSquareFromFireBaseUri(uri: String, imageView: ImageView) {

        fireBaseStorageServiceImpl.launchAnonymously {
            if (it) {
                val storageRef = FirebaseStorage.getInstance()
                val imageRef = storageRef.getReference(uri)


                Glide.with(context)
                    .load(imageRef)
                    .error(showSquareFromFireBaseUri(uri, imageView))
                    .placeholder(R.drawable.ic_default_board_icon)
                    .transform(RoundedCorners(20))
                    .into(imageView)
            }

        }
    }

    fun showSquareFromFireBaseUriCenterCrop(uri: String, imageView: ImageView) {

        fireBaseStorageServiceImpl.launchAnonymously { success ->
            if (success) {
                val storageRef = FirebaseStorage.getInstance()
                storageRef.getReference(uri).downloadUrl.addOnSuccessListener {
                    Glide.with(context)
                        .load(it)
                        .placeholder(R.drawable.ic_default_board_icon)
                        .transform(MultiTransformation(CenterCrop(), RoundedCorners(20)))
                        .into(imageView)
                }.addOnFailureListener {
                    Log.e("Glide", it.message!!)
                    showSquareFromFireBaseUriCenterCrop(uri, imageView)
                }
            }

        }
    }

    fun clearImage(imageView: ImageView) {
        Glide.with(context).clear(imageView)
    }


    fun showFromBitmapCircleCrop(bitmap: Bitmap, imageView: ImageView) {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        Glide.with(context)
            .asBitmap()
            .load(bitmap)
            .transform(CircleCrop())
            .into(imageView)
    }

    fun showFromBitmap(bitmap: Bitmap, imageView: ImageView) {

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        Glide.with(context)
            .asBitmap()
            .load(bitmap)
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(15)))
            .into(imageView)
    }

    fun toBitmap(contentResolver: ContentResolver, fileUri: Uri): Bitmap {

        val bitmap = scaleBitmap(MediaStore.Images.Media.getBitmap(contentResolver, fileUri))

        contentResolver.notifyChange(fileUri, null)
        val exif = ExifInterface(getRealPathFromURI(contentResolver, fileUri)!!)

        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1F, 1F)

            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180F)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180F)
                matrix.postScale(-1F, 1F)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90F)
                matrix.postScale(-1F, 1F)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90F)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90F);
                matrix.postScale(-1F, 1F);
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90F)

        }
        var bmRotated = bitmap
        try {
            bmRotated = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
            //bitmap.recycle()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()

        }

        return bmRotated
    }

    private fun getRealPathFromURI(
        contentResolver: ContentResolver,
        contentUri: Uri?
    ): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(contentUri!!, proj, null, null, null)
            val columnIndex: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)!!
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun scaleBitmap(originalBitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(
            originalBitmap,
            1080,
            (originalBitmap.height * (1080.0 / originalBitmap.width)).toInt(),
            true
        )

    }


}