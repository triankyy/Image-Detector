/*
 * Created by kyy on 3/23/23, 12:05 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/23/23, 12:04 AM
 */

package com.triankyy.imagedetector.model

import android.graphics.RectF

class Result(val id: String?, val title: String?, val confidence: Float?, private var location: RectF?) {
    override fun toString(): String {
        var resultString = ""
        if (title != null) resultString += "$title: "
        if (confidence != null) resultString += confidence.toString()
        return resultString
    }
}