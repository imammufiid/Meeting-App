package com.example.meeting_app.ui.scanner

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import com.example.meeting_app.data.entity.EventEntity
import com.example.meeting_app.databinding.ActivityScannerBinding
import com.example.meeting_app.ui.detail.DetailActivity
import com.example.meeting_app.ui.home.HomeActivity
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref

class ScannerActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityScannerBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var viewModel: ScannerViewModel
    private lateinit var loading: ProgressDialog
    private var activity: String? = null

    companion object {
        const val EXTRAS_ACTIVITY = "extras_activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        init()
        scanner()
        setParcelable()
    }

    private fun init() {
        loading = ProgressDialog(this)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ScannerViewModel::class.java]
        viewModel.getState().observer(this, androidx.lifecycle.Observer {
            handlerUIState(it)
        })
    }

    private fun setParcelable() {
        activity = intent.getStringExtra(EXTRAS_ACTIVITY)
    }

    private fun scanner() {
        codeScanner = CodeScanner(this, _binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE) // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                when(activity) {
                    HomeActivity.ACTIVITY_NAME -> {
                        viewModel.scan(
                            UserPref.getUserData(this)?.token,
                            UserPref.getUserData(this)?.id,
                            it.text
                        )
                    }
                    DetailActivity.ACTIVITY_NAME -> {
                        viewModel.registration(
                            UserPref.getUserData(this)?.token,
                            UserPref.getUserData(this)?.id,
                            it.text
                        )
                    }
                }


            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        _binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun handlerUIState(it: ScannerState?) {
        when (it) {
            is ScannerState.IsSuccess -> isSuccess(it.status, it.msg, it.data)
            is ScannerState.Error -> showToast(it.err, false)
            is ScannerState.IsLoading -> showLoading(it.state)
        }
    }

    private fun isSuccess(status: Int?, msg: String?, data: EventEntity?) {
         when(status) {
             404 -> {
                 // go to registration
                 showToast(msg, false)
                 Handler(mainLooper).postDelayed({
                     startActivity(Intent(this, DetailActivity::class.java).apply {
                         putExtra(DetailActivity.EXTRAS_DATA, data)
                     })
                     finish()
                 }, 1000)
             }
             200 -> {
                 // goes to come
                 showToast(msg, true)
                 Handler(mainLooper).postDelayed({
                     finish()
                 }, 2000)
             }

        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            loading.setMessage("Loading...")
            loading.setCanceledOnTouchOutside(false)
            loading.show()
        } else {
            loading.dismiss()
        }
    }

    private fun showToast(message: String?, state: Boolean? = true) {
        state?.let { isSuccess ->
            if (isSuccess) {
                CustomView.customToast(this, message, true, isSuccess = true)
            } else {
                CustomView.customToast(this, message, true, isSuccess = false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}