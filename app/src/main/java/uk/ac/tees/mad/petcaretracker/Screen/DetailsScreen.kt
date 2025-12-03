package uk.ac.tees.mad.petcaretracker.Screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.Model.PetData
import uk.ac.tees.mad.petcaretracker.PetNavigation
import uk.ac.tees.mad.petcaretracker.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    petData: PetData?
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val showAlertDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    var vaccineName by remember { mutableStateOf("") }
    var vaccineDosage by remember { mutableStateOf("") }
    var petName by remember { mutableStateOf(petData?.petName?:"") }
    var petSpecies by remember { mutableStateOf(petData?.species?:"") }
    var petBreed by remember { mutableStateOf( petData?.breed?:"") }
    var petWeight by remember { mutableStateOf( petData?.weight?:"") }
    var petNotes by remember { mutableStateOf( petData?.notes?:"") }

    if (petData != null) {
        AsyncImage(
            File(petData.image.removePrefix("file://")),
            contentDescription = null, modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .clip(
                    RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                ), contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.systemBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.popBackStack()
                        })
                Text(
                    petData.petName,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                )
                Icon(
                    painterResource(R.drawable.paw),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(510.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(elevation = 8.dp)
                        .background(Color.White)
                ) {
                    Column(modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scroll)) {
                        Text(
                            "Pet Details",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(4.dp)
                        )
                        DetailsText("Species", petData.species)
                        DetailsText("Breed", petData.breed)
                        DetailsText("Date of Birth", petData.dateOfBirth)
                        DetailsText("Gender", petData.gender)
                        DetailsText("Weight", petData.weight + " kg")
                        DetailsText("Notes", petData.notes)
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Vaccinations",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,

                                )
                            IconButton(onClick = {
                                showAlertDialog.value = true
                            }) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            }
                        }
                        petData.vaccinations.forEach {
                            Row(
                                modifier = Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.Green),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Done,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                Text(it, color = Color.Black, modifier = Modifier.padding(4.dp))
                            }
                        }
                    }

                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Row() {
                        Button(onClick = {
                            showEditDialog.value = true
                        }, modifier = Modifier.width(150.dp)) {
                            Text("Edit")
                        }
                        Spacer(modifier = Modifier.width(40.dp))
                        Button(
                            onClick = {
                                viewModel.deletePet(context, petData.documentID)
                                navController.popBackStack()
                            },
                            modifier = Modifier.width(150.dp),
                            colors = ButtonDefaults.buttonColors(Color.Red)
                        ) {
                            Text("Delete")
                        }
                    }
                }
                if (showAlertDialog.value) {
                    AlertDialog(onDismissRequest = { showAlertDialog.value = false }) {
                        Column(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(24.dp)
                                )
                                .background(Color.White)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Vaccine Details",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            )
                            OutlinedTextField(
                                value = vaccineName,
                                onValueChange = { vaccineName = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                label = { Text("Vaccine Name") },
                            )
                            OutlinedTextField(
                                value = vaccineDosage,
                                onValueChange = { vaccineDosage = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                label = { Text("Dosage") },
                                trailingIcon = {
                                    Text("ml")
                                }
                            )
                            Button(
                                onClick = {
                                    viewModel.addVaccine(
                                        context,
                                        petData.documentID,
                                        vaccineName,
                                        vaccineDosage,
                                        onSuccess = {
                                            navController.popBackStack()
                                            Log.d("Vaccine", "Vaccine added successfully")
                                        }
                                    )
                                    vaccineName = ""
                                    vaccineDosage = ""
                                    showAlertDialog.value = false
                                },
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
                if (showEditDialog.value) {
                    AlertDialog(onDismissRequest = { showEditDialog.value = false }) {
                        Column(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(24.dp)
                                )
                                .background(Color.White)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Edit Details",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(petName, onValueChange = {petName = it}, label = {Text("Pet Name")},shape = RoundedCornerShape(24.dp),)
                            OutlinedTextField(petSpecies, onValueChange = {petSpecies = it}, label = {Text("Species")},shape = RoundedCornerShape(24.dp),)
                            OutlinedTextField(petBreed, onValueChange = {petBreed = it}, label = {Text("Breed")},shape = RoundedCornerShape(24.dp),)
                            OutlinedTextField(petWeight, onValueChange = {petWeight = it}, label = {Text("Weight")},shape = RoundedCornerShape(24.dp), trailingIcon = {Text("kg")})
                            OutlinedTextField(petNotes, onValueChange = {petNotes = it}, label = {Text("Notes")},shape = RoundedCornerShape(24.dp),)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.updatePet(
                                        context,
                                        petData.documentID,
                                        petName,
                                        petBreed,
                                        petSpecies,
                                        petWeight,
                                        petNotes,
                                        onSuccess={
                                            navController.popBackStack()
                                        }
                                    )
                                    showEditDialog.value = false
                                },
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "PetCareTracker – Pet Details Screen")
@Composable
fun DetailsScreenPreview() {
    val samplePet = PetData(
        petName = "Buddy",
        species = "Dog",
        breed = "Golden Retriever",
        dateOfBirth = "12 Mar 2021",
        gender = "Male",
        weight = "32",
        notes = "Very friendly, loves walks and treats. Allergic to chicken.",
        vaccinations = listOf("Rabies - 1ml", "DHPP - 2ml", "Bordetella - 0.5ml"),
        image = "https://images.unsplash.com/photo-1543466835-00a7907e715e?w=800",
        documentID = "1"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = samplePet.image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.systemBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    samplePet.petName,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                )
                Icon(
                    painterResource(R.drawable.paw),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(510.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(elevation = 8.dp)
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            "Pet Details",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(4.dp)
                        )

                        DetailsText("Species", samplePet.species)
                        DetailsText("Breed", samplePet.breed)
                        DetailsText("Date of Birth", samplePet.dateOfBirth)
                        DetailsText("Gender", samplePet.gender)
                        DetailsText("Weight", "${samplePet.weight} kg")
                        DetailsText("Notes", samplePet.notes)

                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Vaccinations", fontWeight = FontWeight.SemiBold, color = Color.Black)
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                            }
                        }

                        samplePet.vaccinations.forEach { vaccine ->
                            Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color.Green),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Done, contentDescription = null, tint = Color.White)
                                }
                                Text(vaccine, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {}, modifier = Modifier.width(150.dp)) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.width(150.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsText(type: String, ans: String) {
    Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("$type: ", color = Color.Black, fontWeight = FontWeight.SemiBold)
        Text(ans, color = Color.Black)
    }
}