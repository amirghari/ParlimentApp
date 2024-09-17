package com.example.parlimentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parlimentapp.data.ParliamentMembersData
import com.example.parlimentapp.ui.theme.ParlimentAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParlimentAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                )
                {
                    ParliamentApp()
                }
            }
        }
    }
}

@Composable
fun ParliamentApp(modifier: Modifier = Modifier) {
    var pageNumber by remember { mutableStateOf(1) }
    var partyName by remember { mutableStateOf("") }
    var member by remember { mutableStateOf("") }
    when (pageNumber) {
        1 -> {
            FirstScreen(modifier = Modifier,
                onClick = { pageNumber = 2 },
                onValueChange = { partyName = it }
            )
        }

        2 -> {
            SecondScreen(modifier = Modifier,
                onClick = { pageNumber = 3 },
                partyName = partyName,
                onValueChange = { member = it })
        }
        3 -> {
            ThirdScreen(modifier = Modifier,
                onClick = { pageNumber = 1 },
                member = member)
        }
    }
}
@Composable
fun FirstScreen
            (
    modifier: Modifier,
    onClick: () -> Unit,
    onValueChange: (String) -> Unit
)
{
    val parties : Set<String> = ParliamentMembersData.members.map { it.party }.toSet()
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Select a party: ",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = modifier.padding(10.dp))
        parties.map {
            Card(
                onClick = {
                    onClick()
                    onValueChange(it)
                },
                modifier = modifier
                    .padding(vertical = 6.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(200.dp)
                    ,
            ) {
                Text(text = it,
                    modifier = modifier.padding(10.dp)
                        .align(Alignment.CenterHorizontally),
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = modifier.padding(10.dp))
                onValueChange(it)
            } }
    }
}
@Composable
fun SecondScreen
            (modifier: Modifier,
             onClick: () -> Unit,
             partyName: String,
             onValueChange: (String) -> Unit
        )
{
    val members = ParliamentMembersData.members.filter { it.party == partyName }.sortedBy { it.lastname }
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,

        )

    {
        Spacer(modifier = modifier.padding(20.dp))
        Text("Members of $partyName party: ",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = modifier.padding(10.dp),)
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
                Text(text = "${it.firstname} ${it.lastname}",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp)
                    )
                Spacer(modifier = modifier.padding(5.dp))
            }
        }


    }
}
@Composable
fun ThirdScreen
            (modifier: Modifier,
             onClick: () -> Unit,
             member: String)
            {
                val targetMember = ParliamentMembersData.members.find {it.lastname == member}

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                    )
                {
                    Spacer(modifier = modifier.padding(20.dp))
                    Image(painter = painterResource(id = R.drawable.user),
                          contentDescription = "UserAvatar",
                          modifier = modifier.padding(20.dp))
                    Text(text = "${targetMember?.firstname} ${targetMember?.lastname}",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = modifier.padding(20.dp))
                    Text(text = "Party: ${targetMember?.party}",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp)
                    )
                    Spacer(modifier = modifier.padding(5.dp))
                    Text(text = "Seat number: ${targetMember?.seatNumber}",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp)
                    )
                    Spacer(modifier = modifier.padding(5.dp))
                    Text(text = "Id number: ${targetMember?.hetekaId}",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp)
                    )
                    Spacer(modifier = modifier.padding(5.dp))
                    Text(text = "Minister: ${targetMember?.minister}",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 20.sp)
                    )
                    Spacer(modifier = modifier.padding(20.dp))
                    Button(onClick = { onClick() }) {
                        Text(text = "Back")
                    }
                    Spacer(modifier = modifier.padding(20.dp))

                }

            }


