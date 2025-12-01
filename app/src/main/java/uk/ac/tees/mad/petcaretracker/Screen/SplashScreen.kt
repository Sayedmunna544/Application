package uk.ac.tees.mad.petcaretracker.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.R

@Composable
fun SplashScreen(navController: NavHostController, innerPadding: PaddingValues, viewModel: MainViewModel) {

    val isUserLoggedIn = viewModel._isLoggedIn.value
    LaunchedEffect(isUserLoggedIn) {
        delay(3000L)
        if (isUserLoggedIn) {
            navController.navigate("home_screen") {
                popUpTo(0)
            }
        }else{
            navController.navigate("login_screen") {
                popUpTo(0)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.app_icon), contentDescription = null,
            modifier = Modifier
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(42.dp))
                .size(184.dp)
                .clip(RoundedCornerShape(42.dp))
                .border(2.dp, color = Color.Black, shape = RoundedCornerShape(42.dp))


        )
    }
}