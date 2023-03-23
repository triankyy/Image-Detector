/*
 * Created by kyy on 3/23/23, 12:05 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/22/23, 11:39 PM
 */

buildscript {
    extra.apply {
        set("compose_ui_version", "1.3.3")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.0" apply false
    id("com.android.library") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}