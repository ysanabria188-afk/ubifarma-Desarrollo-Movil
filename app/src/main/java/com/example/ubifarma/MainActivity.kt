package com.example.ubifarma

import UserViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ubifarma.ui.theme.UbifarmaTheme

class MainActivity : ComponentActivity() {
    // Obtenemos la instancia de la base de datos.
    val db by lazy { AppDatabase.getInstance(this) }
    val userDao by lazy { db.userDao() }

    // Creamos una fábrica simple para pasar el DAO al ViewModel.
    val viewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(userDao) as T
            }
        }
    }

    val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UbifarmaTheme {
        Greeting("Android")
    }
}

@Composable
fun UserScreen(viewModel: UserViewModel) {
    // 'collectAsState' convierte el StateFlow en un State que Compose puede observar.
    val userList by viewModel.allUsers.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Text("Lista de Usuarios", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Añadimos un usuario de ejemplo
            val sampleName = "Usuario ${userList.size + 1}"
            val sampleAge = (20..50).random()
            viewModel.addUser(sampleName, sampleAge)
        }) {
            Text("Añadir Usuario Aleatorio")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // LazyColumn es la versión de Compose para listas eficientes (como RecyclerView).
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(userList) { user ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(
                        text = "ID: ${user.id}, Nombre: ${user.name}, Edad: ${user.age}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}