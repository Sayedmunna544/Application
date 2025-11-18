package uk.ac.tees.mad.petcaretracker.Screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.BrowseGallery
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.R
import java.io.InputStream

@Composable
fun CreateScreen(navController: NavHostController, viewModel: MainViewModel) {
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var petName by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var showsSpeciesDropdown by remember { mutableStateOf(false) }
    val speciesList = listOf(
        "Dog",
        "Cat",
        "Bird",
        "Fish",
        "Reptile",
        "Amphibian",
        "Other"
    )
    var breed by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var showsGenderDropdown by remember { mutableStateOf(false) }
    val genderList = listOf(
        "Male",
        "Female",
        "Other"
    )
    var dateOfBirth by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val vaccinations = remember { mutableStateListOf<String>() }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                imageBitmap.value = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            imageBitmap.value = bitmap
        }
    }

    val scroll = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(horizontal = 10.dp)
        ) {
            Icon(
                Icons.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        navController.popBackStack()
                    })
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Add Pet", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap.value != null) {
                    Image(
                        bitmap = imageBitmap.value!!.asImageBitmap(),
                        contentDescription = "Selected pet image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Image(
                        painterResource(R.drawable.background_image),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = { cameraLauncher.launch(null) }) {
                Icon(Icons.Rounded.Camera, contentDescription = null)
            }
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Icon(Icons.Rounded.BrowseGallery, contentDescription = null)
            }
        }
        Divider()
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).verticalScroll(scroll)) {
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text("Pet Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            Box {
                OutlinedTextField(
                    value = species,
                    onValueChange = {},
                    label = { Text("Species") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showsSpeciesDropdown = true },
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                showsSpeciesDropdown = !showsSpeciesDropdown
                            }
                        )
                    }
                )
                DropdownMenu(
                    shape = RoundedCornerShape(24.dp),
                    expanded = showsSpeciesDropdown,
                    onDismissRequest = { showsSpeciesDropdown = false },
                    modifier = Modifier
                        .width(200.dp)
                        .background(Color.White)
                        .border(1.dp, Color.Gray, RoundedCornerShape(24.dp))
                ) {
                    speciesList.forEach { speciesItem ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    speciesItem,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            },
                            onClick = {
                                species = speciesItem
                                showsSpeciesDropdown = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            Box {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Gender") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showsSpeciesDropdown = true },
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        Icon(
                            Icons.Rounded.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                showsGenderDropdown = !showsGenderDropdown
                            }
                        )
                    }
                )
                DropdownMenu(
                    shape = RoundedCornerShape(24.dp),
                    expanded = showsGenderDropdown,
                    onDismissRequest = { showsGenderDropdown = false },
                    modifier = Modifier
                        .width(200.dp)
                        .background(Color.White)
                        .border(1.dp, Color.Gray, RoundedCornerShape(24.dp))
                ) {
                    genderList.forEach { speciesItem ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    speciesItem,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            },
                            onClick = {
                                gender = speciesItem
                                showsGenderDropdown = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    Text("kg")
                }
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text("Pet Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}
