package com.example.playlist_maker_android_brusilodiana.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlist_maker_android_brusilodiana.ui.activity.MainScreen
import androidx.core.content.ContextCompat.startActivity
import com.example.playlist_maker_android_brusilodiana.navigation.Screen
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.ui.screen.SearchScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.SettingsScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.PlaylistsScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.FavoritesScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.CreatePlaylistScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.TrackDetailsScreen
import com.example.playlist_maker_android_brusilodiana.ui.view_model.SearchViewModel
import com.example.playlist_maker_android_brusilodiana.ui.view_model.PlaylistViewModel
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import com.example.playlist_maker_android_brusilodiana.domain.models.Track

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
        val message = context.getString(R.string.share_message)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(context, Intent.createChooser(intent, null), null)
    }

    fun writeToSupport() {
        val email = context.getString(R.string.email_to)
        val subject = context.getString(R.string.email_subject)
        val body = context.getString(R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(context, Intent.createChooser(intent, null), null)
    }

    fun openAgreement() {
        val url = context.getString(R.string.agreement_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(context, intent, null)
    }

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToSearch = { navigateTo(Screen.Search) },
                onNavigateToSettings = { navigateTo(Screen.Settings) },
                onNavigateToPlaylists = { navigateTo(Screen.Playlists) },
                onNavigateToFavorites = { navigateTo(Screen.Favorites) }
            )
        }

        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.getViewModelFactory()
            )
            SearchScreen(
                onBackClick = { navigateUp() },
                viewModel = searchViewModel,
                onTrackClick = { track ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("trackName", track.trackName)
                    navController.currentBackStackEntry?.savedStateHandle?.set("artistName", track.artistName)
                    navController.currentBackStackEntry?.savedStateHandle?.set("trackTime", track.trackTime)
                    navController.currentBackStackEntry?.savedStateHandle?.set("favorite", track.favorite)
                    navigateTo(Screen.TrackDetails)
                }
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

        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                onBackClick = { navigateUp() },
                onCreateNewPlaylist = { navigateTo(Screen.CreatePlaylist) },
                onPlaylistClick = { playlistId ->
                    println("Playlist clicked: $playlistId")
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBackClick = { navigateUp() },
                onTrackClick = { track ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("trackName", track.trackName)
                    navController.currentBackStackEntry?.savedStateHandle?.set("artistName", track.artistName)
                    navController.currentBackStackEntry?.savedStateHandle?.set("trackTime", track.trackTime)
                    navController.currentBackStackEntry?.savedStateHandle?.set("favorite", track.favorite)
                    navigateTo(Screen.TrackDetails)
                }
            )
        }

        composable(Screen.CreatePlaylist.route) {
            val playlistViewModel: PlaylistViewModel = viewModel(
                factory = Creator.getPlaylistViewModelFactory()
            )

            CreatePlaylistScreen(
                onBackClick = { navigateUp() },
                onCreatePlaylist = { name, description ->
                    playlistViewModel.createNewPlaylist(name, description)
                    navigateUp()
                }
            )
        }

        composable(Screen.TrackDetails.route) {
            val trackName = navController.previousBackStackEntry?.savedStateHandle?.get<String>("trackName") ?: ""
            val artistName = navController.previousBackStackEntry?.savedStateHandle?.get<String>("artistName") ?: ""
            val trackTime = navController.previousBackStackEntry?.savedStateHandle?.get<String>("trackTime") ?: ""
            val favorite = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("favorite") ?: false

            val track = Track(
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                favorite = favorite
            )

            TrackDetailsScreen(
                track = track,
                onBackClick = { navigateUp() }
            )
        }
    }
}