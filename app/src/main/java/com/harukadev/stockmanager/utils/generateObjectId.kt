package com.harukadev.stockmanager.utils

fun generateObjectId(): String {
    val timestamp = System.currentTimeMillis() / 1000
    val machineIdentifier = (Math.random() * 16777216).toInt() // Representa 3 bytes
    val processIdentifier = (Math.random() * 65536).toInt() // Representa 2 bytes
    val counter = (Math.random() * 16777216).toInt() // Representa 3 bytes

    return String.format("%08x%06x%04x%06x", timestamp, machineIdentifier, processIdentifier, counter)
}
