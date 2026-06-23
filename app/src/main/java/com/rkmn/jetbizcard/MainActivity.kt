package com.rkmn.jetbizcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkmn.jetbizcard.ui.theme.JetBizCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetBizCardTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(text = "JetBizCard", color = Color(0xFFFFFFFF))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                Color(0xFFAB9261)
                            )
                        )
                    }) { innerPadding ->
                    BizCard(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BizCard(modifier: Modifier = Modifier) {
    val buttonClickedState = remember {
        mutableStateOf(value = false)
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(corner = CornerSize(15.dp)),
            colors = CardDefaults.cardColors(Color(0xFFF6F1E8)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ImageProfile()
                HorizontalDivider(
                    modifier = Modifier.padding(top = 16.dp),
                    color = Color(0xFFAB9261)
                )
                CardInfo()
                Button(
                    modifier = Modifier.padding(top = 5.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFAB9261)),
                    onClick = {
//                        Log.d("clicked", "BizCard Clicked")
                        buttonClickedState.value = !buttonClickedState.value
                    }
                ) {
                    Text(
                        text = "Portfolio",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                if (buttonClickedState.value) {
                    Content()
                } else {
                    Box { }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Content() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(5.dp)
    ) {
        Surface(
            modifier = Modifier
                .padding((3.dp))
                .fillMaxWidth()
                .fillMaxHeight(),
            RoundedCornerShape(corner = CornerSize(6.dp)),
            border = BorderStroke(width = 2.dp, color = Color(0xFFAB9261))
        ) {
            Portfolio(
                data = listOf<String>(
                    "Project 1",
                    "Project 2",
                    "Project 3",
                    "Project 4",
                    "Project 5"
                )
            )
        }
    }
}

@Composable
fun Portfolio(data: List<String>) {
    LazyColumn() {
        items(data) { item ->
            Card(
                modifier = Modifier
                    .padding(13.dp)
                    .fillMaxWidth(),
                shape = RectangleShape,
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
//                        .padding((8.dp))
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(7.dp)
                ) {
                    ImageProfile(modifier = Modifier.size(100.dp))
                    Column(
                        modifier = Modifier
                            .padding(7.dp)
                            .align(alignment = Alignment.CenterVertically)
                    ) {
                        Text(text = item, fontWeight = FontWeight.Bold)
                        Text(
                            text = "A Great Project",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageProfile(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .size(150.dp)
            .padding(5.dp),
        shape = CircleShape,
        border = BorderStroke(
            0.5.dp,
            Color.LightGray
        ),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_picture),
            contentDescription = "Profile Image",
            modifier = modifier.size(135.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun CardInfo() {
    Column(modifier = Modifier.padding(5.dp)) {
        Text(
            text = "Captain Orion",
            modifier = Modifier.padding(top = 3.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFAB9261),
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "The Super Human Soldier",
            modifier = Modifier.padding(3.dp),
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFAB9261),
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "@captainorion",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline
        )
    }
}

//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetBizCardTheme {
        BizCard()
    }
}

