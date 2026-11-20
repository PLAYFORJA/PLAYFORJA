package com.example.data.local

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object MediaStorageHelper {
    fun persistPickedImage(context: Context, uri: Uri): String {
        val mediaDir = File(context.filesDir, "playforja_media")
        val outFile = File(mediaDir, "media_${UUID.randomUUID()}.jpg")

        return try {
            if (!mediaDir.exists() && !mediaDir.mkdirs()) {
                return uri.toString()
            }

            val input = context.contentResolver.openInputStream(uri)
                ?: return uri.toString()

            input.use { source ->
                outFile.outputStream().use { output ->
                    source.copyTo(output)
                }
            }

            Uri.fromFile(outFile).toString()
        } catch (_: Exception) {
            // Never return a file:// URI for a file that may be empty or partially written.
            outFile.delete()
            uri.toString()
        }
    }
}
