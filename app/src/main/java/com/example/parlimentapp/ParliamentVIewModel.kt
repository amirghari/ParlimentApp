package com.example.parlimentapp


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.parlimentapp.data.ParliamentMembersData

class ParliamentViewModel : ViewModel() {
    var pageNumber = mutableStateOf(1)
    var partyName = mutableStateOf("")
    var member = mutableStateOf("")

    fun updatePageNumber(newPage: Int) {
        pageNumber.value = newPage
    }

    fun updatePartyName(newParty: String) {
        partyName.value = newParty
    }

    fun updateMemberName(newMember: String) {
        member.value = newMember
    }

    // Logic to get unique parties
    fun getParties(): Set<String> {
        return ParliamentMembersData.members.map { it.party }.toSet()
    }

    // Logic to get members of a specific party
    fun getMembersByParty(partyName: String) =
        ParliamentMembersData.members.filter { it.party == partyName }.sortedBy { it.lastname }

    // Logic to get details of a specific member
    fun getMemberDetails(memberLastName: String) =
        ParliamentMembersData.members.find { it.lastname == memberLastName }
}
