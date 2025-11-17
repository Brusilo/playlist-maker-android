package com.example.playlist_maker_android_brusilodiana.domain

import com.example.playlist_maker_android_brusilodiana.data.dto.BaseResponse

interface NetworkClient {
    fun doRequest(dto: Any): BaseResponse
}
