package com.victorirala.tpindividual_victorirala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.victorirala.tpindividual_victorirala.ui.theme.TpIndividual_VictorIralaTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var recetas by remember { mutableStateOf(listOf<Receta>()) }
            var isDarkTheme by remember { mutableStateOf(false) }
            var buscarReceta by remember { mutableStateOf("") }

            TpIndividual_VictorIralaTheme(darkTheme = isDarkTheme) {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(text = "Modo Oscuro", modifier = Modifier.padding(end = 8.dp))
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { isDarkTheme = it }
                            )
                        }

                        // Titulo de la APP
                        Text(
                            text = "Carga tu Receta Favorita",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .wrapContentHeight()
                                .align(Alignment.CenterHorizontally)
                        )

                        // Campo de Busqueda de Recetas
                        TextField(
                            value = buscarReceta,
                            onValueChange = { buscarReceta = it },
                            label = { Text("Buscar Recetas") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF4699CD), RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent
                            )
                        )

                        val filtrarRecetas = recetas.filter {
                            it.nombrePlato.contains(buscarReceta, ignoreCase = true)
                        }

                        FormularioReceta { receta ->
                            recetas = recetas + receta
                        }

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filtrarRecetas) { receta ->
                                ListaRecetas(receta) { eliminarReceta ->
                                    recetas = recetas.filter { it != eliminarReceta }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Receta(
    val nombrePlato: String,
    val tiempoPreparacion: String,
    val ingredientes: String,
    val calorias: String,
    val imagen: String
)

@Composable
fun FormularioReceta(guardarReceta: (Receta) -> Unit) {
    val nombreplato = remember { mutableStateOf("") }
    val tiempopreparacion = remember { mutableStateOf("") }
    val ingredientes = remember { mutableStateOf("") }
    val calorias = remember { mutableStateOf("") }
    val imagen = remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Le agregue esto porque en la prueba al hacer enter no pasaba al siguiente campo
    // Sino modificaba el tamaño del campo de texto

    val focusRequestertiempopreparacion = remember { FocusRequester() }
    val focusRequesteringredientes = remember { FocusRequester() }
    val focusRequestercalorias = remember { FocusRequester() }
    val focusRequesterimagen = remember { FocusRequester() }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        // Cargar Campos de Texto
        // Se agrego control para realizar el enter desde el dispositivo o teclado de la PC,
        // Sino solo generaba el enter dentro del campo de texto y no pasa al siguiente registro.
        TextField(
            value = nombreplato.value,
            onValueChange = { nombreplato.value = it },
            label = { Text("Nombre del Plato") },
            modifier = Modifier
                .fillMaxWidth()
                .onKeyEvent { keyEvent: KeyEvent ->
                    if (keyEvent.key.nativeKeyCode == 66 || keyEvent.key == Key.Enter) {
                        focusRequestertiempopreparacion.requestFocus()
                        keyboardController?.hide()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequestertiempopreparacion.requestFocus()

                }
            )


        )

        TextField(
            value = tiempopreparacion.value,
            onValueChange = { tiempopreparacion.value = it },
            label = { Text("Tiempo de Preparación") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequestertiempopreparacion)
                .onKeyEvent { keyEvent: KeyEvent ->
                    if (keyEvent.key.nativeKeyCode == 66 || keyEvent.key == Key.Enter) {
                        focusRequesteringredientes.requestFocus()
                        keyboardController?.hide()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequesteringredientes.requestFocus()

                }
            )

        )

        TextField(
            value = ingredientes.value,
            onValueChange = { ingredientes.value = it },
            label = { Text("Ingredientes Principales") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesteringredientes)
                .onKeyEvent { keyEvent: KeyEvent ->
                    if (keyEvent.key.nativeKeyCode == 66 || keyEvent.key == Key.Enter) {
                        focusRequestercalorias.requestFocus()

                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequestercalorias.requestFocus()

                }
            )

        )

        TextField(
            value = calorias.value,
            onValueChange = { calorias.value = it },
            label = { Text("Calorías por Porcion") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequestercalorias)
                .onKeyEvent { keyEvent: KeyEvent ->
                    if (keyEvent.key.nativeKeyCode == 66 || keyEvent.key == Key.Enter) {
                        focusRequesterimagen.requestFocus()
                        keyboardController?.hide()
                        true
                    } else false
                },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequesterimagen.requestFocus()

                }
            )

        )

        TextField(
            value = imagen.value,
            onValueChange = { imagen.value = it },
            label = { Text("Imagen del Plato") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequesterimagen)
        )

        Button(
            onClick = {

                val recetaNueva =
                    Receta(
                        nombreplato.value,
                        tiempopreparacion.value,
                        ingredientes.value,
                        calorias.value,
                        imagen.value
                    )
                guardarReceta(recetaNueva)

                nombreplato.value = ""
                tiempopreparacion.value = ""
                ingredientes.value = ""
                calorias.value = ""
                imagen.value = ""
            }
        ) {
            Text("Guardar Nueva Receta")
        }


    }

}

@Composable
fun ListaRecetas(receta: Receta, eliminarReceta: (Receta) -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .border(2.dp, Color.Gray)
            .padding(16.dp)
    )
    {
        Text(text = receta.nombrePlato, style = MaterialTheme.typography.titleMedium)

        val tiempoPreparacionTexto = buildAnnotatedString {
            append("Tiempo de Preparación: ")
            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                append(receta.tiempoPreparacion)
            }
        }

        Text(text = tiempoPreparacionTexto)
        Text(text = "Ingredientes : ${receta.ingredientes}")
        Text(text = "Calorías : ${receta.calorias}")
        //Text(text = "Imagen : ${receta.imagen}")

        if (receta.imagen.isNotEmpty()) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = receta.imagen)
                    .apply(block = fun ImageRequest.Builder.() {
                        placeholder(R.drawable.placeholder)
                        error(R.drawable.placeholder)
                    }).build()
            )
            Image(
                painter = painter,
                contentDescription = "Imagen del Plato",
                modifier = Modifier

                    .height(100.dp)
                    .padding(top = 16.dp)
                    .background(Color.Black)
            )

            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {

                }

                is AsyncImagePainter.State.Error -> {
                    Text(text = "Error al Cargar la Imagen", color = Color.Red)
                }

                else -> {

                }
            }

        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { eliminarReceta(receta) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Eliminar Receta")
            }
        }


    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TpIndividual_VictorIralaTheme {
        FormularioReceta(guardarReceta = {})
    }
}
