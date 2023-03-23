/*
 * Created by kyy on 3/23/23, 12:05 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/23/23, 12:04 AM
 */

package com.triankyy.imagedetector.util

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.triankyy.imagedetector.model.Result
import com.triankyy.imagedetector.util.Keys.DIM_BATCH_SIZE
import com.triankyy.imagedetector.util.Keys.DIM_IMG_SIZE_X
import com.triankyy.imagedetector.util.Keys.DIM_IMG_SIZE_Y
import com.triankyy.imagedetector.util.Keys.DIM_PIXEL_SIZE
import com.triankyy.imagedetector.util.Keys.INPUT_SIZE
import com.triankyy.imagedetector.util.Keys.LABEL_PATH
import com.triankyy.imagedetector.util.Keys.MAX_RESULTS
import com.triankyy.imagedetector.util.Keys.MODEL_PATH
import io.reactivex.Single
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class ImageClassifier constructor(assetManager: AssetManager) {
    private var interpreter: Interpreter? = null
    private var labelProb: Array<ByteArray>
    private val labels = Vector<String>()
    private val intValues by lazy { IntArray(INPUT_SIZE * INPUT_SIZE) }
    private var imgData: ByteBuffer

    init {
        try {
            assetManager.open(LABEL_PATH).bufferedReader().use {
                while (true) {
                    val line = it.readLine() ?: break
                    labels.add(line)
                }
            }
        } catch (e: IOException) {
            Log.e("ERR", e.toString())
            throw RuntimeException("Problem reading label file!", e)
        }
        labelProb = Array(1) { ByteArray(labels.size) }
        imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE)
        imgData.order(ByteOrder.nativeOrder())
        try {
            interpreter = Interpreter(loadModelFile(assetManager))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }


    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        imgData.rewind()
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until DIM_IMG_SIZE_X) {
            for (j in 0 until DIM_IMG_SIZE_Y) {
                val value = intValues[pixel++]
                imgData.put((value shr 16 and 0xFF).toByte())
                imgData.put((value shr 8 and 0xFF).toByte())
                imgData.put((value and 0xFF).toByte())
            }
        }
    }

    private fun loadModelFile(assets: AssetManager): MappedByteBuffer {
        val fileDescriptor = assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun recognizeImage(bitmap: Bitmap): Single<List<Result>> {
        return Single.just(bitmap).flatMap {
            convertBitmapToByteBuffer(it)
            interpreter!!.run(imgData, labelProb)
            val pq = PriorityQueue<Result>(3) { lhs, rhs ->
                // Intentionally reversed to put high confidence at the head of the queue.
                (rhs.confidence!!).compareTo(lhs.confidence!!)
            }
            for (i in labels.indices) {
                pq.add(
                    Result(
                        id = "" + i,
                        title = if (labels.size > i) labels[i] else "unknown",
                        confidence = labelProb[0][i].toFloat(),
                        location = null
                    )
                )
            }
            val recognitions = ArrayList<Result>()
            val recognitionsSize = pq.size.coerceAtMost(MAX_RESULTS)
            for (i in 0 until recognitionsSize) pq.poll()?.let { it1 -> recognitions.add(it1) }
            return@flatMap Single.just(recognitions)
        }
    }

    fun close() {
        interpreter?.close()
    }
}