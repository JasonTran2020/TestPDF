package com.example.testpdf.data

import android.graphics.pdf.PdfRenderer
import kotlinx.coroutines.flow.Flow

interface PdfRepository{
    fun getCurrentRenderer() :PdfRenderer?
    fun setRenderer(renderer: PdfRenderer)

    fun getAllPdfs(): Flow<List<PdfEntity>>
    fun getPdf(id:Int):Flow<PdfEntity>
    suspend fun insertPdf(pdfEntity: PdfEntity) : Long
    suspend fun updatePdf(pdfEntity: PdfEntity)
    suspend fun deletePdf(pdfEntity: PdfEntity)
}

class DefaultPdfRepository(private val pdfDao: PdfDao) :PdfRepository{
    private var renderer :PdfRenderer? = null

    override fun getCurrentRenderer(): PdfRenderer? {
        return renderer
    }

    override fun setRenderer(renderer: PdfRenderer) {
        this.renderer = renderer
    }

    override fun getAllPdfs(): Flow<List<PdfEntity>> {
        return pdfDao.getAllPdfs()
    }

    override fun getPdf(id: Int): Flow<PdfEntity> {
        return pdfDao.getPdf(id)
    }

    override suspend fun insertPdf(pdfEntity: PdfEntity) :Long {
        return pdfDao.insert(pdfEntity)
    }

    override suspend fun updatePdf(pdfEntity: PdfEntity) {
        pdfDao.update(pdfEntity)
    }

    override suspend fun deletePdf(pdfEntity: PdfEntity) {
        pdfDao.delete(pdfEntity)
    }

}