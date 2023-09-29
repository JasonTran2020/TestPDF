package com.example.testpdf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PdfEntity::class], version = 2, exportSchema = false)
abstract class PdfDatabase : RoomDatabase() {
    abstract fun pdfDao(): PdfDao

    companion object{
        var Instance: PdfDatabase? = null

        fun getInstance(context: Context) : PdfDatabase{
            return Instance?: synchronized(this){
                Room.databaseBuilder(context,PdfDatabase::class.java,"pdf_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                    Instance = it
                }
            }
        }
    }
}