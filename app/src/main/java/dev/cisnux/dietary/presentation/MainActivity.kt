package dev.cisnux.dietary.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.dietary.presentation.navigation.DietaryNavGraph
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietaryTheme {
                DietaryNavGraph()
            }
        }
    }
}