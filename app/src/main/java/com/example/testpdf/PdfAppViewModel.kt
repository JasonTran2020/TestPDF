package com.example.testpdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testpdf.data.PdfEntity
import com.example.testpdf.data.PdfRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class PdfViewModel(val repository: PdfRepository): ViewModel() {
    val TAG = "PdfViewModel"

    fun setRenderer(newRenderer: PdfRenderer){
        repository.setRenderer(newRenderer)
    }


    suspend fun insertPdfToDatabase(uri: Uri, context: Context){

        var newPdf = PdfEntity(uri = uri.toString())

        //Have to insert in repository to get the autogenerated id, as the id is part of the name for the thumbnail to keep them unique
        val id = repository.insertPdf(newPdf)
        val imagePath = saveBitmapAsFile(uri,id,context)
        val fileName = getFilename(uri,context)


        newPdf = newPdf.copy(id= id.toInt(), fileName = fileName,thumbnailPath = imagePath)
        repository.updatePdf(newPdf)
    }

    private fun getFilename(uri: Uri, context: Context) : String?{
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        if (cursor!=null){
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: -1
            val fileName: String? = if (columnIndex<0) null else cursor.getString(columnIndex)
            cursor.close()
            return fileName
        }
        return null

    }

    private suspend fun saveBitmapAsFile(uri: Uri, id:Long, context: Context): String? {
        val fileDesc = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
        //Open the first page of the pdf and save it into a bitmap first
        val render = PdfRenderer(fileDesc)
        val page = render.openPage(0)
        var bitMap = Bitmap.createBitmap(page.width,page.height, Bitmap.Config.ARGB_8888)
        page.render(bitMap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        //Saving as JPEG has caused a blackground, probably because transparent background are changed to black
        //When moving to the more limited JPEG format
        bitMap = convertTransparentBitmapToWhite(bitMap)

        val directory = context.getDir("imageDir",Context.MODE_PRIVATE)
        val file = File(directory,"Thumbnail$id.png")
        //Regardless if the file name already exist, update it to be whatever bitMap we just got back.
        withContext(Dispatchers.IO) {
            val fos = FileOutputStream(file)
            //JPEGs are smaller than PNG, but at the cost of quality. For a thumbnail, quality isn't a huge issue
            bitMap.compress(Bitmap.CompressFormat.JPEG,90,fos)
            fos.flush()
            fos.close()
        }

        return file.absolutePath
    }

    companion object{
        val Factory:ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PdfApplication)
                PdfViewModel(application.container.pdfRepository)
            }
        }
        fun convertTransparentBitmapToWhite(bitmap: Bitmap): Bitmap{
            //When converting to JPEG to save the thumbnail, the background would be black sometime instead of white like
            //in the pdf renderer. Seems to be related to the fact that I saved the files as JPEG, and by default, transparent
            //backgrounds are saved as the color black. Therefore, this funciton will change that to white
            //Function based on one of the lower answers from this: https://stackoverflow.com/questions/31608961/why-bitmap-showing-black-background-in-android
            if (bitmap.hasAlpha()){
                //Empty bitmap but with the same size
                val newBitmap = Bitmap.createBitmap(bitmap.width,bitmap.height,bitmap.config)
                //Build a new canvas and draw in that empty bitmap into it
                val canvas = Canvas(newBitmap)
                //Now we fill the canvas with white
                canvas.drawColor(Color.WHITE)
                //Then we draw the original bitmap onto it. The documentation online for this method says:
                //Draw the specified bitmap, with its top/left corner at (x,y), using the specified paint, transformed by the current matrix.
                //Dunno about what it means matrix, paint essentially means the type of brush. I.e, color and style. We want original, so I guess null works
                canvas.drawBitmap(bitmap,0f,0f,null)
                //Now we done
                return newBitmap
            }
            return bitmap
        }
    }
}