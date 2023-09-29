package com.example.testpdf.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(x : PdfEntity) : Long

    @Update
    suspend fun update(x:PdfEntity)

    @Delete()
    suspend fun delete(x : PdfEntity)

    @Query("SELECT * FROM pdf_item WHERE id = :id")
    fun getPdf(id : Int) : Flow<PdfEntity>

    @Query("SELECT * FROM pdf_item")
    fun getAllPdfs() : Flow<List<PdfEntity>>
}