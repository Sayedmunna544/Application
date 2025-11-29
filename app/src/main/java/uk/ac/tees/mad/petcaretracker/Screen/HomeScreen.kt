package uk.ac.tees.mad.petcaretracker.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.launch
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.Model.PetData
import uk.ac.tees.mad.petcaretracker.PetNavigation
import uk.ac.tees.mad.petcaretracker.R
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val petData = viewModel.petData.value
    val petFacts = viewModel.facts.value
    val scope = rememberCoroutineScope()
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var storagePermissionGranted by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            cameraPermissionGranted = isGranted
        }
    )

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true &&
                    permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationPermissionGranted = isGranted
    }

    fun checkPermissions() {
        cameraPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }

        notificationPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_NOTIFICATION_POLICY
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storagePermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            storagePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        notificationPermissionLauncher.launch(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
    }

    LaunchedEffect(Unit) {
        checkPermissions()
        if (!cameraPermissionGranted || !storagePermissionGranted || !notificationPermissionGranted) {
            requestPermissions()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(PetNavigation.CreateScreen.route)
                }, shape = RoundedCornerShape(24.dp), containerColor = Color(0xff617cf2)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Logout",
                            tint = Color(0xff617cf2)
                        )
                    }
                    Text(
                        "Add New Pet",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }, floatingActionButtonPosition = FabPosition.Center
    ) { iv ->
        Image(
            painterResource(R.drawable.home_screen),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.blur(4.dp)
        )
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(iv)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "PetCare Tracker",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(24.dp)
                    )
                    .background(Color(0xffc7ebe6))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text("Random Pet Fact", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            scope.launch {
                                viewModel.fetchFacts()
                            }
                        })
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xfffcb695)), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(R.drawable.paw),
                            contentDescription = null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        if (petFacts.data.isNotEmpty()) {
                            Text(
                                petFacts.data[0],
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        } else {
                            Text("Loading...")
                        }
                    }

                }
            }

            Text(
                "My Pets",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 12.dp, top = 12.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                items(petData) { pet ->
                    PetCard(pet) {
                        val gson = Gson()
                        val petJson = gson.toJson(pet)
                        val encodedJson = URLEncoder.encode(petJson, StandardCharsets.UTF_8.toString())
                        navController.navigate(PetNavigation.DetailsScreen.createRoute(encodedJson))
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(data: PetData, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xfffcb695))
            .clickable{
                onClick()
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            AsyncImage(
                model = File(data.image.removePrefix("file://")),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = data.petName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(text = data.species)
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = data.breed)
                Text(text = data.dateOfBirth)
            }
        }
    }
}