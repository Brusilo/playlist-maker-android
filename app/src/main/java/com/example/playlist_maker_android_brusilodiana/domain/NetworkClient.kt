package com.example.playlist_maker_android_brusilodiana.domain

import com.example.playlist_maker_android_brusilodiana.domain.models.BaseResponse

interface NetworkClient {
    suspend fun doRequest(dto: Any): BaseResponse
}