package com.harukadev.stockmanager

import android.app.Application

class StockManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SSLInitializer.initialize()
    }
}
