package com.example.testpdf.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testpdf.PdfViewModel
import com.example.testpdf.R

@Composable
fun ViewPdfScreen(modifier:Modifier = Modifier){

    val viewModel : ViewPdfViewModel = viewModel(factory = ViewPdfViewModel.Factory)

    Column(modifier = modifier){
        val pdfBitmap = viewModel.renderPage()
        if (pdfBitmap == null){
            Image(painter = painterResource(id = R.drawable.baseline_error_24),null, modifier = Modifier.fillMaxWidth())
        }
        else{
            Log.d("HTe ime", "RENDER BOY")
            Image(pdfBitmap.asImageBitmap(),viewModel.currentPage.toString(), contentScale = ContentScale.FillWidth,modifier = Modifier.fillMaxWidth())
        }

        Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly){
            Button(onClick = { viewModel.setPage(viewModel.currentPage-1) }) {
                Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), stringResource(R.string.back))
                
            }
            Button(onClick = { viewModel.setPage(viewModel.currentPage+1) }) {
                Icon(painter = painterResource(id = R.drawable.baseline_arrow_forward_24), stringResource(R.string.next) )
            }
        }
    }
}
