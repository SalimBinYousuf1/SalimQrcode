package com.salim.qr.domain.parsing

sealed class ParsedCode {
    data class Website(
        val url: String,
        val host: String
    ) : ParsedCode()

    data class Text(
        val text: String
    ) : ParsedCode()

    data class Email(
        val address: String,
        val subject: String,
        val body: String
    ) : ParsedCode()

    data class Sms(
        val phoneNumber: String,
        val message: String
    ) : ParsedCode()

    data class Wifi(
        val ssid: String,
        val password: String,
        val security: String,
        val hidden: Boolean
    ) : ParsedCode()

    data class Phone(
        val phoneNumber: String
    ) : ParsedCode()

    data class Contact(
        val name: String,
        val phone: String,
        val email: String,
        val organization: String
    ) : ParsedCode()

    data class Location(
        val latitude: Double,
        val longitude: Double,
        val label: String
    ) : ParsedCode()

    data class Event(
        val title: String,
        val location: String,
        val start: String,
        val end: String,
        val description: String
    ) : ParsedCode()

    data class Payment(
        val vpa: String,
        val name: String,
        val amount: String,
        val note: String
    ) : ParsedCode()

    data class Barcode(
        val value: String,
        val formatName: String
    ) : ParsedCode()

    data class Other(
        val rawText: String
    ) : ParsedCode()
}
