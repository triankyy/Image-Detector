/*
 * Created by kyy on 3/23/23, 5:45 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/23/23, 5:45 PM
 */

package com.triankyy.imagedetector.page

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.DateFormat.getDateTimeInstance
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.triankyy.imagedetector.BuildConfig
import com.triankyy.imagedetector.R
import com.triankyy.imagedetector.component.CustomDialog
import com.triankyy.imagedetector.util.ImageClassifier
import com.triankyy.imagedetector.util.Keys.INPUT_SIZE
import io.reactivex.rxkotlin.subscribeBy
import java.io.File
import java.util.*

fun Context.toast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

private fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = getDateTimeInstance().format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
}

@SuppressLint("CheckResult")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomePage(navController: NavController) {
    val context = LocalContext.current
    lateinit var photoImage: Bitmap
    val classifier = ImageClassifier(context.assets)
    val file = context.createImageFile()
    val resolver = context.contentResolver

    val showDialog = remember { mutableStateOf(false) }
    var imageResult by remember { mutableStateOf<ImageBitmap?>(null) }
    val name = remember { mutableStateListOf<String>() }
    val confidence = remember { mutableStateListOf<Float>() }

    fun scaleImage(uri: Uri) {
        val stream = resolver.openInputStream(uri)
//        photoImage.recycle()
        name.clear()
        confidence.clear()
        photoImage = BitmapFactory.decodeStream(stream)
        imageResult = photoImage.asImageBitmap()
        photoImage = Bitmap.createScaledBitmap(photoImage, INPUT_SIZE, INPUT_SIZE, false)
        classifier.recognizeImage(photoImage).subscribeBy { result ->
            result.map {
                name.add(it.title)
                confidence.add(it.confidence)
            }
        }
    }

    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { if (it) scaleImage(uri) }

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

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { res ->
        if(res == null) return@rememberLauncherForActivityResult
        scaleImage(res)
    }

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
                    IconButton(onClick = { navController.navigate("history") }) {
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
                        onClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)

                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                showDialog.value = true
                            } else {
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (imageResult != null) {
                Image(
                    bitmap = imageResult!!,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                name.forEach {
                    Text(it)
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.baseline_image_not_supported_24),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(
                            color = Color.Gray
                        ),
                        modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth()
                    )
                    Text(text = "No image found", color = Color.Gray)
                }
            }
            if (showDialog.value) {
                CustomDialog(
                    openDialogCustom = showDialog,
                    icon = R.drawable.baseline_add_a_photo_24,
                    title = "Select photo",
                    subtitle = "Select photo from source",
                    cancel = "Camera",
                    onCancel = { cameraLauncher.launch(uri) },
                    accept = "Gallery",
                    onAccept = { galleryLauncher.launch("image/*") }
                )
            }
        }
    }
}