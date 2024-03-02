package com.meow.tsukinari

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.meow.tsukinari.presentation.authentication.AuthViewModel
import com.meow.tsukinari.presentation.authentication.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
//            val mainLayoutViewModel = viewModel(modelClass = MainLayoutViewModel::class.java)
//            MainLayout(mainLayoutViewModel)

            //unit test for SignUpScreen
            val authViewModel = viewModel(modelClass = AuthViewModel::class.java)
            SignUpScreen(authViewModel, {}, {}, {})
        }
    }
}

