package com.example.testpdf.ui

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testpdf.PdfApplication
import com.example.testpdf.PdfViewModel
import com.example.testpdf.data.PdfRepository

class ViewPdfViewModel(val repository: PdfRepository): ViewModel() {
    val TAG = "ViewPdfViewModel"
    var renderer: PdfRenderer? = null
        private set
    var pageCount = 0
        private set
    var currentPage by mutableStateOf(0)
        private set

    init{
        setupRendererFromRepo()
    }
    fun setupRendererFromRepo(){
        renderer = repository.getCurrentRenderer()
        renderer?.let{
            pageCount = it.pageCount
            currentPage = 0
        }
    }

    fun renderPage(): Bitmap? {
        if (renderer!=null){
            val page = renderer!!.openPage(currentPage)
            val bitMap = Bitmap.createBitmap(page.width,page.height, Bitmap.Config.ARGB_8888)
            page.render(bitMap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            return bitMap
        }
        Log.e(TAG,"Failed to renderPage. The renderer was null!")
        return null

    }
    fun setPage(newPage : Int){
        if (newPage in 0 until pageCount){
            currentPage = newPage
        }
        else{
            Log.w(TAG, "Could not set page to $newPage because pageCount is $pageCount")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PdfApplication)
                ViewPdfViewModel(application.container.pdfRepository)
            }
        }
    }
}