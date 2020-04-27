package com.example.shadowui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class CircleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val antialiasPaint = Paint().apply {
        isAntiAlias = true
    }

    val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    val fillPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    val debugPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 2f
    }

    val baseColor = Color.parseColor("#262626")

    var width = 0f
    var height = 0f
    var btnWidth = 0f
    var btnHeight = 0f
    var cx = 0f
    var cy = 0f
    var btnRadius = 0f
    var radius = 0f
    var padding = 60f
    var paddingPercent = 0f
    lateinit var rect: RectF

    override fun onDraw(canvas: Canvas) {

        width = canvas.width.toFloat()
        height = canvas.height.toFloat()

        rect = RectF(padding, padding / 2, width - padding, height - padding / 2)

        btnWidth = width - padding * 2
        btnHeight = height - padding * 2
        cx = width / 2
        cy = height / 2
        btnRadius = btnHeight / 2f
        radius = height / 2f
        paddingPercent = padding / width

        drawPadding(canvas)
        drawButton(canvas)

        drawChar(canvas)

        //canvas.drawRect(rect, debugPaint)
        //canvas.drawCircle(cy, cy, btnRadius, debugPaint)
    }

    private fun drawChar(canvasZ: Canvas) {
        val src = BitmapFactory.decodeResource(resources, R.drawable.baseline_favorite_border_white_24)
        val glowMargin = 50f
        val bmp =
            Bitmap.createBitmap(
                src.width + glowMargin.toInt(),
                src.height + glowMargin.toInt(),
                Bitmap.Config.ARGB_8888
            )


        val alpha = src.extractAlpha()
        val canvas = Canvas(bmp)

        paint.color = Color.WHITE
        paint.maskFilter = BlurMaskFilter(25f, BlurMaskFilter.Blur.OUTER)

        canvas.drawBitmap(alpha, glowMargin / 2, glowMargin / 2f, paint)

        canvasZ.drawBitmap(bmp, cx - bmp.width / 2, cy - bmp.height / 2, antialiasPaint)
        canvasZ.drawBitmap(src, cx - src.width / 2, cy - src.height / 2, antialiasPaint)
    }

    private fun drawPadding(canvas: Canvas) {

//        resetPaint(fillPaint)
//        paddingColorsSteps = floatArrayOf(1f - paddingPercent, 1f)
//        val surfaceShader = RadialGradient(cX, cY, radius, paddingColors, paddingColorsSteps, Shader.TileMode.CLAMP)
//        fillPaint.shader = surfaceShader
//        fillPaint.alpha = 255
//        canvas.drawCircle(cX, cY, radius, fillPaint)

        drawLightPadding(canvas)
        drawShadowPadding(canvas)
    }

    private fun drawPaddingCut(canvas: Canvas) {
        resetPaint(fillPaint)
        fillPaint.color = baseColor
        canvas.drawCircle(cx, cy, btnRadius + 7, fillPaint)

        strokePaint.color = Color.BLACK
        canvas.drawCircle(cx, cy, btnRadius + 1, strokePaint)
    }

    private fun drawLightPadding(canvas: Canvas) {
        resetPaint(fillPaint)
        val lightShader = RadialGradient(
            cx,
            cy,
            radius,
            intArrayOf(Color.WHITE, Color.TRANSPARENT),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        fillPaint.color = Color.WHITE
        fillPaint.alpha = 100
        fillPaint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
        fillPaint.shader = lightShader
        canvas.drawArc(rect, 0f, -180f, true, fillPaint)
    }

    private fun drawShadowPadding(canvas: Canvas) {
        resetPaint(fillPaint)
        val lightShader = RadialGradient(
            cx,
            cy,
            radius,
            intArrayOf(Color.BLACK, Color.TRANSPARENT),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        fillPaint.color = Color.BLACK
        fillPaint.alpha = 180
        fillPaint.maskFilter = BlurMaskFilter(35f, BlurMaskFilter.Blur.NORMAL)
        //fillPaint.shader = lightShader
        canvas.drawArc(rect, 0f, 180f, true, fillPaint)
    }

    private fun drawButton(canvas: Canvas) {

        drawPaddingCut(canvas)

        resetPaint(fillPaint)
        //val surfaceShader = RadialGradient(cx, cy, btnRadius, buttonColors, buttonColorsSteps, Shader.TileMode.MIRROR)
        //fillPaint.shader = surfaceShader
        fillPaint.color = baseColor
        fillPaint.alpha = 255
        canvas.drawCircle(cx, cy, btnRadius, fillPaint)



        resetPaint(fillPaint)
        val surfaceShader = RadialGradient(cx, cy, btnRadius, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
        fillPaint.shader = surfaceShader
        fillPaint.alpha = 45
        canvas.drawCircle(cx, cy, btnRadius, fillPaint)


        resetPaint(fillPaint)
        val lightShader = LinearGradient(cx, 0f, cx, btnHeight, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
        fillPaint.shader = lightShader
        fillPaint.alpha = 45
        canvas.drawCircle(cx, cy, btnRadius, fillPaint)
    }

    private fun resetPaint(paint: Paint) {
        paint.alpha = 255
        paint.shader = null
        paint.maskFilter = null
    }
}