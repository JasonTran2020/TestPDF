package com.example.testpdf.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testpdf.PdfApplication
import com.example.testpdf.data.PdfEntity
import com.example.testpdf.data.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

const val TAG = "PdfViewModel"
class PdfViewModel(val repository: PdfRepository): ViewModel() {

    var renderer: PdfRenderer? = null
        private set
    var pageCount = 0
        private set
    var currentPage by mutableStateOf(0)
        private set

    init{
        renderer = repository.getCurrentRenderer()
        renderer?.let { setRenderer(it) }

    }
    fun setRenderer(newRenderer: PdfRenderer){
        repository.setRenderer(newRenderer)
        renderer = newRenderer
        pageCount = newRenderer.pageCount
        currentPage = 0
    }

    fun renderPage(): Bitmap? {
        if (renderer!=null){
            val page = renderer!!.openPage(currentPage)
            val bitMap = Bitmap.createBitmap(page.width,page.height,Bitmap.Config.ARGB_8888)
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

    suspend fun insertPdfToDatabase(uri: Uri, bitMap: Bitmap, context: Context){

        var newPdf=PdfEntity(uri = uri.toString())
        val id = repository.insertPdf(newPdf)
        val imagePath = saveBitmapAsFile(bitMap,id,context)

        newPdf =newPdf.copy(id= id.toInt(), thumbnailPath = imagePath.absolutePath)
        repository.updatePdf(newPdf)
    }

    private suspend fun saveBitmapAsFile(bitmap: Bitmap, id:Long, context: Context): File {
        val directory = context.getDir("imageDir",Context.MODE_PRIVATE)
        val file = File(directory,"Thumbnail$id.png")
        if (!file.exists()){
            withContext(Dispatchers.IO) {
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos)
                fos.flush()
                fos.close()
            }
        }
        return file
    }

    companion object{
        val Factory:ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PdfApplication)
                PdfViewModel(application.container.pdfRepository)
            }
        }
    }
}