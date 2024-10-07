package com.example.parlimentapp.network

import com.example.parlimentapp.data.entity.ParliamentMemberEntity
import retrofit2.http.GET

interface ParliamentApiService {
    @GET("~peterh/seating.json")
    suspend fun getParliamentMembers(): List<ParliamentMemberEntity>
}
