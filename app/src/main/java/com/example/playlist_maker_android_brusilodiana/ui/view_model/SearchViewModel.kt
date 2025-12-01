package com.example.playlist_maker_android_brusilodiana.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import com.example.playlist_maker_android_brusilodiana.domain.states.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private var lastSearchQuery: String = ""
    private var isSearching = false

    fun search(whatSearch: String) {
        if (whatSearch.isEmpty()) {
            _searchScreenState.value = SearchState.Initial
            return
        }

        lastSearchQuery = whatSearch
        isSearching = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _searchScreenState.value = SearchState.Searching
                val list = tracksRepository.searchTracks(expression = whatSearch)

                if (whatSearch == lastSearchQuery && isSearching) {
                    if (list.isEmpty()) {
                        _searchScreenState.value = SearchState.EmptyResult
                    } else {
                        _searchScreenState.value = SearchState.Success(foundList = list)
                    }
                }
            } catch (e: Exception) {
                if (whatSearch == lastSearchQuery && isSearching) {
                    _searchScreenState.value = SearchState.Fail(e.message ?: "Unknown error")
                }
            } finally {
                isSearching = false
            }
        }
    }

    fun searchDebounced(query: String) {
        lastSearchQuery = query
        isSearching = true

        viewModelScope.launch {
            delay(500) // Дебаунс 500 мс

            if (query == lastSearchQuery && isSearching) {
                search(query)
            }
        }
    }

    fun clearSearch() {
        lastSearchQuery = ""
        isSearching = false
        _searchScreenState.value = SearchState.Initial
    }

    fun refresh() {
        if (lastSearchQuery.isNotEmpty()) {
            search(lastSearchQuery)
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(Creator.getTracksRepository(context)) as T
                }
            }
    }
}