package com.daurenbek.encryptionapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.daurenbek.encryptionapp.databinding.ActivityMainBinding
import java.io.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val fileChooserToEncrypt =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            chooseFileToEncrypt(result)
        }
    private val fileChooserToDecrypt =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            chooseFileToDecrypt(result)
        }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        }
    private lateinit var binding: ActivityMainBinding
    private val loadingDialog = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkForPermissions()

        binding.encryptButton.setOnClickListener {
            checkForPermissions()
            encryptFile()
        }
        binding.decryptButton.setOnClickListener {
            checkForPermissions()
            decryptFile()
        }

    }

    private fun encryptFile() {
        openFileChooserToEncrypt()
    }

    private fun decryptFile() {
        openFileChooserToDecrypt()
    }

    private fun openFileChooserToEncrypt() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        fileChooserToEncrypt.launch(intent)
    }

    private fun openFileChooserToDecrypt() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        fileChooserToDecrypt.launch(intent)
    }

    private fun chooseFileToEncrypt(result: ActivityResult) {
        Thread {
            if (result.resultCode == Activity.RESULT_OK) {
                runOnUiThread {
                    loadingDialog.startLoadingDialog()
                }
                val data: Intent? = result.data
                val filepath = data?.data!!
                var fileName = ""
                filepath.let { uri ->
                    contentResolver.query(uri, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    fileName = cursor.getString(nameIndex)
                }
                val inputStream: InputStream? = contentResolver.openInputStream(filepath)
                val file = File(cacheDir.absolutePath + "/" + fileName)
                inputStream?.let { writeFile(it, file) }
                val originalFilePath = file.path
                var newOriginalFilePath: String
                originalFilePath.let {
                    newOriginalFilePath = it.substring(it.lastIndexOf(":") + 1)
                }
                FileCrypter.encryptFile(newOriginalFilePath)
                runOnUiThread {
                    loadingDialog.dismissDialog()
                }
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Successfully encrypted in\n" + Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        ).path + "/encrypted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }.start()
}

private fun chooseFileToDecrypt(result: ActivityResult) {
    Thread {
        if (result.resultCode == Activity.RESULT_OK) {
            runOnUiThread {
                loadingDialog.startLoadingDialog()
            }
            val data: Intent? = result.data
            val filepath = data?.data!!
            var fileName = ""
            filepath.let { uri ->
                contentResolver.query(uri, null, null, null, null)
            }?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                fileName = cursor.getString(nameIndex)
            }
            val inputStream: InputStream? = contentResolver.openInputStream(filepath)
            val file = File(cacheDir.absolutePath + "/" + fileName)
            inputStream?.let { writeFile(it, file) }
            val originalFilePath = file.path
            var newOriginalFilePath: String
            originalFilePath.let {
                newOriginalFilePath = it.substring(it.lastIndexOf(":") + 1)
            }
            FileCrypter.decryptFile(newOriginalFilePath)
            runOnUiThread {
                loadingDialog.dismissDialog()
            }
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    "Successfully decrypted in\n" + Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ).path + "/decrypted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }.start()
}

private fun checkForPermissions(): Boolean {
    if (ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
    ) {
        permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    if (ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
    ) {
        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    return false
}

fun writeFile(`in`: InputStream, file: File?) {
    var out: OutputStream? = null
    try {
        out = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (out != null) {
                out.close()
            }
            `in`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
}