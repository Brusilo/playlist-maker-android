package com.example.playlist_maker_android_brusilodiana.data


import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    fun saveImageToAppStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                val fileName = generateFileName()
                val outputFile = File(context.filesDir, fileName)

                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }

                val savedUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        outputFile
                    )
                } else {
                    Uri.fromFile(outputFile)
                }

                savedUri.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "playlist_cover_$timeStamp.jpg"
    }

    fun getImageFileFromUri(context: Context, uriString: String?): File? {
        return if (uriString.isNullOrEmpty()) {
            null
        } else {
            try {
                val uri = Uri.parse(uriString)
                if (uri.scheme == "file") {
                    File(uri.path ?: return null)
                } else if (uri.scheme == "content") {
                    val fileName = generateFileName()
                    val outputFile = File(context.filesDir, fileName)

                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    inputStream?.use { input ->
                        FileOutputStream(outputFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                    outputFile
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}