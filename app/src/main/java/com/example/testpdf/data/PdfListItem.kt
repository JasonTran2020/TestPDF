package com.example.testpdf.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
const val TAG = "PdfListItem"
class PdfListItem(val uri : String,
                  val id:Int = 0,
                  val bitmap: Bitmap? = null) {

    companion object{
        fun entityListToItemList(entityList: List<PdfEntity>):List<PdfListItem>{
            return entityList.map {
                Log.d(TAG,"The path was: ${it.thumbnailPath}")
                if(it.thumbnailPath!=null){
                    Log.d(TAG,"Bruh? huh?")
                    val myBitmap = BitmapFactory.decodeFile(it.thumbnailPath)
                    return@map PdfListItem(it.uri,it.id,myBitmap)
                }

                Log.e(TAG,"How we get here?")
                PdfListItem(it.uri,it.id,null)


            }
        }

    }
}

