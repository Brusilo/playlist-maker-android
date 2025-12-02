package com.example.playlist_maker_android_brusilodiana.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.core.content.ContextCompat.startActivity
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.ui.activity.MainScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.SearchScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.SettingsScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.PlaylistsScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.FavoritesScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.CreatePlaylistScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.TrackDetailsScreen
import com.example.playlist_maker_android_brusilodiana.ui.screen.PlaylistDetailsScreen
import com.example.playlist_maker_android_brusilodiana.ui.view_model.SearchViewModel
import com.example.playlist_maker_android_brusilodiana.ui.view_model.PlaylistViewModel
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import com.example.playlist_maker_android_brusilodiana.domain.models.Track
import java.net.URLEncoder
import java.net.URLDecoder

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
                factory = SearchViewModel.getViewModelFactory(context)
            )
            SearchScreen(
                onBackClick = { navigateUp() },
                viewModel = searchViewModel,
                onTrackClick = { track: Track ->
                    navController.navigate(
                        "${Screen.TrackDetails.route}/" +
                                "${URLEncoder.encode(track.trackName, "UTF-8")}/" +
                                "${URLEncoder.encode(track.artistName, "UTF-8")}/" +
                                "${URLEncoder.encode(track.trackTime, "UTF-8")}/" +
                                "${URLEncoder.encode(track.artworkUrl, "UTF-8")}/" +
                                "${track.id}"
                    ) {
                        launchSingleTop = true
                    }
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
            val playlistViewModel: PlaylistViewModel = viewModel(
                factory = Creator.getPlaylistViewModelFactory(context)
            )
            PlaylistsScreen(
                onBackClick = { navigateUp() },
                onCreateNewPlaylist = { navigateTo(Screen.CreatePlaylist) },
                onPlaylistClick = { playlistId ->
                    navController.navigate("${Screen.PlaylistDetails.route}/$playlistId") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Favorites.route) {
            val playlistViewModel: PlaylistViewModel = viewModel(
                factory = Creator.getPlaylistViewModelFactory(context)
            )
            FavoritesScreen(
                onBackClick = { navigateUp() },
                onTrackClick = { track: Track ->
                    navController.navigate(
                        "${Screen.TrackDetails.route}/" +
                                "${URLEncoder.encode(track.trackName, "UTF-8")}/" +
                                "${URLEncoder.encode(track.artistName, "UTF-8")}/" +
                                "${URLEncoder.encode(track.trackTime, "UTF-8")}/" +
                                "${URLEncoder.encode(track.artworkUrl, "UTF-8")}/" +
                                "${track.id}"
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.CreatePlaylist.route) {
            val playlistViewModel: PlaylistViewModel = viewModel(
                factory = Creator.getPlaylistViewModelFactory(context)
            )

            CreatePlaylistScreen(
                onBackClick = { navigateUp() },
                onCreatePlaylist = { name, description, coverImageUri ->
                    playlistViewModel.createNewPlaylist(name, description, coverImageUri)
                    navigateUp()
                }
            )
        }

        composable(
            "${Screen.TrackDetails.route}/{trackName}/{artistName}/{trackTime}/{artworkUrl}/{id}",
            arguments = listOf(
                navArgument("trackName") { type = NavType.StringType },
                navArgument("artistName") { type = NavType.StringType },
                navArgument("trackTime") { type = NavType.StringType },
                navArgument("artworkUrl") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val trackName = URLDecoder.decode(backStackEntry.arguments?.getString("trackName") ?: "", "UTF-8")
            val artistName = URLDecoder.decode(backStackEntry.arguments?.getString("artistName") ?: "", "UTF-8")
            val trackTime = URLDecoder.decode(backStackEntry.arguments?.getString("trackTime") ?: "", "UTF-8")
            val artworkUrl = URLDecoder.decode(backStackEntry.arguments?.getString("artworkUrl") ?: "", "UTF-8")
            val id = backStackEntry.arguments?.getString("id") ?: "0"

            val track = Track(
                id = id.toLongOrNull() ?: 0L,
                trackName = trackName,
                artistName = artistName,
                trackTime = trackTime,
                artworkUrl = artworkUrl,
                favorite = false
            )

            TrackDetailsScreen(
                track = track,
                onBackClick = { navigateUp() }
            )
        }

        composable(
            "${Screen.PlaylistDetails.route}/{playlistId}",
            arguments = listOf(
                navArgument("playlistId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L

            PlaylistDetailsScreen(
                playlistId = playlistId,
                onBackClick = { navigateUp() },
                onTrackClick = { track: Track ->
                    navController.navigate(
                        "${Screen.TrackDetails.route}/" +
                                "${URLEncoder.encode(track.trackName, "UTF-8")}/" +
                                "${URLEncoder.encode(track.artistName, "UTF-8")}/" +
                                "${URLEncoder.encode(track.trackTime, "UTF-8")}/" +
                                "${URLEncoder.encode(track.artworkUrl, "UTF-8")}/" +
                                "${track.id}"
                    ) {
                        launchSingleTop = true
                    }
                },
                navController = navController
            )
        }
    }
}