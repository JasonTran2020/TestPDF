package com.example.testpdf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.testpdf.PdfApplication
import com.example.testpdf.data.PdfEntity
import com.example.testpdf.data.PdfRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeScreenViewModel(pdfRepository: PdfRepository):ViewModel() {

    val pdfListState : StateFlow<List<PdfEntity>> = pdfRepository.getAllPdfs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf()
    )

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PdfApplication)
                HomeScreenViewModel(application.container.pdfRepository)
            }
        }
    }
}