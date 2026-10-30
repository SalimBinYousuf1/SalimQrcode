package com.salim.qr.domain.encoding

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

data class QrStyle(
    val errorCorrection: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
    val moduleShape: ModuleShape = ModuleShape.SQUARE,
    val eyeShape: EyeShape = EyeShape.SQUARE,
    val foregroundColor: Int = 0xFF1E2347.toInt(),
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val withCenterLogo: Boolean = false
)

enum class ModuleShape { SQUARE, ROUNDED, DOTS }
enum class EyeShape { SQUARE, ROUNDED }

object BarcodeEncoder {

    fun encode(
        payload: String,
        format: BarcodeFormat = BarcodeFormat.QR_CODE,
        width: Int = 512,
        height: Int = 512,
        style: QrStyle = QrStyle(),
        logoBitmap: Bitmap? = null
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, 1)
            if (format == BarcodeFormat.QR_CODE) {
                put(EncodeHintType.ERROR_CORRECTION, if (style.withCenterLogo) ErrorCorrectionLevel.H else style.errorCorrection)
            }
        }

        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(payload, format, width, height, hints)

        val bmWidth = bitMatrix.width
        val bmHeight = bitMatrix.height
        val bitmap = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888)

        if (format != BarcodeFormat.QR_CODE || style.moduleShape == ModuleShape.SQUARE) {
            val pixels = IntArray(bmWidth * bmHeight)
            for (y in 0 until bmHeight) {
                val offset = y * bmWidth
                for (x in 0 until bmWidth) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) style.foregroundColor else style.backgroundColor
                }
            }
            bitmap.setPixels(pixels, 0, bmWidth, 0, 0, bmWidth, bmHeight)
        } else {
            val canvas = Canvas(bitmap)
            canvas.drawColor(style.backgroundColor)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = style.foregroundColor
                setStyle(Paint.Style.FILL)
            }
            // Custom module shapes (Dots or Rounded)
            val moduleWidth = bmWidth.toFloat() / bitMatrix.width
            val moduleHeight = bmHeight.toFloat() / bitMatrix.height

            for (x in 0 until bitMatrix.width) {
                for (y in 0 until bitMatrix.height) {
                    if (bitMatrix.get(x, y)) {
                        val left = x * moduleWidth
                        val top = y * moduleHeight
                        val right = left + moduleWidth
                        val bottom = top + moduleHeight

                        when (style.moduleShape) {
                            ModuleShape.DOTS -> {
                                val radius = (moduleWidth / 2f) * 0.85f
                                canvas.drawCircle(left + moduleWidth / 2f, top + moduleHeight / 2f, radius, paint)
                            }
                            ModuleShape.ROUNDED -> {
                                val radius = moduleWidth * 0.35f
                                canvas.drawRoundRect(RectF(left, top, right, bottom), radius, radius, paint)
                            }
                            else -> {
                                canvas.drawRect(left, top, right, bottom, paint)
                            }
                        }
                    }
                }
            }
        }

        // Draw center logo if enabled
        if (format == BarcodeFormat.QR_CODE && style.withCenterLogo && logoBitmap != null) {
            val logoCanvas = Canvas(bitmap)
            drawCenterLogo(logoCanvas, bmWidth, bmHeight, logoBitmap)
        }

        return bitmap
    }

    private fun drawCenterLogo(canvas: Canvas, qrWidth: Int, qrHeight: Int, logo: Bitmap) {
        val logoSize = (qrWidth * 0.22f).toInt()
        val left = (qrWidth - logoSize) / 2f
        val top = (qrHeight - logoSize) / 2f

        // Draw white rounded plate behind logo for scannability
        val platePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFFFFF.toInt()
            setStyle(Paint.Style.FILL)
        }
        val plateMargin = 6f
        val plateRadius = 14f
        canvas.drawRoundRect(
            RectF(left - plateMargin, top - plateMargin, left + logoSize + plateMargin, top + logoSize + plateMargin),
            plateRadius,
            plateRadius,
            platePaint
        )

        val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
        canvas.drawBitmap(scaledLogo, left, top, null)
    }

    fun generateSvg(payload: String, format: BarcodeFormat = BarcodeFormat.QR_CODE, size: Int = 512): String {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, 1)
        }
        val bitMatrix = MultiFormatWriter().encode(payload, format, size, size, hints)
        val sb = StringBuilder()
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"$size\" height=\"$size\" viewBox=\"0 0 $size $size\">\n")
        sb.append("<rect width=\"100%\" height=\"100%\" fill=\"#FFFFFF\"/>\n")
        val cellW = size.toFloat() / bitMatrix.width
        val cellH = size.toFloat() / bitMatrix.height
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                if (bitMatrix.get(x, y)) {
                    val px = x * cellW
                    val py = y * cellH
                    sb.append("<rect x=\"$px\" y=\"$py\" width=\"$cellW\" height=\"$cellH\" fill=\"#1E2347\"/>\n")
                }
            }
        }
        sb.append("</svg>")
        return sb.toString()
    }
}
