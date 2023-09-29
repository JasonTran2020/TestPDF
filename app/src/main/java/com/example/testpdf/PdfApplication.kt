package com.example.testpdf

import android.app.Application
import com.example.testpdf.data.AppContainer
import com.example.testpdf.data.DefaultAppContainer

class PdfApplication:Application() {
    lateinit var container:AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}