package com.example.testpdf.data

import android.content.Context
import androidx.compose.ui.platform.LocalContext

interface AppContainer {
    var pdfRepository: PdfRepository
}

class DefaultAppContainer(context : Context) :AppContainer{

    override var pdfRepository: PdfRepository = DefaultPdfRepository(PdfDatabase.getInstance(context = context).pdfDao())

}