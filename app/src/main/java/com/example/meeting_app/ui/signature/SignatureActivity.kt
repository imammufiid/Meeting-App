package com.example.meeting_app.ui.signature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.meeting_app.databinding.ActivitySignatureBinding

class SignatureActivity : AppCompatActivity() {
    private lateinit var _bind: ActivitySignatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bind = ActivitySignatureBinding.inflate(layoutInflater)
        setContentView(_bind.root)
    }
}