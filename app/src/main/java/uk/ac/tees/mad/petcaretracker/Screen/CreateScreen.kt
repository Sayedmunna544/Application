package uk.ac.tees.mad.petcaretracker.Screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.BrowseGallery
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.PetNavigation
import uk.ac.tees.mad.petcaretracker.R
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(navController: NavHostController, viewModel: MainViewModel) {
    val isLoading = viewModel.loading.value
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
    var vaccineName by remember { mutableStateOf("")}
    var vaccineDosage by remember { mutableStateOf("")}
    val showAlertDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val showDatePickerDialog = remember {
        mutableStateOf(false)
    }

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
                    .padding(8.dp)
                    .size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap.value != null) {
                    Image(
                        bitmap = imageBitmap.value!!.asImageBitmap(),
                        contentDescription = "Selected pet image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape).border(1.dp, Color.Black, CircleShape),
                        contentScale = ContentScale.FillBounds
                    )
                } else {
                    Image(
                        painterResource(R.drawable.background_image),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape).border(1.dp, Color.Black, CircleShape),
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
                onValueChange = { },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    Icon(
                        Icons.Rounded.DateRange,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            showDatePickerDialog.value = true
                        }
                    )
                }
            )
            if (showDatePickerDialog.value) {
                DatePickerDialog(onDismissRequest = { showDatePickerDialog.value = false }, confirmButton = {
                    Button(onClick = {
                        dateOfBirth = "${datePickerState.selectedDateMillis}"
                        if (datePickerState.selectedDateMillis != null) {
                            val fomatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                            dateOfBirth = fomatter.format(datePickerState.selectedDateMillis!!)
                        }
                        showDatePickerDialog.value = false
                    }) {
                        Text("Confirm")
                    }
                }) {
                    DatePicker(state = datePickerState)
                }
            }
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
                shape = RoundedCornerShape(24.dp),
                minLines = 3
            )
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    Text("Vaccinations:")
                    Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.clickable {
                        showAlertDialog.value = true
                        }
                    )
                }
                vaccinations.forEach {
                    Text(it)
                    Divider()
                }
            }
            if (showAlertDialog.value) {
                AlertDialog(onDismissRequest = { showAlertDialog.value = false }) {
                    Column(
                        modifier = Modifier.clip(
                            RoundedCornerShape(24.dp)
                        ).background(Color.White).padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Vaccine Details", fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
                        OutlinedTextField(
                            value = vaccineName,
                            onValueChange = { vaccineName = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            label = { Text("Vaccine Name") }
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
                        Button(onClick = {
                            vaccinations.add("$vaccineName - $vaccineDosage")
                            vaccineName = ""
                            vaccineDosage = ""
                            showAlertDialog.value = false
                        }, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text("Add")
                        }
                    }
                }
            }
            Button(onClick = {
                if (imageBitmap.value != null && petName.isNotEmpty() && species.isNotEmpty() && breed.isNotEmpty() && dateOfBirth.isNotEmpty() && gender.isNotEmpty() && weight.isNotEmpty() && notes.isNotEmpty()) {
                    viewModel.savePetData(context, imageBitmap.value!!, petName, species, breed, dateOfBirth, gender, weight, notes, vaccinations, onSuccess = {
                        navController.navigate(PetNavigation.HomeScreen.route){
                            popUpTo(0)
                        }
                    })
                }else{
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }
            }, enabled = !isLoading, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), shape = RoundedCornerShape(24.dp)) {
                if (isLoading) {
                    Text("Loading...")
                } else {
                    Text("Save and Continue")
                }
            }
        }
    }
}
