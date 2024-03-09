package com.meow.tsukinari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meow.tsukinari.presentation.reader.ReaderScreen
import com.meow.tsukinari.presentation.reader.ReaderViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
//            val mainLayoutViewModel = viewModel(modelClass = MainLayoutViewModel::class.java)
//            MainLayout(mainLayoutViewModel)

            // test case for the adding chapter screen
//            val addChapterViewModel = viewModel(modelClass = AddChapterViewModel::class.java)
//            AddChapterScreen(addChapterViewModel, "fictionId"){}

            // test case for the reader screen
            val readerViewModel = viewModel(modelClass = ReaderViewModel::class.java)
            ReaderScreen(readerViewModel)
        }
    }
}

