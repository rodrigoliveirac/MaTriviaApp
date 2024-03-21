package com.rodcollab.matriviaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.rodcollab.matriviaapp.game.theme.MaTriviaAppTheme
import com.rodcollab.matriviaapp.game.ui.TriviaGameScreen
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameVm
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaTriviaAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: TriviaGameVm by viewModels()
                    TriviaGameScreen(viewModel)
                }
            }
        }
    }
}