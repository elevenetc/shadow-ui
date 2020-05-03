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
        isDither = true
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

    var padding = 60f

    var paddingPercent = 0f

    val cornerRadius = 20f

    lateinit var lightRect: RectF
    lateinit var btnRect: RectF
    lateinit var totalRect: RectF

    val cutWidth = 7f
    val cutGap = 1f

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

        drawPadding(canvas)
        drawCut(canvas)
        drawButton(canvas)
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
            { c, width, height ->
                fillPaint.color = Color.BLACK
                c.drawRoundRect(0f, 0f, btnRect.width(), btnRect.height(), 25f, 25f, fillPaint)
                fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
                val width = 5f
                c.drawRoundRect(width, width, btnRect.width() - width, btnRect.height() - width, 25f, 25f, fillPaint)
            },
            Bitmap.Config.ARGB_8888
        )
    }

    private fun drawCut(canvas: Canvas) {

        resetPaint(fillPaint)
        resetPaint(strokePaint)

        fillPaint.color = baseColor

        val cornerDecreaser = cornerRadius / 10f
        canvas.drawRoundRect(
            btnRect.left - cutWidth,
            btnRect.top - cutWidth,
            btnRect.right + cutWidth,
            btnRect.bottom + cutWidth,
            cornerRadius + cornerDecreaser,
            cornerRadius + cornerDecreaser,
            fillPaint
        )

        strokePaint.color = Color.BLACK
        strokePaint.strokeWidth = cutGap
        canvas.drawRoundRect(btnRect, cornerRadius, cornerRadius, strokePaint)
    }

    private fun drawInnerShadow(canvas: Canvas) {

        resetPaint(fillPaint)
        resetPaint(strokePaint)
        resetPaint(antialiasPaint)

        val blurRadius = 50f
        val borderWidth = 40f

        val borderBitmap = createBmp(
            totalRect,
            { c, width, height ->
                fillPaint.color = Color.BLACK
                c.drawRect(0f, 0f, width, height, fillPaint)
                fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

                c.drawRoundRect(
                    borderWidth,
                    borderWidth,
                    width - borderWidth,
                    height - borderWidth,
                    cornerRadius,
                    cornerRadius,
                    fillPaint
                )
            }
        )

        val glowCanvasBmp = createBmp(totalRect)

        resetPaint(fillPaint)

        val sourceAlpha = borderBitmap.extractAlpha()
        val glowCanvas = Canvas(glowCanvasBmp)

        paint.color = Color.BLACK
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

        glowCanvas.clipPath(rectToPath(btnRect, cornerRadius))
        paint.alpha = 175
        glowCanvas.drawBitmap(sourceAlpha, 0f, 0f, paint)

        canvas.drawBitmap(glowCanvasBmp, 0f, 0f, antialiasPaint)
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

    private fun drawLightPadding(canvas: Canvas) {
        resetPaint(fillPaint)
        val lightShader = LinearGradient(
            cx,
            0f,
            cx,
            cy,
            intArrayOf(Color.WHITE, Color.TRANSPARENT),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        fillPaint.color = Color.WHITE
        //fillPaint.alpha = 100
        //fillPaint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
        fillPaint.shader = lightShader

        val blurRadius = padding - padding / 2.5f
        val borderSize = padding

        val borderBitmap = createBmp(
            totalRect,
            { c, width, height ->
                fillPaint.color = Color.WHITE
                //c.drawRect(0f, 0f, width, height, fillPaint)
                //fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

                val widthDecreaser = borderSize / 12f

                c.drawRoundRect(
                    borderSize + widthDecreaser,
                    borderSize,
                    width - borderSize - widthDecreaser,
                    height - borderSize,
                    cornerRadius,
                    cornerRadius,
                    fillPaint
                )
            }
        )

        val glowCanvasBmp = createBmp(totalRect)

        resetPaint(fillPaint)

        val sourceAlpha = borderBitmap.extractAlpha()
        val glowCanvas = Canvas(glowCanvasBmp)

        paint.color = Color.WHITE
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

        glowCanvas.drawBitmap(sourceAlpha, 0f, 0f, paint)
        canvas.drawBitmap(glowCanvasBmp, 0f, 0f, antialiasPaint)

        //canvas.drawBitmap(borderBitmap, 0f, 0f, null)


        //canvas.drawRect(totalRect, fillPaint)
        //canvas.drawArc(lightRect, 0f, -180f, true, fillPaint)
    }

    private fun drawShadowPadding(canvas: Canvas) {
        resetPaint(fillPaint)
        val lightShader = LinearGradient(
            cx,
            cy,
            cx,
            0f,
            intArrayOf(Color.BLACK, Color.TRANSPARENT),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        fillPaint.color = Color.BLACK
        fillPaint.shader = lightShader

        val blurRadius = padding
        val borderSize = padding

        val borderBitmap = createBmp(
            totalRect,
            { c, width, height ->
                fillPaint.color = Color.BLACK
                //c.drawRect(0f, 0f, width, height, fillPaint)
                //fillPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

                val borderSizeDecreaser = borderSize / 10f

                c.drawRoundRect(
                    borderSize + borderSizeDecreaser,
                    borderSize,
                    width - borderSize - borderSizeDecreaser,
                    height - borderSize,
                    cornerRadius,
                    cornerRadius,
                    fillPaint
                )
            }
        )

        val glowCanvasBmp = createBmp(totalRect)

        resetPaint(fillPaint)

        val sourceAlpha = borderBitmap.extractAlpha()
        val glowCanvas = Canvas(glowCanvasBmp)

        paint.color = Color.BLACK
        paint.isDither = true
        paint.maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)

        glowCanvas.drawBitmap(sourceAlpha, 0f, 0f, paint)
        canvas.drawBitmap(glowCanvasBmp, 0f, 0f, antialiasPaint)
    }

    private fun drawButton(canvas: Canvas) {

        resetPaint(fillPaint)

        val lightShader = LinearGradient(
            cx,
            btnRect.top,
            cx,
            btnRect.bottom,
            intArrayOf(Color.WHITE, Color.TRANSPARENT, Color.BLACK),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )

        fillPaint.alpha = 20
        fillPaint.shader = lightShader

        canvas.drawRoundRect(btnRect, cornerRadius, cornerRadius, fillPaint)
    }

    private fun resetPaint(paint: Paint) {
        paint.alpha = 255
        paint.shader = null
        paint.maskFilter = null
        paint.xfermode = null
    }
}