package com.levandowski.androidstream

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.Surface
import android.view.SurfaceView

class ScreenCaptureManager (private val activity: Activity,
                            private val surfaceView : SurfaceView) {

    private var density: Int = 0
    private var surface: Surface? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaProjectionManager: MediaProjectionManager? = null

    init {
        DisplayMetrics().run {
            activity.windowManager.defaultDisplay.getMetrics(this)
            this@ScreenCaptureManager.density =densityDpi
        }
        mediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        surface = surfaceView.holder?.surface
    }

    fun startIfNeeded(code: Int, mResultCode: Int, mResultData: Intent?) {
        if (virtualDisplay == null)
            startScreenCapture(code, mResultCode, mResultData)
        else
            stopScreenCapture()
    }

    private fun startScreenCapture(code: Int, mResultCode: Int, mResultData: Intent?) {
        if (mediaProjection != null) {
            setUpVirtualDisplay()
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection(mResultCode, mResultData)
            setUpVirtualDisplay()
        } else {
            activity.startActivityForResult(mediaProjectionManager?.createScreenCaptureIntent(), code)
        }
    }

    fun setUpMediaProjection(mResultCode: Int, mResultData: Intent?) {
        mediaProjection = mediaProjectionManager?.getMediaProjection(mResultCode, mResultData)
    }

    fun setUpVirtualDisplay() {
        val width =  surfaceView.width
        val height = surfaceView.height

        virtualDisplay = mediaProjection?.
            createVirtualDisplay(
                "ScreenCapture", width,
                height, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, surface,
                null, null)
    }

    fun stopScreenCapture() {
        if (virtualDisplay == null) return
        virtualDisplay?.release()
        virtualDisplay = null
    }

    fun tearDownMediaProjection() {
        if (mediaProjection != null) {
            mediaProjection?.stop()
            mediaProjection = null
        }
    }
}
