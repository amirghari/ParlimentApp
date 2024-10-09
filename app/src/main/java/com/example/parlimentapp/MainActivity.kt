package com.example.parlimentapp

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

import com.example.parlimentapp.data.database.ParliamentDatabase
import com.example.parlimentapp.data.entity.ParliamentMemberEntity
import com.example.parlimentapp.network.NetworkModule
import com.example.parlimentapp.ui.theme.ParlimentAppTheme



class MainActivity : ComponentActivity() {

    private val viewModel: ParliamentViewModel by viewModels {
        val database = ParliamentDatabase.getDatabase(applicationContext)
        val apiService = NetworkModule.apiService
        ParliamentViewModelFactory(database.parliamentMemberDao(), apiService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ParlimentAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ParliamentApp(viewModel = viewModel)
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ParliamentApp(viewModel: ParliamentViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchAndSaveMembersIfNeeded()
    }
    val pageNumber = viewModel.pageNumber.collectAsState()
    Log.d("UI", "Current page number: ${pageNumber.value}")
    when (pageNumber.value) {
        1 -> {
            val partiesState = viewModel.parties.collectAsState(initial = emptySet())
            LaunchedEffect(Unit) {
                viewModel.getParties()  // Fetch parties when on page 1
            }
            FirstScreen(
                onClick = { viewModel.updatePageNumber(2) },
                onValueChange = { viewModel.updatePartyName(it) },
                parties = partiesState.value
            )
        }
        2 -> {
            val membersState = viewModel.members.collectAsState(initial = emptyList())
            LaunchedEffect(viewModel.partyName.value) {
                viewModel.getMembersByParty(viewModel.partyName.value)  // Fetch members for the selected party
            }
            SecondScreen(
                onClick = { viewModel.updatePageNumber(3) },
                partyName = viewModel.partyName.value,
                onValueChange = { viewModel.updateMemberName(it) },
                members = membersState.value
            )
        }
        3 -> {
            val memberDetails = viewModel.targetMember.collectAsState(initial = null)
            LaunchedEffect(viewModel.selectedMemberName.value) {
                viewModel.getMemberDetails(viewModel.selectedMemberName.value)  // Fetch selected member details
            }
            ThirdScreen(
                onClick = { viewModel.updatePageNumber(1) },
                targetMember = memberDetails.value,
                updateMember = { updatedMember ->
                    // Call the ViewModel function to update the member in the database
                    viewModel.updateMember(updatedMember)
                },
                viewModel = viewModel
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreen(
    onClick: () -> Unit,
    onValueChange: (String) -> Unit,
    parties: Set<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        Text(
            text = "Select a party:",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        parties.forEach { party ->
            Card(
                onClick = {
                    onClick()
                    onValueChange(party)
                },
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = party,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SecondScreen(
    onClick: () -> Unit,
    partyName: String,
    onValueChange: (String) -> Unit,
    members: List<ParliamentMemberEntity>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Members of $partyName party:",
            style = TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        members.forEach { member ->
            Button(
                onClick = {
                    onClick()
                    onValueChange(member.lastname)
                },
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .width(300.dp)
            ) {
                Text(
                    text = "${member.firstname} ${member.lastname}",
                    style = TextStyle(
                        fontSize = 20.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdScreen(
    onClick: () -> Unit,
    targetMember: ParliamentMemberEntity?,
    updateMember: (ParliamentMemberEntity) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParliamentViewModel
) {
    // State variables for note and vote
    var note by remember { mutableStateOf("") }
    var vote by remember { mutableStateOf(0f) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }


    // Update the state when the targetMember changes
    LaunchedEffect(targetMember) {
        if (targetMember != null) {
            note = targetMember.note ?: ""  // Default to empty string if null
            vote = targetMember.vote?.toFloat() ?: 0f  // Default to 0 if null
        }
        targetMember?.pictureUrl?.let { pictureUrl ->
            viewModel.fetchMemberImage(
                pictureUrl,
                onSuccess = { responseBody ->
                    val inputStream = responseBody.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageBitmap = bitmap // Update the state with the fetched image
                },
                onError = { errorMessage ->
                    Log.e("ThirdScreen", errorMessage)
                }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!.asImageBitmap(),
                contentDescription = "Member Image",
                modifier = Modifier
                    .size(300.dp)
                    .padding(20.dp)
                    .clip(CircleShape)
            )

        } else {
            Text("Loading image...")
        }
        Text(
            text = "${targetMember?.firstname ?: ""} ${targetMember?.lastname ?: ""}",
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Party: ${targetMember?.party ?: "N/A"}",
            style = TextStyle(
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Seat number: ${targetMember?.seatNumber ?: "N/A"}",
            style = TextStyle(
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Id number: ${targetMember?.hetekaId ?: "N/A"}",
            style = TextStyle(
                fontSize = 20.sp
            )
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Minister: ${targetMember?.minister ?: "N/A"}",
            style = TextStyle(
                fontSize = 20.sp
            )
        )

        // Input for Note
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = note,
            onValueChange = { note = it },
            label = { Text(text = "Add a note") },
            placeholder = { Text(text = "Enter your note here") },
            modifier = Modifier.fillMaxWidth()
        )

        // Input for Vote
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Vote: ${vote.toInt()}")
        Slider(
            value = vote,
            onValueChange = { vote = it },
            valueRange = 0f..10f, // Assume votes are between 0 and 10
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            val updatedMember = targetMember?.copy(note = note, vote = vote.toInt())
            if (updatedMember != null) {
                updateMember(updatedMember)
            }
            onClick() // Navigate back to the previous screen
        }) {
            Text(text = "Save & Back")
        }
    }
}

