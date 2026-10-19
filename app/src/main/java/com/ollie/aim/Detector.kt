package com.ollie.aim

import android.graphics.Bitmap
import kotlin.math.hypot

object Detector {
    fun scan(bmp: Bitmap): Pair<Float, Float>? {
        val W = bmp.width; val H = bmp.height
        val step = 4
        val cx = W / 2f; val cy = H / 2f
        var bestScore = 0; var bestX = 0f; var bestY = 0f
        val x0 = (W * 0.20).toInt(); val x1 = (W * 0.80).toInt()
        val y0 = (H * 0.10).toInt(); val y1 = (H * 0.70).toInt()
        for (y in y0 until y1 step step) {
            for (x in x0 until x1 step step) {
                if (!isSkin(bmp.getPixel(x, y))) continue
                var n = 0
                for (dy in -6..6 step 3) for (dx in -6..6 step 3) {
                    val px = (x + dx).coerceIn(0, W - 1)
                    val py = (y + dy).coerceIn(0, H - 1)
                    if (isSkin(bmp.getPixel(px, py))) n++
                }
                val dist = hypot(x - cx, y - cy)
                val score = (n * 100 - dist).toInt()
                if (score > bestScore) {
                    bestScore = score; bestX = x.toFloat(); bestY = y.toFloat()
                }
            }
        }
        return if (bestScore > 200) bestX to bestY else null
    }

    private fun isSkin(c: Int): Boolean {
        val r = (c shr 16) and 0xFF
        val g = (c shr 8) and 0xFF
        val b = c and 0xFF
        return r in 95..255 && g in 40..180 && b in 20..120 &&
               r > g && r > b && (r - g) > 10
    }
}
