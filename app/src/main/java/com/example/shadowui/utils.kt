package com.example.shadowui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF

fun rectToPath(rect: RectF, round: Float = 0f): Path {
    val result = Path()
    result.addRoundRect(rect, round, round, Path.Direction.CW)
    return result
}

fun createBmp(rect: RectF, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
    return Bitmap.createBitmap(
        rect.width().toInt(),
        rect.height().toInt(),
        config
    )
}

fun createBmp(
    rect: RectF,
    drawFun: (Canvas, Float, Float) -> Unit,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888
): Bitmap {
    return createBmp(rect.width().toInt(), rect.height().toInt(), config, drawFun)
}

fun createBmp(
    width: Int,
    height: Int,
    config: Bitmap.Config,
    drawFun: (Canvas, Float, Float) -> Unit
): Bitmap {

    val result = Bitmap.createBitmap(width, height, config)

    val canvas = Canvas(result)

    drawFun(canvas, width.toFloat(), height.toFloat())

    return result
}