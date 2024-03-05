package com.harukadev.stockmanager

import android.app.Application
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class StockManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SSLInitializer.initialize()

        val logFile = File(applicationContext.filesDir, "log.txt")

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            try {
                val writer = FileWriter(logFile, false)
                val printWriter = PrintWriter(writer)

                throwable.printStackTrace(printWriter)

                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            System.exit(1)
        }
    }
}
