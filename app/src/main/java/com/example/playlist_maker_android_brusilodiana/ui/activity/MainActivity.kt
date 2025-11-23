package com.example.playlist_maker_android_brusilodiana.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.ui.Screen
import com.example.playlist_maker_android_brusilodiana.ui.SearchScreen
import com.example.playlist_maker_android_brusilodiana.ui.SettingsScreen
import com.example.playlist_maker_android_brusilodiana.ui.view_model.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current

    fun navigateTo(screen: Screen) {
        navController.navigate(screen.route) {
            launchSingleTop = true
        }
    }

    fun navigateUp() {
        navController.popBackStack()
    }

    fun shareApp() {
        val message = context.getString(R.string.share_message)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    fun writeToSupport() {
        val email = context.getString(R.string.email_to)
        val subject = context.getString(R.string.email_subject)
        val body = context.getString(R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = android.net.Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    fun openAgreement() {
        val url = context.getString(R.string.agreement_url)
        val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url))
        context.startActivity(intent)
    }

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToSearch = { navigateTo(Screen.Search) },
                onNavigateToSettings = { navigateTo(Screen.Settings) }
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.getViewModelFactory()
            )
            SearchScreen(
                onBackClick = { navigateUp() },
                viewModel = searchViewModel
            )
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

@Composable
fun MainScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val title = stringResource(R.string.main_screen_title)
    val search = stringResource(R.string.search_button)
    val playlists = stringResource(R.string.playlists_button)
    val favorites = stringResource(R.string.favorites_button)
    val settings = stringResource(R.string.settings_button)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.blue_background))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MenuItem(search, Icons.Default.Search) { onNavigateToSearch() }
            MenuItem(playlists, Icons.Default.List) { }
            MenuItem(favorites, Icons.Default.Favorite) { }
            MenuItem(settings, Icons.Default.Settings) { onNavigateToSettings() }
        }
    }
}

@Composable
fun MenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = colorResource(R.color.chevron_grey),
            modifier = Modifier.size(20.dp)
        )
    }
}