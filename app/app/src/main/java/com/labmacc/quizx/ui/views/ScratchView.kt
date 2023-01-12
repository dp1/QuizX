package com.labmacc.quizx.ui.views

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.labmacc.quizx.R

class ScratchView(context: Context?) : View(context), View.OnTouchListener {
    private val strokeSize = 40f * resources.displayMetrics.density

    private lateinit var buffer: Bitmap
    private lateinit var bufferCanvas: Canvas
    private var bitmap: Bitmap? = null
    private var bitmapTransform: Matrix? = null

    private val circlePaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        strokeWidth = strokeSize
        strokeCap = Paint.Cap.ROUND
    }

    private var prevPosition = Pair(0f, 0f)

    init {
        setOnTouchListener(this)
    }

    fun setImage(img: Bitmap) {
        bitmap = img
    }

    fun coveredArea(): Float {
        var coveredPixels = 0
        for (x in 0 until buffer.width) {
            for (y in 0 until buffer.height) {
                val pixel = buffer.getPixel(x, y)
                if (pixel == Color.GRAY) {
                    coveredPixels += 1
                }
            }
        }
        val imageArea = buffer.width * buffer.height
        return coveredPixels.toFloat() / imageArea
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!::buffer.isInitialized) {
            buffer = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888
            )

            bufferCanvas = Canvas().apply {
                setBitmap(buffer)
                drawColor(Color.GRAY)
            }
        }

        if (bitmap != null && bitmapTransform == null) {
            bitmapTransform = Matrix().apply {
                setRectToRect(
                    RectF(0f, 0f, bitmap!!.width.toFloat(), bitmap!!.height.toFloat()),
                    RectF(0f, 0f, width.toFloat(), height.toFloat()),
                    Matrix.ScaleToFit.FILL
                )
            }
        }

        canvas?.apply {
            bitmap?.let {
                drawBitmap(it, bitmapTransform!!, null)
            }
            drawBitmap(buffer, 0f, 0f, null)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (bitmap == null) {
            return true
        }

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                bufferCanvas.drawCircle(event.x, event.y, strokeSize / 2, circlePaint)
                prevPosition = Pair(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                bufferCanvas.drawLine(
                    prevPosition.first,
                    prevPosition.second,
                    event.x,
                    event.y,
                    circlePaint
                )
                prevPosition = Pair(event.x, event.y)
                invalidate()
            }
        }
        return true
    }
}