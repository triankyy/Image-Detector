/*
 * Created by kyy on 3/23/23, 8:50 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/23/23, 8:50 PM
 */

package com.triankyy.imagedetector.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.triankyy.imagedetector.ui.theme.Purple40

@Composable
fun CustomDialog(
    openDialogCustom: MutableState<Boolean>,
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    cancel: String,
    onCancel: () -> Unit,
    accept: String,
    onAccept: () -> Unit
) {
    Dialog(onDismissRequest = { openDialogCustom.value = false}) {
        CustomDialogUI(
            openDialogCustom = openDialogCustom,
            icon = icon,
            title = title,
            subtitle = subtitle,
            cancel = cancel,
            onCancel = onCancel,
            accept = accept,
            onAccept = onAccept
        )
    }
}

@Composable
fun CustomDialogUI(
    modifier: Modifier = Modifier,
    openDialogCustom: MutableState<Boolean>,
    @DrawableRes icon: Int,
    title: String,
    subtitle: String,
    cancel: String,
    onCancel: () -> Unit,
    accept: String,
    onAccept: () -> Unit
) {
    Card(
        //shape = MaterialTheme.shapes.medium,
        shape = RoundedCornerShape(10.dp),
        // modifier = modifier.size(280.dp, 240.dp)
        modifier = Modifier.padding(10.dp,5.dp,10.dp,10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier
                .background(Color.White)) {

            //.......................................................................
            Image(
                painter = painterResource(id = icon),
                contentDescription = null, // decorative
                contentScale = ContentScale.Fit,
                colorFilter  = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(top = 35.dp)
                    .height(70.dp)
                    .fillMaxWidth(),

                )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            //.......................................................................
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalArrangement = Arrangement.SpaceAround) {

                TextButton(onClick = {
                    onCancel()
                    openDialogCustom.value = false
                }) {

                    Text(
                        text = cancel,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(onClick = {
                    onAccept()
                    openDialogCustom.value = false
                }) {
                    Text(
                        text = accept,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}