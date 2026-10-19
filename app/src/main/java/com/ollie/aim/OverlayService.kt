package com.ollie.aim

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.WindowManager

class OverlayService : Service() {
    companion object { var instance: OverlayService? = null }
    private lateinit var wm: WindowManager
    private var view: CrossView? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val v = CrossView(this)
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT)
        wm.addView(v, lp)
        view = v
    }

    fun drawAt(x: Float?, y: Float?) {
        val v = view ?: return
        val lp = v.layoutParams as WindowManager.LayoutParams
        if (x == null || y == null) v.visible = false
        else {
            v.visible = true
            lp.x = (x - v.half).toInt()
            lp.y = (y - v.half).toInt()
            wm.updateViewLayout(v, lp)
        }
    }
    override fun onDestroy() { instance = null; super.onDestroy() }
    override fun onBind(i: Intent?): IBinder? = null
}
