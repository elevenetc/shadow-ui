package com.example.shadowui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class RectShadowButton(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    val antialiasPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    val xFerPaint = Paint().apply {
        isAntiAlias = true
    }

    val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        isDither = true
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
        alpha = 100
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
    var padding = 30f
    var paddingPercent = 0f

    val roundCornern = 10f

    lateinit var lightRect: RectF
    lateinit var btnRect: RectF
    lateinit var totalRect: RectF

    val debug = Debug(false)

    override fun onDraw(canvas: Canvas) {

        width = canvas.width.toFloat()
        height = canvas.height.toFloat()

        lightRect = RectF(padding, padding, width - padding, height - padding)
        btnRect = RectF(padding, padding, width - padding, height - padding)
        totalRect = RectF(0f, 0f, width, height)

        btnWidth = btnRect.width()
        btnHeight = btnRect.height()
        cx = width / 2
        cy = height / 2
        btnRadius = btnHeight / 2f
        radius = height / 2f
        paddingPercent = padding / width

        //drawPadding(canvas)
        //drawButton(canvas)

        //drawChar(canvas)

        drawInnerShadow(canvas)

        if (debug.enabled) {
            canvas.drawRect(btnRect, debugPaint)
            canvas.drawRect(totalRect, debugPaint)
        }
    }

    private fun createHole() {
        val src = createBmp(
            btnRect,
            Bitmap.Config.ARGB_8888
        ) { c, width, height ->
            fillPaint.color = Color.BLACK
            c.drawRoundRect(0f, 0f, btnRect.width(), btnRect.height(), 25f, 25f, fillPaint)
            fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            val width = 5f
            c.drawRoundRect(width, width, btnRect.width() - width, btnRect.height() - width, 25f, 25f, fillPaint)
        }
    }

    private fun drawInnerShadow(canvas: Canvas) {

        resetPaint(fillPaint)
        resetPaint(strokePaint)
        resetPaint(antialiasPaint)

        val src = createBmp(
            totalRect,
            Bitmap.Config.ARGB_8888
        ) { c, width, height ->
            fillPaint.color = Color.BLACK
            c.drawRect(0f, 0f, width, height, fillPaint)
            fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            val size = 50f
            c.drawRoundRect(size, size, width - size, height - size, 25f, 25f, fillPaint)
        }

        val glowCanvasBmp = createBmp(totalRect)

        resetPaint(fillPaint)

        val sourceAlpha = src.extractAlpha()
        val glowCanvas = Canvas(glowCanvasBmp)

        paint.color = Color.BLACK
        val blurRadius = 50f
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

        glowCanvas.clipPath(rectToPath(btnRect, 25f))
        glowCanvas.drawBitmap(sourceAlpha, 0f, 0f, paint)

        canvas.drawBitmap(glowCanvasBmp, 0f, 0f, antialiasPaint)
    }

    private fun rectToPath(rect: RectF, round: Float = 0f): Path {
        val result = Path()
        result.addRoundRect(rect, round, round, Path.Direction.CW)
        return result
    }

    private fun createBmp(rect: RectF, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap {
        return Bitmap.createBitmap(
            totalRect.width().toInt(),
            totalRect.height().toInt(),
            config
        )
    }

    private fun createBmp(
        rect: RectF,
        config: Bitmap.Config,
        drawFun: (Canvas, Float, Float) -> Unit
    ): Bitmap {
        return createBmp(rect.width().toInt(), rect.height().toInt(), config, drawFun)
    }

    private fun createBmp(
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
        canvas.drawArc(lightRect, 0f, -180f, true, fillPaint)
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
        canvas.drawArc(lightRect, 0f, 180f, true, fillPaint)
    }

    private fun drawButton(canvas: Canvas) {

        //drawPaddingCut(canvas)

        val destinationBmp = Bitmap.createBitmap(
            btnRect.width().toInt(),
            btnRect.height().toInt(),
            Bitmap.Config.ARGB_8888
        )
//
//        val cnv = Canvas(bmp)

        // base
        resetPaint(fillPaint)
        fillPaint.color = Color.RED
        //fillPaint.color = baseColor
        fillPaint.alpha = 255
        canvas.drawRoundRect(btnRect, roundCornern, roundCornern, fillPaint)
        //cnv.drawRoundRect(btnRect, roundCornern, roundCornern, fillPaint)

        // radial light
        resetPaint(fillPaint)
        //val surfaceShader = RadialGradient(cx, cy, btnRadius, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
        fillPaint.color = Color.RED
        fillPaint.maskFilter = BlurMaskFilter(35f, BlurMaskFilter.Blur.INNER)
        fillPaint.alpha = 150
        fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        canvas.drawCircle(100f, 100f, 70f, fillPaint)

        fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        canvas.drawCircle(200f, 100f, 70f, fillPaint)

        fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        canvas.drawCircle(300f, 100f, 70f, fillPaint)

        //canvas.drawRoundRect(btnRect, roundCornern, roundCornern, fillPaint)

        // vertical light
//        resetPaint(fillPaint)
//        val lightShader = LinearGradient(cx, 0f, cx, btnHeight, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
//        fillPaint.shader = lightShader
//        fillPaint.alpha = 45
//        canvas.drawRoundRect(btnRect, roundCornern, roundCornern, fillPaint)
    }

    private fun resetPaint(paint: Paint) {
        paint.alpha = 255
        paint.shader = null
        paint.maskFilter = null
        paint.xfermode = null
    }
}