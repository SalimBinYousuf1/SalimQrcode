package com.salim.qr

import com.google.zxing.BarcodeFormat
import com.salim.qr.domain.encoding.BarcodeEncoder
import com.salim.qr.domain.encoding.QrStyle
import com.salim.qr.domain.encoding.ScannabilityChecker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BarcodeEncodingTest {

    @Test
    fun testQrEncodingAndSelfCheck() {
        val payload = "https://salim.app"
        val bitmap = BarcodeEncoder.encode(payload, BarcodeFormat.QR_CODE, 256, 256, QrStyle())
        assertNotNull(bitmap)
        assertEquals(256, bitmap.width)
        assertEquals(256, bitmap.height)

        val check = ScannabilityChecker.verify(bitmap, payload)
        assertTrue(check.isValid)
        assertEquals(payload, check.decodedPayload)
    }

    @Test
    fun testSvgGeneration() {
        val payload = "SALIM-TEST-PAYLOAD"
        val svg = BarcodeEncoder.generateSvg(payload, BarcodeFormat.QR_CODE, 300)
        assertTrue(svg.contains("<svg"))
        assertTrue(svg.contains("</svg>"))
    }
}
