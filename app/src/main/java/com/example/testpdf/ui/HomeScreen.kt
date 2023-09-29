package com.example.testpdf.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testpdf.R
import com.example.testpdf.data.PdfEntity
import com.example.testpdf.data.PdfListItem

@Composable
fun HomeScreen(modifier: Modifier = Modifier, onItemClick : (pdfItem:PdfListItem) -> Unit){

    val viewModel:PdfAppViewModel = viewModel(factory = PdfAppViewModel.Factory)
    var listOfPdf = viewModel.pdfListState.collectAsState()

    LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(2),contentPadding= PaddingValues(8.dp), verticalArrangement = Arrangement.Top){
        items(PdfListItem.entityListToItemList(listOfPdf.value,)){
            //Padding goess first, then clickable so a user actual has to click the card, not next to it
            PdfCard(temp = it,onItemClick,modifier = Modifier.padding(8.dp).clickable { onItemClick(it) })
        }


    }
}

@Composable
fun PdfCard(temp: PdfListItem, onItemClick : (pdfItem:PdfListItem) -> Unit,  modifier: Modifier = Modifier){
    Card(shape = CardDefaults.elevatedShape, modifier = modifier)  {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (temp.bitmap==null){
                Image(painter = painterResource(id = R.drawable.baseline_error_24),null, contentScale = ContentScale.FillWidth, modifier = Modifier.fillMaxWidth())
            }
            else{
                Image(temp.bitmap.asImageBitmap(),null)
            }

            Text(temp.uri)
        }
    }
}
