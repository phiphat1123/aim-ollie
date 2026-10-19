package com.ollie.aim

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class ScreenCaptureService : Service() {
    private var proj: MediaProjection? = null
    private var reader: ImageReader? = null
    private val w = 720; private val h = 1280
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(i: Intent?, f: Int, id: Int): Int {
        startForeground(1, buildNotif())
        val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        proj = mgr.getMediaProjection(i!!.getIntExtra("code", 0),
                                      i.getParcelableExtra("data")!!)
        reader = ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 2)
        proj!!.createVirtualDisplay("cap", w, h,
            resources.displayMetrics.densityDpi, reader!!.surface, null, null)
        reader!!.setOnImageAvailableListener({ r ->
            val img = r.acquireLatestImage() ?: return@setOnImageAvailableListener
            val bmp = img.toBitmap(); img.close()
            val hit = Detector.scan(bmp)
            val sw = resources.displayMetrics.widthPixels.toFloat()
            val sh = resources.displayMetrics.heightPixels.toFloat()
            handler.post {
                OverlayService.instance?.drawAt(
                    hit?.first?.times(sw / w),
                    hit?.second?.times(sh / h))
            }
        }, handler)
        return START_STICKY
    }

    private fun Image.toBitmap(): Bitmap {
        val p = planes[0]
        val rowPx = p.rowStride / p.pixelStride
        val bmp = Bitmap.createBitmap(rowPx, height, Bitmap.Config.ARGB_8888)
        bmp.copyPixelsFromBuffer(p.buffer)
        return bmp
    }

    private fun buildNotif(): Notification {
        val ch = "aim_ch"
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(ch) == null)
            nm.createNotificationChannel(
                NotificationChannel(ch, "aim", NotificationManager.IMPORTANCE_LOW))
        return NotificationCompat.Builder(this, ch)
            .setContentTitle("ollie aim")
            .setContentText("กำลังจับภาพหน้าจอ")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()
    }
    override fun onBind(i: Intent?): IBinder? = null
}
