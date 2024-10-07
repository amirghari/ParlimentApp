package com.example.parlimentapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parliament_members")
data class ParliamentMemberEntity(
    @PrimaryKey val hetekaId: Int,           // Matches "hetekaId" in JSON
    val firstname: String,                   // Matches "firstname" in JSON
    val lastname: String,                    // Matches "lastname" in JSON
    val party: String,                       // Matches "party" in JSON
    val minister: Boolean,                   // Changed to Boolean to match the "minister" field in JSON
    val seatNumber: Int,                     // Matches "seatNumber" in JSON
    val pictureUrl: String,                  // Matches "pictureUrl" in JSON
    val note: String? = null,                 // Optional note field
    val vote: Int? = null                    // Optional vote field

    )

