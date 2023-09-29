package com.example.testpdf

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.testpdf.ui.theme.TestPDFTheme

const val TAG = "MAIN ACTIVITY"

class MainActivity : ComponentActivity() {
    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        if (it!=null){
            contentResolver.openFileDescriptor(it, "r")
        }
        else{
            Log.e(TAG, "Bruh we didn't get a pdf back or something. Uri is null")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestPDFTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PdfApp()
                }
            }
        }
    }
}

