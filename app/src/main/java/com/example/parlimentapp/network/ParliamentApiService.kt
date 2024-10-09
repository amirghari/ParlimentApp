package com.example.parlimentapp.network

import com.example.parlimentapp.data.entity.ParliamentMemberEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ParliamentApiService {
    @GET("~peterh/seating.json")
    suspend fun getParliamentMembers(): List<ParliamentMemberEntity>
    @GET
    suspend fun getMemberImage(@Url fullUrl: String): Response<ResponseBody>
}

