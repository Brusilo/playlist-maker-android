package com.example.playlist_maker_android_brusilodiana.domain.states

import com.example.playlist_maker_android_brusilodiana.domain.models.Track

sealed class SearchState {
    object Initial : SearchState()
    object Searching : SearchState()
    object EmptyResult : SearchState()
    data class Success(val foundList: List<Track>) : SearchState()
    data class Fail(val error: String) : SearchState()
}