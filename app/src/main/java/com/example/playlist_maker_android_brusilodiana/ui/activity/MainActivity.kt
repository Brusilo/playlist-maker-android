package com.example.playlist_maker_android_brusilodiana.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.playlist_maker_android_brusilodiana.data.preferences.ThemePreferences
import com.example.playlist_maker_android_brusilodiana.navigation.PlaylistHost
import com.example.playlist_maker_android_brusilodiana.ui.theme.MyApplicationTheme
import com.example.playlist_maker_android_brusilodiana.ui.view_model.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel = remember {
                ThemeViewModel(ThemePreferences.create(this))
            }
            val darkTheme = themeViewModel.darkTheme.collectAsState(initial = false)

            MyApplicationTheme(darkTheme = darkTheme.value) {
                val navController = rememberNavController()
                PlaylistHost(navController = navController)
            }
        }
    }
}