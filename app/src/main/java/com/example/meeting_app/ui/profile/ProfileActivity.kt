package com.example.meeting_app.ui.profile

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.meeting_app.R
import com.example.meeting_app.data.entity.UserEntity
import com.example.meeting_app.databinding.ActivityProfileBinding
import com.example.meeting_app.utils.helper.CustomView
import com.example.meeting_app.utils.pref.UserPref
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var loading: ProgressDialog

    private var imageFromApi: String? = null
    private var currentPhotoPath: String? = null
    private var part: MultipartBody.Part? = null

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        init()
        checkPermission()
        setProfile()
        observeViewModel()
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        finish()
        return true
    }

    private fun init() {
        supportActionBar?.title = getString(R.string.title_profile)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ProfileViewModel::class.java]

        loading = ProgressDialog(this)
        _binding.btnEditProfile.setOnClickListener(this)
        _binding.imgPhoto.setOnClickListener(this)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    private fun openGalleryWithPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                pickFromGallery()
            }
        } else {
            pickFromGallery()
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = uri?.let { uri ->
                contentResolver.query(uri, filePath, null, null, null)
            }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePath[0])
            val picturePath = columnIndex?.let { columnIndex -> cursor?.getString(columnIndex) }
            cursor?.close()
            currentPhotoPath = picturePath.toString()
            _binding.imgPhoto.apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageURI(uri)
            }
        }
    }


    private fun observeViewModel() {
        viewModel.getState().observer(this, {
            handlerUIState(it)
        })

        viewModel.getUser().observe(this, {
            if (it != null) {
                _binding.etUsername.setText(it.nama)
                _binding.etEmail.setText(it.email)
                _binding.etNip.setText(it.nip)
                _binding.etInstance.setText(it.instansi)
                _binding.etAddressInstance.setText(it.alamatInstansi)
                _binding.etJabatan.setText(it.jabatan)
                _binding.etPangkat.setText(it.pangkat)
                _binding.etTelpInstansi.setText(it.teleponInstansi)
                _binding.etFaxInstansi.setText(it.faxInstansi)

                imageFromApi = it.image
                Glide.with(this)
                    .load(it.image)
                    .centerCrop()
                    .into(_binding.imgPhoto)
            }
        })
    }

    private fun setProfile() {
        val userData = UserPref.getUserData(this)
        viewModel.getUser(userData.idUser)
    }


    private fun handlerUIState(it: ProfileState?) {
        when (it) {
            is ProfileState.IsLoading -> showLoading(it.state)
            is ProfileState.Error -> showToast(it.err, false)
            is ProfileState.IsSuccess -> isSuccess(it.message, data = it.data)
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

    private fun showToast(message: String?, isSuccess: Boolean? = true) {
        isSuccess?.let { yes ->
            if (yes) {
                CustomView.customToast(this, message, true, isSuccess = true)
            } else {
                CustomView.customToast(this, message, true, isSuccess = false)
            }
        }
    }

    private fun isSuccess(message: String?, state: Boolean? = true, data: UserEntity?) {
        state?.let { isSuccess ->
            if (isSuccess) {
                CustomView.customToast(this, message, true, isSuccess = true)
                _binding.etUsername.setText(data?.nama)
                _binding.etPassword.setText("")
            } else {
                CustomView.customToast(this, message, true, isSuccess = false)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_photo -> openGalleryWithPermission()
            R.id.btn_edit_profile -> {
                val username = _binding.etUsername.text.toString()
                val nip = _binding.etNip.text.toString()
                val jabatan = _binding.etJabatan.text.toString()
                val pangkat = _binding.etPangkat.text.toString()
                val instansi = _binding.etInstance.text.toString()
                val addressInstansi = _binding.etAddressInstance.text.toString()
                val telpInstansi = _binding.etTelpInstansi.text.toString()
                val faxInstansi = _binding.etFaxInstansi.text.toString()
                val password = _binding.etPassword.text.toString()
                if (username.isEmpty()) {
                    _binding.etUsername.error = "Field is required!"
                    return
                }

                if (nip.isEmpty()) {
                    _binding.etNip.error = "Field is required!"
                    return
                }
                if (jabatan.isEmpty()) {
                    _binding.etJabatan.error = "Field is required!"
                    return
                }
                if (pangkat.isEmpty()) {
                    _binding.etPangkat.error = "Field is required!"
                    return
                }
                if (instansi.isEmpty()) {
                    _binding.etInstance.error = "Field is required!"
                    return
                }
                if (addressInstansi.isEmpty()) {
                    _binding.etAddressInstance.error = "Field is required!"
                    return
                }
                if (telpInstansi.isEmpty()) {
                    _binding.etTelpInstansi.error = "Field is required!"
                    return
                }
                if (faxInstansi.isEmpty()) {
                    _binding.etFaxInstansi.error = "Field is required!"
                    return
                }

                val newUsername = username.toRequestBody("text/plain".toMediaTypeOrNull())
                val newNIP = nip.toRequestBody("text/plain".toMediaTypeOrNull())
                val newJabatan = jabatan.toRequestBody("text/plain".toMediaTypeOrNull())
                val newPangkat = pangkat.toRequestBody("text/plain".toMediaTypeOrNull())
                val newInstansi = instansi.toRequestBody("text/plain".toMediaTypeOrNull())
                val newAddressInstansi = addressInstansi.toRequestBody("text/plain".toMediaTypeOrNull())
                val newTelp = telpInstansi.toRequestBody("text/plain".toMediaTypeOrNull())
                val newFax = faxInstansi.toRequestBody("text/plain".toMediaTypeOrNull())
                val newPassword = password.toRequestBody("text/plain".toMediaTypeOrNull())

                if (currentPhotoPath != null) {
                    val pictFromBitmap = File(currentPhotoPath)
                    val reqFile = pictFromBitmap.asRequestBody("image/*".toMediaTypeOrNull())
                    part = MultipartBody.Part.createFormData("image", pictFromBitmap.name, reqFile)
                } else {
                    if (imageFromApi.isNullOrEmpty()) {
                        showToast("Image is Required!", false)
                        return
                    }
                }

                viewModel.editUser(
                    UserPref.getUserData(this).idUser, newUsername, newNIP, newJabatan,
                    newPangkat, newInstansi, newAddressInstansi, newTelp, newFax, newPassword, part
                )

            }
        }
    }
}