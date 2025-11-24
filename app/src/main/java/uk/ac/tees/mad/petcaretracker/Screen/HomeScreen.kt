package uk.ac.tees.mad.petcaretracker.Screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.Model.PetData
import uk.ac.tees.mad.petcaretracker.PetNavigation
import uk.ac.tees.mad.petcaretracker.R
import java.io.File

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val petData = viewModel.petData.value
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
            ) {
                Text("PetCare Tracker", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(12.dp))
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                items(petData.chunked(3)) { chunk ->
                    Column(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .fillMaxHeight()
                    ) {
                        chunk.forEach { pet ->
                            PetCard(pet)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(data: PetData) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        AsyncImage(
            model = File(data.image.removePrefix("file://")),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            data.petName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}