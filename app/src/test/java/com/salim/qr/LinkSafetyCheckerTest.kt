package com.salim.qr

import com.salim.qr.domain.safety.LinkSafetyChecker
import com.salim.qr.domain.safety.SafetyVerdict
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkSafetyCheckerTest {

    @Test
    fun testBlockedScheme() {
        val verdict = LinkSafetyChecker.evaluate("javascript:alert(1)")
        assertTrue(verdict is SafetyVerdict.Blocked)
    }

    @Test
    fun testFileSchemeBlocked() {
        val verdict = LinkSafetyChecker.evaluate("file:///etc/passwd")
        assertTrue(verdict is SafetyVerdict.Blocked)
    }

    @Test
    fun testIpAddressWarning() {
        val verdict = LinkSafetyChecker.evaluate("http://192.168.1.1/admin")
        assertTrue(verdict is SafetyVerdict.Warning)
    }

    @Test
    fun testShortenerWarning() {
        val verdict = LinkSafetyChecker.evaluate("https://bit.ly/3xyz")
        assertTrue(verdict is SafetyVerdict.Warning)
    }

    @Test
    fun testSafeHttpsUrl() {
        val verdict = LinkSafetyChecker.evaluate("https://salim.app")
        assertTrue(verdict is SafetyVerdict.Safe)
    }
}
