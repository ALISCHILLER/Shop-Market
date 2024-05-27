@file:OptIn(ExperimentalMaterial3Api::class)


package com.msa.eshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.SetupNavigator
import com.msa.eshop.ui.theme.EShopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navManager: NavManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContent {
            EShopTheme {
                    SetupNavigator()
                }

        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EShopTheme {

    }
}