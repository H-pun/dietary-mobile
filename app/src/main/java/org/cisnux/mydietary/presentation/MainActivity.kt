package org.cisnux.mydietary.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.cisnux.mydietary.presentation.navigation.DietaryNavGraph
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme

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