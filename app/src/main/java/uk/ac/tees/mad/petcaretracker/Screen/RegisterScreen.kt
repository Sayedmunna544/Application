package uk.ac.tees.mad.petcaretracker.Screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.tees.mad.petcaretracker.MainViewModel
import uk.ac.tees.mad.petcaretracker.PetNavigation
import uk.ac.tees.mad.petcaretracker.R

@Composable
fun RegisterScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    val loggedIn = viewModel._isLoggedIn.value
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
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }
    val loading = viewModel.loading.value;
    LaunchedEffect(loggedIn) {
        if (loggedIn) {
            navController.navigate(PetNavigation.HomeScreen.route) {
                popUpTo(0)
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painterResource(id = R.drawable.background_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().blur(20.dp),
            contentScale = ContentScale.FillHeight
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "Sign up", modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(), fontSize = 32.sp
            )
            Text("Enter your details to register", fontSize = 16.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.weight(0.1f))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        modifier = Modifier.clickable {
                            isPasswordVisible = !isPasswordVisible
                        }.padding(end = 8.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.padding(top = 16.dp))
            Button(onClick = {
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.signUp(firstName, lastName, email, password, context)
            }, modifier = Modifier.fillMaxWidth(), enabled = !loading) {
                if (loading) {
                    Text("Loading...")
                } else {
                    Text("Sign up", fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.padding(top = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Already have an account?")
                Text(
                    "Sign in",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            navController.navigate(PetNavigation.LoginScreen.route)
                        },
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}



@Preview(showBackground = true, name = "Register Screen - Filled Example")
@Composable
fun RegisterScreenPreview() {
    var firstName by remember { mutableStateOf("John") }
    var lastName by remember { mutableStateOf("Doe") }
    var email by remember { mutableStateOf("john.doe@example.com") }
    var password by remember { mutableStateOf("secret123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val loading = false  // Change to true to preview loading state

    Box(modifier = Modifier.fillMaxSize()) {
        // Background blurred image
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp),
            contentScale = ContentScale.FillHeight
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Sign up",
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Enter your details to register",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(0.5f),
                        focusedLabelColor = Color.White,
                        cursorColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.weight(0.1f))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(0.5f),
                        focusedLabelColor = Color.White,
                        cursorColor = Color.White
                    )
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(0.5f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(24.dp),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        color = Color.White,
                        modifier = Modifier
                            .clickable { isPasswordVisible = !isPasswordVisible }
                            .padding(end = 12.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(0.5f),
                    focusedLabelColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.padding(top = 24.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = if (loading) "Loading..." else "Sign up",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.padding(top = 24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?", color = Color.White.copy(alpha = 0.9f))
                Text(
                    "Sign in",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable { }
                )
            }
        }
    }
}