package uk.ac.tees.mad.petcaretracker.Screen

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditOff
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.Model.PetData
import uk.ac.tees.mad.petcaretracker.PetNavigation
import uk.ac.tees.mad.petcaretracker.R
import uk.ac.tees.mad.petcaretracker.utils.scheduleDailyNotification
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreen(navController: NavHostController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val user = viewModel.userData.value
    var userName by remember { mutableStateOf(user.fullName) }
    val petData = viewModel.petData.value
    val petFacts = viewModel.facts.value
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val sideBarVisible = remember { mutableStateOf(false) };
    var cameraPermissionGranted by rememberSaveable { mutableStateOf(false) }
    var storagePermissionGranted by rememberSaveable { mutableStateOf(false) }
    var notificationPermissionGranted by rememberSaveable { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        cameraPermissionGranted = permissions[Manifest.permission.CAMERA] == true
        storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionGranted =
                permissions[Manifest.permission.POST_NOTIFICATIONS] == true
        } else {
            notificationPermissionGranted = true
        }
    }

    fun checkPermissions() {
        cameraPermissionGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    fun requestPermissions() {
        val permissionsToRequest = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionsLauncher.launch(permissionsToRequest.toTypedArray())
    }

    LaunchedEffect(Unit) {
        checkPermissions()
        if (!cameraPermissionGranted || !storagePermissionGranted || !notificationPermissionGranted) {
            requestPermissions()
        }
    }

    LaunchedEffect(Unit) {
        checkPermissions()
        if (!cameraPermissionGranted || !storagePermissionGranted || !notificationPermissionGranted) {
            delay(1000)
            requestPermissions()
        }
    }

    LaunchedEffect(notificationPermissionGranted) {
        if (notificationPermissionGranted) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                (context as? Activity)?.startActivity(
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                )
            } else {
                val notificationTimes = listOf(
                    Triple(9, 0, "Good morning! Time for your pet’s breakfast 🐾"),
                    Triple(10, 30, "Walk time! Take your pet out for fresh air 🚶‍♂️"),
                    Triple(18, 0, "Dinner time! Don’t forget to feed your buddy 🍲"),
                    Triple(21, 0, "Evening cuddle reminder 💕 Spend some time together")
                )

                notificationTimes.forEachIndexed { index, (hour, minute, message) ->
                    scheduleDailyNotification(context, hour, minute, message, index)
                }
            }
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
                            contentDescription = null,
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
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painterResource(R.drawable.home_screen),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.blur(4.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(iv)

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)

                ) {
                    Icon(
                        Icons.Rounded.Menu,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                sideBarVisible.value = !sideBarVisible.value
                            }
                            .align(Alignment.CenterStart)
                    )
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "PetCare Tracker",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
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
                        horizontalArrangement = Arrangement.SpaceBetween
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
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
                        Column(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .verticalScroll(scroll)
                        ) {
                            if (petFacts.fact.isNotEmpty()) {
                                Text(
                                    petFacts.fact,
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
                            val encodedJson =
                                URLEncoder.encode(petJson, StandardCharsets.UTF_8.toString())
                            navController.navigate(
                                PetNavigation.DetailsScreen.createRoute(
                                    encodedJson
                                )
                            )
                        }
                    }
                }

            }
            if (sideBarVisible.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable {
                            sideBarVisible.value = false
                        }
                ) {
                    AnimatedVisibility(
                        visible = sideBarVisible.value,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -it }),
                        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.6f)
                                .shadow(8.dp)
                                .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                                .background(Color.White),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 100.dp)
                                    .size(150.dp)
                                    //.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(
                                            topStart = 24.dp,
                                            topEnd = 24.dp
                                        ),

                                        )
                            ) {
                                Image(
                                    painterResource(R.drawable.boy),
                                    contentDescription = null,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            OutlinedTextField(
                                userName,
                                onValueChange = { userName = it },
                                trailingIcon = {
                                    Icon(
                                        Icons.Rounded.Edit,
                                        contentDescription = null
                                    )
                                }, modifier = Modifier.padding(8.dp),
                                shape = RoundedCornerShape(24.dp)
                            )
                            OutlinedTextField(
                                user.email,
                                onValueChange = {},
                                trailingIcon = {
                                    Icon(
                                        Icons.Rounded.EditOff,
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier.padding(8.dp),
                                readOnly = true,
                                shape = RoundedCornerShape(24.dp)
                            )
                            Button(onClick = {
                                viewModel.updateUserData(context,userName)
                            }, modifier = Modifier.fillMaxWidth().padding(8.dp), colors = ButtonDefaults.buttonColors( Color(0xff617cf2)),
                                shape = RoundedCornerShape(13.dp)) {
                                Text("Save")
                            }
                            Button(onClick = {viewModel.logOut()
                                             navController.navigate(PetNavigation.LoginScreen.route){
                                                 popUpTo(0)
                                             }}, modifier = Modifier.fillMaxWidth().padding(8.dp), colors = ButtonDefaults.buttonColors( Color.Red.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(13.dp)) {
                                Text("Logout")
                            }
                        }
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
            .clickable {
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