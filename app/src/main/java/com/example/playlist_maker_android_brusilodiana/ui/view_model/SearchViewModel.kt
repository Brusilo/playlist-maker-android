package com.example.playlist_maker_android_brusilodiana.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.creator.Creator
import com.example.playlist_maker_android_brusilodiana.domain.SearchHistoryRepository
import com.example.playlist_maker_android_brusilodiana.domain.TracksRepository
import com.example.playlist_maker_android_brusilodiana.domain.states.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val context: Context
) : ViewModel() {
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    val searchHistory = searchHistoryRepository.getHistory().map { history ->
        history.take(10)
    }

    private var lastSearchQuery: String = ""
    private var isSearching = false
    private var lastErrorState: SearchState.Fail? = null

    fun search(whatSearch: String) {
        if (whatSearch.isEmpty()) {
            _searchScreenState.value = SearchState.Initial
            return
        }

        lastSearchQuery = whatSearch
        isSearching = true

        searchHistoryRepository.addToHistory(whatSearch)

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
                    val errorMessage = when {
                        e.message?.contains(context.getString(R.string.no_internet_connection), ignoreCase = true) == true ->
                            context.getString(R.string.no_internet_connection)
                        e.message?.contains(context.getString(R.string.connection_timeout), ignoreCase = true) == true ->
                            context.getString(R.string.connection_timeout)
                        e.message?.contains(context.getString(R.string.network_error), ignoreCase = true) == true ->
                            context.getString(R.string.network_error_generic, e.message ?: context.getString(R.string.unknown_error))
                        else -> context.getString(R.string.search_error, e.message ?: context.getString(R.string.unknown_error))
                    }
                    lastErrorState = SearchState.Fail(errorMessage)
                    _searchScreenState.value = lastErrorState!!
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
            delay(500)

            if (query == lastSearchQuery && isSearching) {
                search(query)
            }
        }
    }

    fun clearSearch() {
        lastSearchQuery = ""
        isSearching = false
        lastErrorState = null
        _searchScreenState.value = SearchState.Initial
    }

    fun clearHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.clearHistory()
        }
    }

    fun refresh() {
        if (lastSearchQuery.isNotEmpty()) {
            search(lastSearchQuery)
        } else if (lastErrorState != null) {
            _searchScreenState.value = lastErrorState!!
        }
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        Creator.getTracksRepository(context),
                        Creator.getSearchHistoryRepository(context),
                        context
                    ) as T
                }
            }
    }
}