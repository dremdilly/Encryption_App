package com.daurenbek.encryptionapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.*

object FileUtils {

    private val TAG = "FileUtils"

    @Throws(IOException::class)
    fun copy(sourceLocation: File, targetLocation: File) {
        if (sourceLocation.isDirectory) {
            copyDirectory(sourceLocation, targetLocation)
        } else {
            copyFile(sourceLocation, targetLocation)
        }
    }

    @Throws(IOException::class)
    private fun copyDirectory(source: File, target: File) {
        if (!target.exists()) {
            target.mkdir()
        }

        for (f in source.list()) {
            copy(File(source, f), File(target, f))
        }
    }

    @Throws(IOException::class, FileNotFoundException::class)
    private fun copyFile(source: File, target: File) {
        try {
            val inputStream = FileInputStream(source)
            val out = FileOutputStream(target)
            out.write(inputStream.readBytes())
            out.flush()
            out.close()
        } catch (e: IOException) {
            throw IOException()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(inputStream: InputStream, out: OutputStream) {
        out.write(inputStream.readBytes())
        out.flush()
        out.close()
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun save(file: File, data: ByteArray): File {

        try {
            FileOutputStream(file).use { os ->
                os.write(data)
                os.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Ignore
        return file
    }

    fun createDirIfNotExists(path: String): Boolean {
        var ret = true
        val file = File(path)
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ret = false
            }
        }
        return ret
    }

    fun deleteFile(path: String): Boolean {
        val f = File(path)

        return if (f.exists()) {
            f.delete()
        } else false
    }

    fun deleteFile(f: File): Boolean {

        return if (f.exists()) {
            f.delete()
        } else false

    }

    fun deleteDir(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles())
                deleteDir(child)
        fileOrDirectory.delete()
    }
}