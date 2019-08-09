package com.levandowski.androidstream

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_screen_capture.*

class ScreenCaptureActivity : AppCompatActivity() {
    private val STATE_RESULT_CODE = "result_code"
    private val STATE_RESULT_DATA = "result_data"

    private val REQUEST_MEDIA_PROJECTION = 39
    private var mResultCode: Int = 0
    private var mResultData: Intent? = null

    private var screenCaptureManager: ScreenCaptureManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_capture)

        screenCaptureManager = ScreenCaptureManager(this, surface)
    }

    override fun onResume() {
        super.onResume()

        button.setOnClickListener {
            message_main.visibility = View.GONE
            screenCaptureManager?.startIfNeeded(REQUEST_MEDIA_PROJECTION, mResultCode, mResultData)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        if (mResultData != null) {
            outState?.putInt(STATE_RESULT_CODE, mResultCode)
            outState?.putParcelable(STATE_RESULT_DATA, mResultData)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "Captura cancelada", Toast.LENGTH_SHORT).show()
                return
            }

            mResultCode = resultCode
            mResultData = data
            screenCaptureManager?.setUpMediaProjection(mResultCode, mResultData)
            screenCaptureManager?.setUpVirtualDisplay()
        }
    }

    override fun onPause() {
        super.onPause()
        screenCaptureManager?.stopScreenCapture()
    }

    override fun onDestroy() {
        super.onDestroy()
        screenCaptureManager?.tearDownMediaProjection()
    }
}
