package com.example.parlimentapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.parlimentapp.data.entity.ParliamentMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ParliamentMemberDao {

    @Query("SELECT * FROM parliament_members")
    fun getAllMembers(): Flow<List<ParliamentMemberEntity>>

    @Query("SELECT * FROM parliament_members WHERE party = :partyName")
    fun getMembersByParty(partyName: String): Flow<List<ParliamentMemberEntity>>

    @Query("SELECT DISTINCT party FROM parliament_members")
    fun getDistinctParties(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllMembers(members: List<ParliamentMemberEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMember(member: ParliamentMemberEntity)
    @Update
    fun updateMember(member: ParliamentMemberEntity)
}
