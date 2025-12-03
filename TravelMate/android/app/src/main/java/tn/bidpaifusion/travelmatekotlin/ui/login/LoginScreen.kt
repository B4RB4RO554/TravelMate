package tn.bidpaifusion.travelmatekotlin.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import tn.bidpaifusion.travelmatekotlin.viewmodel.LoginState
import tn.bidpaifusion.travelmatekotlin.viewmodel.LoginViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable

@Preview(showBackground = true, showSystemUi = true)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username or Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (state) {
            is LoginState.Loading -> CircularProgressIndicator()
            is LoginState.Error -> Text((state as LoginState.Error).message, color = MaterialTheme.colorScheme.error)
            is LoginState.Success -> {
                val successState = state as LoginState.Success
                val userId = successState.userId
                val token = successState.token

                Text("Login successful!", color = MaterialTheme.colorScheme.primary)

                LaunchedEffect(Unit) {
                    navController.navigate("home/$userId/$token") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }


            else -> {}
        }
    }
}
