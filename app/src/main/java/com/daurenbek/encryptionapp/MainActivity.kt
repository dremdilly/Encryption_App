package com.daurenbek.encryptionapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.daurenbek.encryptionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val chooser = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        startChooser(result, 111)
    }
    lateinit var binding: ActivityMainBinding
    lateinit var filepath: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener { encryptFile() }
        binding.decryptButton.setOnClickListener { decryptFile() }
    }

    private fun encryptFile() {
        openFileChooser()
    }

    private fun decryptFile() {
        openFileChooser()
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        chooser.launch(Intent.createChooser(intent, "Choose File"))

    }

    private fun startChooser(result: ActivityResult, resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            filepath = data?.data!!
        }
    }
}