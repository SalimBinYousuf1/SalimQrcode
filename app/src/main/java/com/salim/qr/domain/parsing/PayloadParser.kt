package com.salim.qr.domain.parsing

import android.net.Uri
import java.util.Locale

object PayloadParser {

    fun parse(raw: String, format: String = "QR_CODE"): ParsedCode {
        val trimmed = raw.trim()

        if (format != "QR_CODE" && format != "DATA_MATRIX" && format != "AZTEC") {
            return ParsedCode.Barcode(trimmed, format)
        }

        val lower = trimmed.lowercase(Locale.ROOT)

        return when {
            // Wi-Fi
            trimmed.startsWith("WIFI:", ignoreCase = true) -> parseWifi(trimmed)

            // Email
            trimmed.startsWith("mailto:", ignoreCase = true) -> parseMailto(trimmed)

            // SMS
            trimmed.startsWith("smsto:", ignoreCase = true) || trimmed.startsWith("sms:", ignoreCase = true) -> parseSms(trimmed)

            // Phone
            trimmed.startsWith("tel:", ignoreCase = true) -> parseTel(trimmed)

            // Contact vCard / MECARD
            trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) || trimmed.startsWith("MECARD:", ignoreCase = true) -> parseContact(trimmed)

            // Location geo:
            trimmed.startsWith("geo:", ignoreCase = true) -> parseGeo(trimmed)

            // Event VEVENT
            trimmed.startsWith("BEGIN:VEVENT", ignoreCase = true) -> parseEvent(trimmed)

            // UPI Payment
            trimmed.startsWith("upi://pay", ignoreCase = true) -> parseUpi(trimmed)

            // Web URL
            trimmed.startsWith("http://", ignoreCase = true) ||
            trimmed.startsWith("https://", ignoreCase = true) ||
            isProbableUrl(trimmed) -> {
                val fullUrl = if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
                    "https://$trimmed"
                } else trimmed
                val withoutScheme = fullUrl.substringAfter("://")
                val host = withoutScheme.substringBefore("/").substringBefore("?").substringBefore("#").substringBefore(":")
                ParsedCode.Website(fullUrl, host)
            }

