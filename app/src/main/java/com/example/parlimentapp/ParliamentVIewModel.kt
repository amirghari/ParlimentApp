package com.example.parlimentapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parlimentapp.data.dao.ParliamentMemberDao
import com.example.parlimentapp.data.entity.ParliamentMemberEntity
import com.example.parlimentapp.network.NetworkModule
import com.example.parlimentapp.network.ParliamentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class ParliamentViewModel(
    private val dao: ParliamentMemberDao,
    private val apiService: ParliamentApiService
) : ViewModel() {
    init {
        fetchAndSaveMembersIfNeeded()
        // This will be called when the ViewModel is initialized
    }

    private val _pageNumber = MutableStateFlow(1)
    val pageNumber: StateFlow<Int> = _pageNumber.asStateFlow()

    private val _partyName = MutableStateFlow("")
    val partyName: StateFlow<String> = _partyName.asStateFlow()

    private val _selectedMemberName = MutableStateFlow("")
    val selectedMemberName: StateFlow<String> = _selectedMemberName.asStateFlow()

    private val _parties = MutableStateFlow(emptySet<String>())
    val parties: StateFlow<Set<String>> = _parties.asStateFlow()

    private val _members = MutableStateFlow(emptyList<ParliamentMemberEntity>())
    val members: StateFlow<List<ParliamentMemberEntity>> = _members.asStateFlow()

    private val _targetMember = MutableStateFlow<ParliamentMemberEntity?>(null)
    val targetMember: StateFlow<ParliamentMemberEntity?> = _targetMember.asStateFlow()

    private val TAG = "ParliamentViewModel"  // Tag for logging

    fun updatePageNumber(newPage: Int) {
        Log.d(TAG, "updatePageNumber: Changing page number to $newPage")
        _pageNumber.value = newPage
    }

    fun updatePartyName(newParty: String) {
        Log.d(TAG, "updatePartyName: New party selected: $newParty")
        _partyName.value = newParty
    }

    fun updateMemberName(newMemberName: String) {
        Log.d(TAG, "updateMemberName: New member selected: $newMemberName")
        _selectedMemberName.value = newMemberName
    }

    fun getParties() {
        viewModelScope.launch(Dispatchers.IO) {   // Ensure this uses IO dispatcher
            dao.getDistinctParties().collect { result: List<String> ->
                _parties.value = result.toSet()
            }
        }
    }

    fun getMembersByParty(partyName: String) {
        viewModelScope.launch(Dispatchers.IO) {  // Ensure this uses IO dispatcher
            dao.getMembersByParty(partyName).collect { result ->
                _members.value = result
            }
        }
    }



    fun getMemberDetails(memberLastName: String) {
        viewModelScope.launch {
            Log.d(TAG, "Fetching details for member: $memberLastName")
            dao.getAllMembers().collect { membersList ->
                _targetMember.value = membersList.find { it.lastname == memberLastName }
                Log.d(TAG, "Target member: ${_targetMember.value}")
            }
        }
    }

    fun fetchAndSaveMembersIfNeeded() {
        viewModelScope.launch(Dispatchers.IO) {
            val members = dao.getAllMembers().first()  // Get the current members in the database
            if (members.isEmpty()) {
                Log.d(TAG, "fetchAndSaveMembersIfNeeded: Database is empty, fetching from API")
                fetchAndSaveMembers()
            } else {
                Log.d(TAG, "fetchAndSaveMembersIfNeeded: Database already has members, skipping fetch")
            }
        }
    }

    fun updateMember(member: ParliamentMemberEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateMember(member)
            Log.d(TAG, "Member updated with note: ${member.note}, vote: ${member.vote}")
        }
    }



    fun fetchAndSaveMembers() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "fetchAndSaveMembers: Starting to fetch members from the API")
            try {
                val members = apiService.getParliamentMembers()
                Log.d(TAG, "fetchAndSaveMembers: Fetched ${members.size} members")
                members.forEach { member ->
                    dao.insertMember(member)
                }
                Log.d(TAG, "fetchAndSaveMembers: Members successfully saved to the database")
            } catch (e: Exception) {
                Log.e(TAG, "fetchAndSaveMembers: Error fetching members: ${e.message}", e)
            }
        }
    }


    fun fetchMemberImage(pictureUrl: String, onSuccess: (ResponseBody) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fullUrl = "https://avoindata.eduskunta.fi/$pictureUrl"
                val response = NetworkModule.imageApiService.getMemberImage(fullUrl)

                if (response.isSuccessful && response.body() != null) {
                    // Image fetched successfully, call onSuccess with the response body
                    onSuccess(response.body()!!)
                    Log.d(TAG, "Image fetched successfully from $fullUrl")
                } else {
                    // Handle the case where the response is not successful
                    onError("Failed to fetch image: ${response.code()}")
                    Log.e(TAG, "Failed to fetch image: ${response.code()}")
                }
            } catch (e: Exception) {
                // Handle exceptions
                onError("Error fetching image: ${e.message}")
                Log.e(TAG, "Error fetching image: ${e.message}", e)
            }
        }
    }
}
