package uk.ac.tees.mad.petcaretracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.petcaretracker.Model.PetData
import uk.ac.tees.mad.petcaretracker.Screen.CreateScreen
import uk.ac.tees.mad.petcaretracker.Screen.DetailsScreen
import uk.ac.tees.mad.petcaretracker.Screen.HomeScreen
import uk.ac.tees.mad.petcaretracker.Screen.LoginScreen
import uk.ac.tees.mad.petcaretracker.Screen.RegisterScreen
import uk.ac.tees.mad.petcaretracker.Screen.SplashScreen
import uk.ac.tees.mad.petcaretracker.ui.theme.PetCareTrackerTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetCareTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(innerPadding)
                }
            }
        }
    }
}

sealed class PetNavigation(val route: String) {
    object SplashScreen : PetNavigation("splash_screen")
    object LoginScreen : PetNavigation("login_screen")
    object RegisterScreen : PetNavigation("register_screen")
    object HomeScreen : PetNavigation("home_screen")
    object CreateScreen : PetNavigation("create_screen")
    object DetailsScreen : PetNavigation("details_screen/{petJson}") {
        fun createRoute(petJson: String) = "details_screen/$petJson"
    }
}

@Composable
fun AppNavigation(innerPadding: PaddingValues) {
    val viewModel : MainViewModel = hiltViewModel();
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = PetNavigation.LoginScreen.route){
        composable(PetNavigation.SplashScreen.route){
            SplashScreen(navController, innerPadding)
        }
        composable(PetNavigation.RegisterScreen.route){
            RegisterScreen(navController, viewModel)
        }
        composable(PetNavigation.LoginScreen.route){
            LoginScreen(navController, viewModel)
        }
        composable(PetNavigation.HomeScreen.route){
            HomeScreen(navController, viewModel)
        }
        composable(PetNavigation.CreateScreen.route){
            CreateScreen(navController, viewModel)
        }
        composable(PetNavigation.DetailsScreen.route) { backStackEntry ->
            val petJson = backStackEntry.arguments?.getString("petJson")
            val gson = Gson()
            val petData: PetData? = if (petJson != null) {
                try {
                    val decodedJson = URLDecoder.decode(petJson, StandardCharsets.UTF_8.toString())
                    gson.fromJson(decodedJson, PetData::class.java)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
            DetailsScreen(navController, viewModel, petData)
        }
    }
}