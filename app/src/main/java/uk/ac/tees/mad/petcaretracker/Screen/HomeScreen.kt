package uk.ac.tees.mad.petcaretracker.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.PetNavigation
import uk.ac.tees.mad.petcaretracker.R

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = {

                navController.navigate(PetNavigation.CreateScreen.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Logout")
            }
        }) { iv ->
        Image(
            painterResource(R.drawable.home_screen),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.blur(4.dp)
        )
        Column(modifier = Modifier.fillMaxSize().padding(iv)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp, // Adjust elevation for shadow intensity
                        shape = RectangleShape,
                        clip = false, // Set to false to allow shadow to extend outside bounds
                        spotColor = Color.LightGray,
                        ambientColor = Color.LightGray
                    )
                    .offset(y = (-2).dp) // Slightly shift up to make shadow appear below
                    .background(Color.Transparent) // Ensure background to prevent shadow overlap
                    .clipToBounds() // Clip content to prevent shadow from appearing inside
            ) {
                Text("PetCare Tracker", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(12.dp))
            }
        }
    }
}