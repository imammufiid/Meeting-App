package com.example.meeting_app.ui.signature

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.meeting_app.R
import com.example.meeting_app.databinding.FragmentSignatureBottomSheetBinding
import com.example.meeting_app.ui.detail.DetailActivity
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class SignatureBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {

    private lateinit var _bind : FragmentSignatureBottomSheetBinding
    private var buttonSignatureListener: ButtonSignatureListener? = null
    private var imagePathSignature: String? = ""
    companion object {
        const val TAG = "signature_bottom_sheet"
        const val IS_REPLY = "is_reply"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentSignatureBottomSheetBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bind.btnClear.setOnClickListener(this)
        _bind.btnSignin.setOnClickListener(this)
        _bind.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                Toast.makeText(context, "On start Signing", Toast.LENGTH_SHORT).show()
            }

            override fun onSigned() {
                _bind.btnSignin.isEnabled = true
                _bind.btnClear.isEnabled = true
            }

            override fun onClear() {
                _bind.btnSignin.isEnabled = false
                _bind.btnClear.isEnabled = false
            }
        })
    }

    private fun addJpgSignatureToGallery(signature: Bitmap?): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            Log.d("Photo_File", photo.toString())

            imagePathSignature = photo.toString()
            if (signature != null) {
                saveBitmapToJPG(signature, photo)
            }
//            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun getAlbumStorageDir(albumName: String?): File? {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0F, 0F, null)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

//    private fun scanMediaFile(photo: File) {
//        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        val contentUri: Uri = Uri.fromFile(photo)
//        mediaScanIntent.data = contentUri
//        this.sendBroadcast(mediaScanIntent)
//    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_clear -> {
                _bind.signaturePad.clear()
            }
            R.id.btn_signin -> {
                if (buttonSignatureListener != null) {
                    val signatureBitmap = _bind.signaturePad.signatureBitmap
                    if (addJpgSignatureToGallery(signatureBitmap)) {
                        Toast.makeText(context, "Signature saved into the Gallery", Toast.LENGTH_SHORT)
                            .show()
                        buttonSignatureListener?.signIn()
                        dialog?.dismiss()
                    } else {
                        Toast.makeText(context, "Unable to store the signature", Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is DetailActivity) {
            this.buttonSignatureListener = (activity as DetailActivity).buttonSignatureListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.buttonSignatureListener = null
    }

    interface ButtonSignatureListener {
        fun signIn(pathSignature: String)
    }
}