package com.example.playlist_maker_android_brusilodiana

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlist_maker_android_brusilodiana.ui.activity.MainScreen
import androidx.core.content.ContextCompat.startActivity
import com.example.playlist_maker_android_brusilodiana.ui.Screen
import com.example.playlist_maker_android_brusilodiana.ui.SearchScreen
import com.example.playlist_maker_android_brusilodiana.ui.SettingsScreen

@Composable
fun PlaylistHost(navController: NavHostController) {
    val context = LocalContext.current

    fun navigateTo(screen: Screen) {
        navController.navigate(screen.route) {
            launchSingleTop = true
        }
    }

    fun navigateUp() {
        navController.popBackStack()
    }

    fun shareApp() {
        val message = context.getString(com.example.playlist_maker_android_brusilodiana.R.string.share_message)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(context, Intent.createChooser(intent, null), null)
    }

    fun writeToSupport() {
        val email = context.getString(com.example.playlist_maker_android_brusilodiana.R.string.email_to)
        val subject = context.getString(com.example.playlist_maker_android_brusilodiana.R.string.email_subject)
        val body = context.getString(com.example.playlist_maker_android_brusilodiana.R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(context, Intent.createChooser(intent, null), null)
    }

    fun openAgreement() {
        val url = context.getString(com.example.playlist_maker_android_brusilodiana.R.string.agreement_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(context, intent, null)
    }

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToSearch = { navigateTo(Screen.Search) },
                onNavigateToSettings = { navigateTo(Screen.Settings) }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(onBackClick = { navigateUp() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navigateUp() },
                onShareClick = { shareApp() },
                onSupportClick = { writeToSupport() },
                onAgreementClick = { openAgreement() }
            )
        }
    }
}
