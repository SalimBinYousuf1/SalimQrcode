package com.salim.qr.domain.encoding

import android.graphics.Bitmap
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

data class ScannabilityResult(
    val isValid: Boolean,
    val decodedPayload: String?,
    val warningMessage: String?
)

object ScannabilityChecker {

    fun verify(bitmap: Bitmap, expectedPayload: String): ScannabilityResult {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        return try {
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            val matches = result.text == expectedPayload
            ScannabilityResult(
                isValid = matches,
                decodedPayload = result.text,
                warningMessage = if (!matches) "Decoded text did not strictly match source payload." else null
            )
        } catch (_: Exception) {
            ScannabilityResult(
                isValid = false,
                decodedPayload = null,
                warningMessage = "Code may have insufficient contrast or overly complex customization for cameras to scan reliably."
            )
        }
    }
}
