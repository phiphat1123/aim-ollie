package com.ollie.aim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

class CrossView(c: Context) : View(c) {
    var visible = false
    val size = 120
    val half = size / 2f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFF2D55.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    override fun onMeasure(w: Int, h: Int) = setMeasuredDimension(size, size)
    override fun onDraw(canvas: Canvas) {
        if (!visible) return
        canvas.drawCircle(half, half, half - 6, paint)
        val p2 = Paint(paint).apply { strokeWidth = 3f }
        canvas.drawLine(half, 4f, half, half - 10, p2)
        canvas.drawLine(half, half + 10, half, size - 4f, p2)
        canvas.drawLine(4f, half, half - 10, half, p2)
        canvas.drawLine(half + 10, half, size - 4f, half, p2)
    }
}
