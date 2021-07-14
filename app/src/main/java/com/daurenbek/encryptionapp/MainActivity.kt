package com.daurenbek.encryptionapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.daurenbek.encryptionapp.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {
    private val fileChooser =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            chooseFile(result)
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
        fileChooser.launch(intent)
    }

    private fun chooseFile(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val filepath = data?.data!!
            var fileName: String = ""
            filepath.let { uri ->
                contentResolver.query(uri, null, null, null, null)
            }?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                fileName = cursor.getString(nameIndex)
            }
            Toast.makeText(applicationContext, fileName, Toast.LENGTH_SHORT).show()
            val folder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val myFile = File(folder, fileName)
            val fis = FileInputStream(myFile)
        }
    }
}