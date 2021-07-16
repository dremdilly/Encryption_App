package com.daurenbek.encryptionapp

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object FileCrypter {
    private val TAG = "FileCrypter"
    private const val ALGORITHM = "AES"
    private var key: SecretKey? = null
    private var salt = "A8768CC5BEAA6093"
    private const val TEMP_FILE_TAG = "temp_"

    init {
        key = getKey()
    }

    fun getDecryptedFileIfExists(originalFilePath: String): Pair<Boolean, String> {
        val filePath = FileCrypter.getFileParentPath(originalFilePath)
        val fileName = FileCrypter.getFileNameFromPath(originalFilePath)
        val file = File(filePath, "$TEMP_FILE_TAG$fileName")

        return if (file.exists()) {
            Pair(true, file.path)
        } else {
            Pair(false, file.path)
        }
    }

    fun encryptFile(originalFilePath: String) {
        FileUtils.createDirIfNotExists("/storage/emulated/0/Download/encrypted")
        val encryptedFilePath = createCopyOfOriginalFile(originalFilePath, true)
        val fis = FileInputStream(originalFilePath)
        val aes = Cipher.getInstance(ALGORITHM)
        aes.init(Cipher.ENCRYPT_MODE, key)
        val fs = FileOutputStream(File(encryptedFilePath))
        val out = CipherOutputStream(fs, aes)
        out.write(fis.readBytes())
        out.flush()
        out.close()

        renameFileToOriginalFileName(encryptedFilePath)
    }

    fun decryptFile(originalFilePath: String) {
        FileUtils.createDirIfNotExists("/storage/emulated/0/Download/decrypted")
        val decryptedFilePath = createCopyOfOriginalFile(originalFilePath, false)
        val filePath = getFileParentPath(originalFilePath)
        val fileName = getFileNameFromPath(originalFilePath)
        val fis = FileInputStream("$filePath/$fileName")
        val aes = Cipher.getInstance(ALGORITHM)
        aes.init(Cipher.DECRYPT_MODE, key)
        val out = CipherInputStream(fis, aes)
        File(decryptedFilePath).outputStream().use {
            out.copyTo(it)
        }

        renameFileToOriginalFileName(decryptedFilePath)
    }

    private fun renameFileToOriginalFileName(path: String): String {
        val filePath = FileCrypter.getFileParentPath(path)
        val fileName = FileCrypter.getFileNameFromPath(path)

        val from = File(filePath, fileName!!)

        val renameTo = fileName.replace(TEMP_FILE_TAG, "")

        val to = File(filePath, renameTo)
        if (from.exists())
            from.renameTo(to)

        return to.path
    }

    private fun createCopyOfOriginalFile(originalFilePath: String, isEncryption: Boolean): String {
        val filePath = FileCrypter.getFileParentPath(originalFilePath)
        val fileName = FileCrypter.getFileNameFromPath(originalFilePath)
        val originalFile = File(originalFilePath)
        val copyFile: File?
        if (isEncryption) {
            copyFile = File("/storage/emulated/0/Download/encrypted", "$TEMP_FILE_TAG$fileName")
        }
        else {
            copyFile = File("/storage/emulated/0/Download/decrypted", "$TEMP_FILE_TAG$fileName")
        }

        try {
            com.daurenbek.encryptionapp.FileUtils.copy(originalFile, copyFile)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return copyFile.path
    }

    private fun deleteFile(path: String) {
        val file = File(path)

        if (file.exists())
            file.delete()
    }

    private fun getFileParentPath(path: String?): String? {
        var newPath = ""
        path?.let {
            newPath = it.substring(0, it.lastIndexOf("/") + 1)
        }
        return newPath
    }

    private fun getFileNameFromPath(path: String?): String? {
        var newPath = ""
        path?.let {
            newPath = it.substring(it.lastIndexOf("/") + 1)
        }
        return newPath
    }

    private fun getKey(): SecretKey? {
        var secretKey: SecretKey? = null

        try {
            secretKey = SecretKeySpec(salt.toBytes(), ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return secretKey
    }

    private fun String.toBytes(): ByteArray {
        return this.toByteArray(Charsets.UTF_8)
    }
}
