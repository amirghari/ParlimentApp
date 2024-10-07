package com.example.parlimentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parlimentapp.data.ParliamentMembersData
import com.example.parlimentapp.ui.theme.ParlimentAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ParliamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParlimentAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ParliamentApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ParliamentApp(viewModel: ParliamentViewModel) {
    when (viewModel.pageNumber.value) {
        1 -> {
            FirstScreen(
                modifier = Modifier,
                onClick = { viewModel.updatePageNumber(2) },
                onValueChange = { viewModel.updatePartyName(it) },
                parties = viewModel.getParties()
            )
        }

        2 -> {
            SecondScreen(
                modifier = Modifier,
                onClick = { viewModel.updatePageNumber(3) },
                partyName = viewModel.partyName.value,
                onValueChange = { viewModel.updateMemberName(it) },
                members = viewModel.getMembersByParty(viewModel.partyName.value)
            )
        }

        3 -> {
            ThirdScreen(
                modifier = Modifier,
                onClick = { viewModel.updatePageNumber(1) },
                member = viewModel.member.value,
                targetMember = viewModel.getMemberDetails(viewModel.member.value)
            )
        }
    }
}

@Composable
fun FirstScreen(
    modifier: Modifier,
    onClick: () -> Unit,
    onValueChange: (String) -> Unit,
    parties: Set<String>
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select a party: ",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = modifier.padding(10.dp))

        parties.forEach { party ->
            Card(
                onClick = {
                    onClick()
                    onValueChange(party)
                },
                modifier = modifier
                    .align(Alignment.CenterHorizontally)
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
                        style = androidx.compose.ui.text.TextStyle(
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
    modifier: Modifier,
    onClick: () -> Unit,
    partyName: String,
    onValueChange: (String) -> Unit,
    members: List<ParliamentMembersData.ParliamentMember>
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = modifier.padding(20.dp))
        Text(
            "Members of $partyName party: ",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = modifier.padding(10.dp))
        members.forEach {
            Button(
                onClick = {
                    onClick()
                    onValueChange(it.lastname)
                },
                modifier = modifier
                    .padding(vertical = 5.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(300.dp),
            ) {
                Text(
                    text = "${it.firstname} ${it.lastname}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = modifier.padding(5.dp))
            }
        }
    }
}

@Composable
fun ThirdScreen(
    modifier: Modifier,
    onClick: () -> Unit,
    member: String,
    targetMember: ParliamentMembersData.ParliamentMember?
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = modifier.padding(20.dp))
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = "UserAvatar",
            modifier = modifier.padding(20.dp)
        )
        Text(
            text = "${targetMember?.firstname} ${targetMember?.lastname}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = modifier.padding(20.dp))
        Text(
            text = "Party: ${targetMember?.party}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp
            )
        )
        Spacer(modifier = modifier.padding(5.dp))
        Text(
            text = "Seat number: ${targetMember?.seatNumber}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp
            )
        )
        Spacer(modifier = modifier.padding(5.dp))
        Text(
            text = "Id number: ${targetMember?.hetekaId}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp
            )
        )
        Spacer(modifier = modifier.padding(5.dp))
        Text(
            text = "Minister: ${targetMember?.minister}",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp
            )
        )
        Spacer(modifier = modifier.padding(20.dp))
        Button(onClick = { onClick() }) {
            Text(text = "Back")
        }
        Spacer(modifier = modifier.padding(20.dp))
    }
}
