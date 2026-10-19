package com.ollie.aim

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val projMgr by lazy {
        getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }
    private val reqCapture = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val i = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra("code", res.resultCode)
                putExtra("data", res.data)
            }
            ContextCompat.startForegroundService(this, i)
        }
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_main)
        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
        }
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            ContextCompat.startForegroundService(this,
                Intent(this, OverlayService::class.java))
            reqCapture.launch(projMgr.createScreenCaptureIntent())
        }
    }
}
