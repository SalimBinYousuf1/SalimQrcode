package com.salim.qr.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "code_records",
    indices = [
        Index(value = ["origin"]),
        Index(value = ["timestamp"]),
        Index(value = ["favorite"])
    ]
)
data class CodeRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val origin: String, // "scanned" or "created"
    val payload: String,
    val format: String, // "QR_CODE", "EAN_13", "CODE_128", etc.
    val parsedType: String, // "Website", "Text", "Email", "SMS", "Wifi", etc.
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val favorite: Boolean = false,
    val styleJson: String = ""
)
