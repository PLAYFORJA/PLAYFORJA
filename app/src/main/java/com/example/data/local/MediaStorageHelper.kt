package com.example.data.local

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object MediaStorageHelper {
    fun persistPickedImage(context: Context, uri: Uri): String {
        return try {
            val mediaDir = File(context.filesDir, "playforja_media")
            if (!mediaDir.exists()) mediaDir.mkdirs()
            val outFile = File(mediaDir, "media_${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(outFile).toString()
        } catch (_: Exception) {
            uri.toString()
        }
    }
}
