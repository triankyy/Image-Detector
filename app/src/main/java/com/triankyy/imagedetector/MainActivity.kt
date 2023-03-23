/*
 * Created by kyy on 3/23/23, 12:05 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/22/23, 11:52 PM
 */

package com.triankyy.imagedetector

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.triankyy.imagedetector.ui.theme.ImageDetectorTheme
import com.triankyy.imagedetector.util.ImageClassifier
import com.triankyy.imagedetector.util.Keys.INPUT_SIZE
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var photoImage: Bitmap
    private lateinit var classifier: ImageClassifier
    private fun Context.createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            externalCacheDir      /* directory */
        )
    }
    private fun Context.toast(message: CharSequence): Unit {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        classifier = ImageClassifier(assets);
        setContent {
            var imageResult by remember { mutableStateOf<ImageBitmap?>(null) }
            var txtResult = mutableStateListOf<String>()

            val context = LocalContext.current
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                BuildConfig.APPLICATION_ID + ".provider", file
            )
            val galeryLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) {
                if(it == null) return@rememberLauncherForActivityResult
                txtResult.clear()
                val stream = contentResolver.openInputStream(it)
                if(::photoImage.isInitialized) photoImage.recycle()
                photoImage = BitmapFactory.decodeStream(stream)
                imageResult = photoImage.asImageBitmap()
                photoImage = Bitmap.createScaledBitmap(photoImage, INPUT_SIZE, INPUT_SIZE, false)
                classifier.recognizeImage(photoImage).subscribeBy { result ->
                    result.map {
                        txtResult.add(it.toString())
                    }
                }
            }
            val cameraLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.TakePicture()
            ) {
//                imageUri = uri
            }
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) {
                if (it) {
                    context.toast("Permission Granted")
                    cameraLauncher.launch(uri)
                } else {
                    context.toast("Permission Denied")
                }

            }

            ImageDetectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text(text = "Image detector") }
                            )
                        },
                        bottomBar = {
                            BottomAppBar(
                                containerColor = MaterialTheme.colorScheme.primary,
                                actions = {
                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(
                                            painter = painterResource(R.drawable.baseline_history_24),
                                            contentDescription = null
                                        )
                                    }
                                },
                                floatingActionButton = {
                                    ExtendedFloatingActionButton(
                                        text = { Text(text = "Pick Image") },
                                        icon = {
                                           Icon(
                                                painter = painterResource(R.drawable.baseline_add_a_photo_24),
                                                contentDescription = null
                                            )
                                        },
                                        onClick = { galeryLauncher.launch("image/*") },
//                                        onClick = {
//                                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
//
//                                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
//                                                cameraLauncher.launch(uri)
//                                            } else {
//                                                // Request a permission
//                                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
//                                            }
//                                        },
                                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                    )
                                }
                            )
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(it)
                        ) {
                            if (imageResult != null) {
                                Image(
                                    bitmap = imageResult!!,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                            txtResult.forEach {
                                Text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}