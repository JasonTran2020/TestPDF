package com.example.testpdf.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_item")
data class PdfEntity(
    val uri : String,
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    val thumbnailPath:String? = null
)

