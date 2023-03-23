/*
 * Created by kyy on 3/23/23, 11:12 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/23/23, 11:12 PM
 */

package com.triankyy.imagedetector.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HistoryPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Image detector") }
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            Text(text = "History page")
        }
    }
}