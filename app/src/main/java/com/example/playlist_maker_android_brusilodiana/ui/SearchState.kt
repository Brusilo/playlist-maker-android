package com.example.playlist_maker_android_brusilodiana.ui

import com.example.playlist_maker_android_brusilodiana.data.network.Track

sealed class SearchState {
    object Initial : SearchState()
    object Searching : SearchState()
    data class Success(val foundList: List<Track>) : SearchState()
    data class Fail(val error: String) : SearchState()
}