            else -> ParsedCode.Text(trimmed)
        }
    }

    private fun isProbableUrl(text: String): Boolean {
        if (text.contains(" ") || !text.contains(".")) return false
        val commonTlds = listOf(".com", ".org", ".net", ".io", ".co", ".app", ".dev", ".gov", ".edu", ".in", ".uk")
        return commonTlds.any { text.lowercase(Locale.ROOT).contains(it) }
    }

    private fun parseWifi(raw: String): ParsedCode.Wifi {
        // WIFI:S:MySSID;T:WPA;P:MyPassword;H:false;;
        var ssid = ""
        var pass = ""
        var type = "WPA"
        var hidden = false

        val body = raw.substringAfter("WIFI:").trimEnd(';')
        val tokens = splitEscaped(body, ';')
        for (token in tokens) {
            val idx = token.indexOf(':')
            if (idx > 0) {
                val key = token.substring(0, idx).uppercase(Locale.ROOT)
                val value = unescapeWifi(token.substring(idx + 1))
                when (key) {
                    "S" -> ssid = value
                    "P" -> pass = value
                    "T" -> type = value
                    "H" -> hidden = value.equals("true", ignoreCase = true)
                }
            }
        }
        return ParsedCode.Wifi(ssid, pass, type, hidden)
    }

    private fun unescapeWifi(value: String): String {
        return value.replace("\\;", ";")
            .replace("\\:", ":")
            .replace("\\,", ",")
            .replace("\\\\", "\\")
            .replace("\\\"", "\"")
    }

    private fun splitEscaped(str: String, delimiter: Char): List<String> {
        val list = mutableListOf<String>()
        val sb = StringBuilder()
        var escaped = false
        for (ch in str) {
            if (escaped) {
                sb.append(ch)
                escaped = false
            } else if (ch == '\\') {
                escaped = true
                sb.append(ch)
            } else if (ch == delimiter) {
                list.add(sb.toString())
                sb.clear()
            } else {
                sb.append(ch)
            }
        }
        if (sb.isNotEmpty()) list.add(sb.toString())
        return list
    }

    private fun parseMailto(raw: String): ParsedCode.Email {
        val clean = raw.removePrefix("mailto:").removePrefix("MAILTO:")
        val address = clean.substringBefore("?")
        var subject = ""
        var body = ""
        if (clean.contains("?")) {
            val query = clean.substringAfter("?")
            for (param in query.split("&")) {
                val parts = param.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].lowercase(Locale.ROOT)
                    val value = try {
                        java.net.URLDecoder.decode(parts[1], "UTF-8")
                    } catch (_: Exception) {
                        parts[1]
                    }
                    when (key) {
                        "subject" -> subject = value
                        "body" -> body = value
                    }
                }
            }
        }
        return ParsedCode.Email(address, subject, body)
    }

    private fun parseSms(raw: String): ParsedCode.Sms {
        val clean = if (raw.startsWith("smsto:", ignoreCase = true)) raw.substring(6) else raw.substring(4)
        val parts = clean.split(":", limit = 2)
        val number = parts.getOrNull(0) ?: ""
        val message = parts.getOrNull(1) ?: ""
        return ParsedCode.Sms(number, message)
    }

    private fun parseTel(raw: String): ParsedCode.Phone {
        val number = raw.substring(4)
        return ParsedCode.Phone(number)
    }

    private fun parseContact(raw: String): ParsedCode.Contact {
        var name = ""
        var phone = ""
        var email = ""
        var org = ""

        val lines = raw.lines()
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("FN:", ignoreCase = true) -> name = trimmed.substring(3).trim()
                trimmed.startsWith("N:", ignoreCase = true) && name.isEmpty() -> {
                    val parts = trimmed.substring(2).split(";")
                    name = parts.reversed().filter { it.isNotBlank() }.joinToString(" ")
                }
                trimmed.startsWith("TEL", ignoreCase = true) -> {
                    phone = trimmed.substringAfter(':').trim()
                }
                trimmed.startsWith("EMAIL", ignoreCase = true) -> {
                    email = trimmed.substringAfter(':').trim()
                }
                trimmed.startsWith("ORG:", ignoreCase = true) -> {
                    org = trimmed.substring(4).trim()
                }
            }
        }
        return ParsedCode.Contact(name, phone, email, org)
    }

    private fun parseGeo(raw: String): ParsedCode.Location {
        return try {
            val clean = raw.substring(4)
            val coords = clean.substringBefore('?').split(',')
            val lat = coords.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            val lon = coords.getOrNull(1)?.toDoubleOrNull() ?: 0.0
            val label = if (clean.contains("?q=")) clean.substringAfter("?q=") else ""
            ParsedCode.Location(lat, lon, label)
        } catch (_: Exception) {
            ParsedCode.Location(0.0, 0.0, raw)
        }
    }

    private fun parseEvent(raw: String): ParsedCode.Event {
        var summary = ""
        var location = ""
        var start = ""
        var end = ""
        var description = ""

        val lines = raw.lines()
        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("SUMMARY:", ignoreCase = true) -> summary = trimmed.substring(8).trim()
                trimmed.startsWith("LOCATION:", ignoreCase = true) -> location = trimmed.substring(9).trim()
                trimmed.startsWith("DTSTART:", ignoreCase = true) -> start = trimmed.substring(8).trim()
                trimmed.startsWith("DTEND:", ignoreCase = true) -> end = trimmed.substring(6).trim()
                trimmed.startsWith("DESCRIPTION:", ignoreCase = true) -> description = trimmed.substring(12).trim()
            }
        }
        return ParsedCode.Event(summary, location, start, end, description)
    }

    private fun parseUpi(raw: String): ParsedCode.Payment {
        return try {
            val uri = Uri.parse(raw)
            val vpa = uri.getQueryParameter("pa") ?: ""
            val name = uri.getQueryParameter("pn") ?: ""
            val amount = uri.getQueryParameter("am") ?: ""
            val note = uri.getQueryParameter("tn") ?: ""
            ParsedCode.Payment(vpa, name, amount, note)
        } catch (_: Exception) {
            ParsedCode.Payment("", "", "", raw)
        }
    }
}
