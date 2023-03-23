/*
 * Created by kyy on 3/23/23, 12:05 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/22/23, 11:52 PM
 */

package com.triankyy.imagedetector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.triankyy.imagedetector.page.HistoryPage
import com.triankyy.imagedetector.page.HomePage
import com.triankyy.imagedetector.ui.theme.ImageDetectorTheme
import java.util.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            ImageDetectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomePage(navController = navController)
                        }
                        composable("history") {
                            HistoryPage(navController = navController)
                        }
                    }
                }
            }
        }
    }
}