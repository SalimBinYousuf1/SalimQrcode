package com.salim.qr.domain.safety

import android.net.Uri
import java.util.Locale

sealed class SafetyVerdict {
    object Safe : SafetyVerdict()
    data class Warning(val reasonTitle: String, val reasonDesc: String) : SafetyVerdict()
    data class Blocked(val reasonTitle: String, val reasonDesc: String) : SafetyVerdict()
}

object LinkSafetyChecker {

    private val BLOCKED_SCHEMES = setOf(
        "javascript", "file", "intent", "content", "data", "about", "chrome", "app"
    )

    private val SHORTENER_DOMAINS = setOf(
        "bit.ly", "tinyurl.com", "t.co", "is.gd", "buff.ly", "ow.ly", "goo.gl", "rebrand.ly", "cutt.ly"
    )

    private val IP_REGEX = Regex("^(\\d{1,3}\\.){3}\\d{1,3}$")

    fun evaluate(rawUrl: String): SafetyVerdict {
        val trimmed = rawUrl.trim()
        val scheme = if (trimmed.contains(":")) {
            trimmed.substringBefore(":").lowercase(Locale.ROOT)
        } else ""

        // Block dangerous schemes completely
        if (BLOCKED_SCHEMES.contains(scheme)) {
            return SafetyVerdict.Blocked(
                reasonTitle = "Blocked execution scheme",
                reasonDesc = "The link uses a '${scheme}:' prefix which could execute unauthorized scripts or access local files."
            )
        }

        val withoutScheme = if (trimmed.contains("://")) {
            trimmed.substringAfter("://")
        } else if (trimmed.contains(":")) {
            trimmed.substringAfter(":")
        } else trimmed

        val host = withoutScheme.substringBefore("/").substringBefore("?").substringBefore("#").substringBefore(":").lowercase(Locale.ROOT)

        // Suspicious IP address host
        if (IP_REGEX.matches(host)) {
            return SafetyVerdict.Warning(
                reasonTitle = "IP address destination",
                reasonDesc = "This link points directly to a raw IP address ($host) rather than a verified domain name."
            )
        }

        // URL Shortener
        if (SHORTENER_DOMAINS.contains(host)) {
            return SafetyVerdict.Warning(
                reasonTitle = "Shortened link detected",
                reasonDesc = "The link is wrapped using a URL shortener service ($host) which conceals the actual target destination."
            )
        }

        // Punycode or look-alike domain
        if (host.startsWith("xn--") || host.contains("..")) {
            return SafetyVerdict.Warning(
                reasonTitle = "Possible spoofed domain",
                reasonDesc = "The domain name contains internationalized punycode characters that can mimic standard brand names."
            )
        }

        // Plain HTTP (unencrypted)
        if (scheme == "http") {
            return SafetyVerdict.Warning(
                reasonTitle = "Unencrypted connection",
                reasonDesc = "This website uses HTTP instead of secure HTTPS. Sensitive information may not be encrypted in transit."
            )
        }

        return SafetyVerdict.Safe
    }
}
