package com.salim.qr.domain.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.salim.qr.data.db.CodeRecord
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CodeExporter {

    fun saveToGallery(context: Context, bitmap: Bitmap, title: String): Uri? {
        val filename = "Salim_${System.currentTimeMillis()}.png"
        val outputStream: OutputStream?
        val imageUri: Uri?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Salim")
            }
            val resolver = context.contentResolver
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Salim"
            val file = File(imagesDir)
            if (!file.exists()) file.mkdirs()
            val imageFile = File(file, filename)
            outputStream = FileOutputStream(imageFile)
            imageUri = Uri.fromFile(imageFile)
        }

        outputStream?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        return imageUri
    }

    fun shareBitmap(context: Context, bitmap: Bitmap, textDescription: String) {
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "salim_code_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, textDescription)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share QR Code"))
    }

    fun copyImageToClipboard(context: Context, bitmap: Bitmap) {
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "clipboard_qr.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newUri(context.contentResolver, "Salim QR Code", uri)
        clipboard.setPrimaryClip(clip)
    }

    fun copyTextToClipboard(context: Context, label: String, text: String, isSensitive: Boolean = false) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        if (isSensitive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            clip.description.extras = android.os.PersistableBundle().apply {
                putBoolean("android.content.extra.IS_SENSITIVE", true)
            }
        }
        clipboard.setPrimaryClip(clip)
    }

    fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share"))
    }

    fun exportSvgFile(context: Context, svgContent: String, title: String) {
        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val file = File(cachePath, "salim_${System.currentTimeMillis()}.svg")
        file.writeText(svgContent)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/svg+xml"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$title.svg")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export SVG"))
    }

    fun exportPdfFile(context: Context, bitmap: Bitmap, title: String, payload: String) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Draw header
        val headerPaint = Paint().apply {
            color = Color.parseColor("#1E2347")
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText("Salim QR Code", 50f, 80f, headerPaint)

        val subPaint = Paint().apply {
            color = Color.parseColor("#6F7391")
            textSize = 14f
            isAntiAlias = true
        }
        val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generated on $dateStr", 50f, 105f, subPaint)

        // Draw QR Image centered
        val qrSize = 340
        val left = (595 - qrSize) / 2f
        val top = 160f
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, qrSize, qrSize, true)
        canvas.drawBitmap(scaledBitmap, left, top, null)

        // Draw payload preview
        val payloadPaint = Paint().apply {
            color = Color.parseColor("#1E2347")
            textSize = 12f
            isAntiAlias = true
        }
        val wrappedPayload = if (payload.length > 70) payload.take(67) + "..." else payload
        canvas.drawText("Content: $wrappedPayload", 50f, 540f, payloadPaint)

        // Footer
        val footerPaint = Paint().apply {
            color = Color.parseColor("#B9BDE0")
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText("Created with Salim QR - 100% Offline & Private", 50f, 780f, footerPaint)

        document.finishPage(page)

        val cachePath = File(context.cacheDir, "shared_images")
        cachePath.mkdirs()
        val pdfFile = File(cachePath, "salim_${System.currentTimeMillis()}.pdf")
        FileOutputStream(pdfFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "$title.pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export PDF"))
    }

    fun serializeRecordsToJson(records: List<CodeRecord>): String {
        val root = JSONObject()
        root.put("app", "Salim")
        root.put("version", "1.0.0")
        root.put("exportTime", System.currentTimeMillis())

        val array = JSONArray()
        for (r in records) {
            val item = JSONObject()
            item.put("id", r.id)
            item.put("origin", r.origin)
            item.put("payload", r.payload)
            item.put("format", r.format)
            item.put("parsedType", r.parsedType)
            item.put("title", r.title)
            item.put("timestamp", r.timestamp)
            item.put("favorite", r.favorite)
            array.put(item)
        }
        root.put("records", array)
        return root.toString(2)
    }

    fun parseRecordsFromJson(jsonString: String): List<CodeRecord> {
        val list = mutableListOf<CodeRecord>()
        try {
            val root = JSONObject(jsonString)
            val array = root.optJSONArray("records") ?: return emptyList()
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                list.add(
                    CodeRecord(
                        origin = item.optString("origin", "scanned"),
                        payload = item.getString("payload"),
                        format = item.optString("format", "QR_CODE"),
                        parsedType = item.optString("parsedType", "Website"),
                        title = item.optString("title", ""),
                        timestamp = item.optLong("timestamp", System.currentTimeMillis()),
                        favorite = item.optBoolean("favorite", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
