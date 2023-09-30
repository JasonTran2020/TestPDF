package com.example.testpdf

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testpdf.ui.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.testpdf.data.PdfEntity
import com.example.testpdf.data.PdfListItem
import com.example.testpdf.ui.PdfViewModel
import com.example.testpdf.ui.ViewPdfScreen
import kotlinx.coroutines.launch

enum class PdfScreens(){
    HomeScreen,
    ViewPDFScreen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var currentScreen = PdfScreens.valueOf(currentBackStackEntry?.destination?.route ?: PdfScreens.HomeScreen.name)
    val coroutineScope = rememberCoroutineScope()

    val viewModel : PdfViewModel = viewModel(factory = PdfViewModel.Factory)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it!=null){
            //Permission to persist the Uri, so we can still use it when saving to Room?
            context.contentResolver.takePersistableUriPermission(it,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val fileDesc = context.contentResolver.openFileDescriptor(it, "r")
            if (fileDesc == null){
                Log.e(TAG, "Could not get file desc")
            }
            else{
                //BUsiness logic? Need to move it somehwere else
                val render = PdfRenderer(fileDesc)

                coroutineScope.launch {
                    viewModel.insertPdfToDatabase(it,context)
                }
                //By setting the renderer here, before navigating to the next screen, we gurantee that the next screen will have a renderer to use
                //THis is honestly not a good idea. Saw some other code and perhaps putting this as a coroutine viewmodel method would be better
                //But I think this would require changing the ViewPdfScreen to using produceState
                viewModel.setRenderer(render)
                navController.navigate(PdfScreens.ViewPDFScreen.name)
            }

        }
        else{
            Log.e(TAG, "Bruh we didn't get a pdf back or something. Uri is null")
        }
    }

    val onItemClick : (pdfEnity: PdfListItem) -> Unit ={
        val fileDesc = context.contentResolver.openFileDescriptor(Uri.parse(it.uri), "r")
        if (fileDesc == null){
            Log.e(TAG, "Could not get file desc")
        }
        else{
            val render = PdfRenderer(fileDesc)
            viewModel.setRenderer(render)
            navController.navigate(PdfScreens.ViewPDFScreen.name)
        }
    }
    
    Scaffold(
        floatingActionButton = {if (currentScreen == PdfScreens.HomeScreen)AddButton { launcher.launch(arrayOf("application/pdf")) } },
        topBar = { PdfAppBar(currentScreen = currentScreen, onClick = { navController.popBackStack() },modifier = Modifier.background(Color.Green))}
    ) { contentPadding ->
        
        NavHost(navController = navController, startDestination = PdfScreens.HomeScreen.name ){

            composable(PdfScreens.HomeScreen.name){
                HomeScreen(modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                ) {  onItemClick(it) }
            }

            composable(PdfScreens.ViewPDFScreen.name){
                ViewPdfScreen(modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize())
            }
        }
    }
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfAppBar(currentScreen: PdfScreens,onClick: () -> Unit,modifier: Modifier = Modifier){
    TopAppBar(
        title = { Text("TestPdfApp") },
        navigationIcon = {if (currentScreen==PdfScreens.ViewPDFScreen){
            IconButton(onClick = onClick){
                Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24),null)
            } } },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier)

}

@Composable
fun AddButton(onClick : ()->Unit){
    IconButton(onClick = onClick) {
        Icon(painterResource(id = R.drawable.baseline_add_24),null)
    }
}



@Preview
@Composable
fun previewApp()
{
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background) {
        PdfApp()
    }
}
