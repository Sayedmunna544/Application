package uk.ac.tees.mad.petcaretracker.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.tees.mad.petcaretracker.MainViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: MainViewModel) {
    var firstName by remember {
        mutableStateOf("")
    }
    var lastName by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,) {
        Text("Sign up", modifier = Modifier
            .fillMaxWidth()
            .systemBarsPadding()
        , fontSize = 32.sp)
        Text("Enter your details to register", fontSize = 16.sp)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            OutlinedTextField(value = firstName , onValueChange = {firstName = it}, label = {Text("First Name")}, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.weight(0.1f))
            OutlinedTextField(value = lastName , onValueChange = {lastName = it}, label = {Text("Last Name")}, modifier = Modifier.weight(1f))
        }
        OutlinedTextField(value = email , onValueChange = {email = it}, label = {Text("Email")}, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password , onValueChange = {password = it}, label = {Text("Password")}, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.padding(top = 16.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Register", fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.padding(top = 16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
            Text("Already have an account?")
            Text("Sign in")
        }
    }
}