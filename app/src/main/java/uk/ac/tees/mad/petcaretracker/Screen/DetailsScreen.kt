package uk.ac.tees.mad.petcaretracker.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.Model.PetData
import uk.ac.tees.mad.petcaretracker.R
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DetailsScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    petData: PetData?
) {
    if (petData != null) {
        AsyncImage(
            File(petData.image.removePrefix("file://")),
            contentDescription = null, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.55f).clip(
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ), contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.systemBarsPadding()) {
            Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = null , tint = Color.White, modifier = Modifier.size(30.dp))
                Text(petData.petName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
                Icon(painterResource( R.drawable.paw), contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
                Column(modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 510.dp).padding(16.dp).clip(RoundedCornerShape(24.dp)).background(Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pet Details", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
                        DetailsText("Species",petData.species)
                        DetailsText("Breed",petData.breed)
                        DetailsText("Date of Birth",petData.dateOfBirth)
                        DetailsText("Gender",petData.gender)
                        DetailsText("Weight",petData.weight+" kg")
                        DetailsText("Notes",petData.notes)
                        DetailsText("Vaccinations",petData.vaccinations.joinToString(", "))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsText(type: String, ans: String){
    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(type, color = Color.Black, fontWeight = FontWeight.SemiBold)
        Text(": ", color = Color.Black)
        Text(ans, color = Color.Black)
    }
}