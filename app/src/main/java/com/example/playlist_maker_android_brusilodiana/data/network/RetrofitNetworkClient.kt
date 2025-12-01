package com.example.playlist_maker_android_brusilodiana.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlist_maker_android_brusilodiana.R
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchRequest
import com.example.playlist_maker_android_brusilodiana.data.dto.TracksSearchResponse
import com.example.playlist_maker_android_brusilodiana.domain.NetworkClient
import com.example.playlist_maker_android_brusilodiana.domain.models.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RetrofitNetworkClient(
    private val api: ITunesApiService,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: Any): BaseResponse {
        return try {
            if (!isInternetAvailable()) {
                return BaseResponse().apply {
                    resultCode = -1
                    errorMessage = context.getString(R.string.no_internet_connection)
                }
            }

            when (dto) {
                is TracksSearchRequest -> {
                    withContext(Dispatchers.IO) {
                        val response = api.searchTracks(
                            query = dto.expression,
                            limit = 50
                        )
                        response.apply {
                            resultCode = 200
                            errorMessage = null
                        }
                    }
                }
                else -> BaseResponse().apply {
                    resultCode = 400
                    errorMessage = context.getString(R.string.invalid_request_type)
                }
            }
        } catch (e: UnknownHostException) {
            BaseResponse().apply {
                resultCode = -1
                errorMessage = context.getString(R.string.no_internet_connection)
            }
        } catch (e: SocketTimeoutException) {
            BaseResponse().apply {
                resultCode = -1
                errorMessage = context.getString(R.string.connection_timeout)
            }
        } catch (e: IOException) {
            BaseResponse().apply {
                resultCode = -1
                errorMessage = context.getString(R.string.network_error_generic, e.message ?: context.getString(R.string.unknown_error))
            }
        } catch (e: Exception) {
            BaseResponse().apply {
                resultCode = -2
                errorMessage = context.getString(R.string.unexpected_error, e.message ?: context.getString(R.string.unknown_error))
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}