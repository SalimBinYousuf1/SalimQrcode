package com.salim.qr

import com.salim.qr.domain.parsing.ParsedCode
import com.salim.qr.domain.parsing.PayloadParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PayloadParserTest {

    @Test
    fun testParseUrl() {
        val result = PayloadParser.parse("https://salim.app/welcome")
        assertTrue(result is ParsedCode.Website)
        assertEquals("salim.app", (result as ParsedCode.Website).host)
    }

    @Test
    fun testParseWifiEscaped() {
        val wifiPayload = "WIFI:T:WPA;S:Home\\;Net;P:Secret\\:Pass123;H:false;;"
        val result = PayloadParser.parse(wifiPayload)
        assertTrue(result is ParsedCode.Wifi)
        val wifi = result as ParsedCode.Wifi
        assertEquals("Home;Net", wifi.ssid)
        assertEquals("Secret:Pass123", wifi.password)
        assertEquals("WPA", wifi.security)
    }

    @Test
    fun testParseMailto() {
        val mailto = "mailto:support@salim.app?subject=Help&body=Hello"
        val result = PayloadParser.parse(mailto)
        assertTrue(result is ParsedCode.Email)
        val email = result as ParsedCode.Email
        assertEquals("support@salim.app", email.address)
        assertEquals("Help", email.subject)
    }

    @Test
    fun testParseSms() {
        val sms = "smsto:+1234567890:Hello Salim"
        val result = PayloadParser.parse(sms)
        assertTrue(result is ParsedCode.Sms)
        val smsCode = result as ParsedCode.Sms
        assertEquals("+1234567890", smsCode.phoneNumber)
        assertEquals("Hello Salim", smsCode.message)
    }

    @Test
    fun testParseTel() {
        val tel = "tel:+1987654321"
        val result = PayloadParser.parse(tel)
        assertTrue(result is ParsedCode.Phone)
        assertEquals("+1987654321", (result as ParsedCode.Phone).phoneNumber)
    }

    @Test
    fun testParsePlainText() {
        val text = "Just a simple offline note"
        val result = PayloadParser.parse(text)
        assertTrue(result is ParsedCode.Text)
        assertEquals(text, (result as ParsedCode.Text).text)
    }
}
