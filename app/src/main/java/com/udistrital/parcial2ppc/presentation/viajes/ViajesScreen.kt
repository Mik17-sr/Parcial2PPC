package com.udistrital.parcial2ppc.presentation.viajes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial2ppc.domain.model.Category
import com.udistrital.parcial2ppc.presentation.components.PrimaryButton
import com.udistrital.parcial2ppc.presentation.theme.CardBorder
import com.udistrital.parcial2ppc.presentation.theme.GreenPrimary
import com.udistrital.parcial2ppc.presentation.theme.TextDark
import com.udistrital.parcial2ppc.presentation.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ViajesScreen(viewModel: ViajesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var screen by remember { mutableStateOf<Screen>(Screen.List) }

    when (val currentScreen = screen) {
        Screen.List -> TripListScreen(
            state = state,
            onCreate = { screen = Screen.CreateTrip },
            onOpen = { tripId ->
                viewModel.openTrip(tripId)
                screen = Screen.Detail
            },
            onRefresh = viewModel::refresh
        )
        Screen.CreateTrip -> TripFormScreen(
            isLoading = state.isLoading,
            errorMessage = state.errorMessage,
            onBack = {
                viewModel.clearMessages()
                screen = Screen.List
            },
            onSave = { name, destination, start, end ->
                viewModel.createTrip(name, destination, start, end)
                screen = Screen.List
            }
        )
        Screen.Detail -> TripDetailScreen(
            state = state,
            onBack = {
                viewModel.closeTrip()
                screen = Screen.List
            },
            onAddPlace = { screen = Screen.CreatePlace }
        )
        Screen.CreatePlace -> PlaceFormScreen(
            isLoading = state.isLoading,
            categories = state.categories,
            tripStart = state.selectedTrip?.startDate.orEmpty(),
            tripEnd = state.selectedTrip?.endDate.orEmpty(),
            errorMessage = state.errorMessage,
            onBack = {
                viewModel.clearMessages()
                screen = Screen.Detail
            },
            onSave = { name, description, city, categoryId, date ->
                viewModel.createPlace(name, description, city, categoryId, date)
                screen = Screen.Detail
            }
        )
    }

}

private sealed interface Screen {
    data object List : Screen
    data object CreateTrip : Screen
    data object Detail : Screen
    data object CreatePlace : Screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripListScreen(
    state: ViajesUiState,
    onCreate: () -> Unit,
    onOpen: (String) -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis viajes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreate, containerColor = GreenPrimary) {
                Icon(Icons.Default.Add, contentDescription = "Crear viaje", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            }
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = Color(0xFFB00020),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                TextButton(onClick = onRefresh, modifier = Modifier.padding(horizontal = 12.dp)) {
                    Text("Reintentar")
                }
            }
            if (state.trips.isEmpty() && !state.isLoading) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Luggage, null, tint = GreenPrimary, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Aun no tienes viajes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Crea un viaje y agrega los lugares que quieres conocer.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.trips, key = { it.id }) { trip ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, CardBorder),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.fillMaxWidth().clickable { onOpen(trip.id) }
                        ) {
                            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Luggage, null, tint = GreenPrimary)
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(trip.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text(trip.destination, color = TextMuted, fontSize = 14.sp)
                                    Text("${trip.startDate} - ${trip.endDate}", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDetailScreen(state: ViajesUiState, onBack: () -> Unit, onAddPlace: () -> Unit) {
    val trip = state.selectedTrip
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trip?.name ?: "Detalle del viaje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White)
            )
        },
        floatingActionButton = {
            if (trip != null) {
                FloatingActionButton(onClick = onAddPlace, containerColor = GreenPrimary) {
                    Icon(Icons.Default.Add, "Agregar lugar", tint = Color.White)
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else if (trip == null) {
            Text(state.errorMessage ?: "Viaje no encontrado", modifier = Modifier.padding(padding).padding(20.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(trip.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                            Text("Destino: ${trip.destination}", color = TextDark)
                            Text("Periodo: ${trip.startDate} - ${trip.endDate}", color = TextMuted)
                        }
                    }
                }
                item {
                    Text("Lugares a visitar (${trip.places.size})", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
                if (trip.places.isEmpty()) {
                    item { Text("Agrega el primer lugar de este viaje.", color = TextMuted) }
                }
                items(trip.places, key = { it.id.ifBlank { it.name } }) { place ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = GreenPrimary)
                            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                                Text(place.name, fontWeight = FontWeight.Bold, color = TextDark)
                                Text(place.description, color = TextMuted, fontSize = 13.sp)
                                Text("${place.city} - ${place.tentativeDate}", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripFormScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    FormScaffold("Crear viaje", onBack) {
        FieldLabel("Nombre del viaje *")
        AppField(name, { name = it }, "Ej: Europa 2026")
        FieldLabel("Destino *")
        AppField(destination, { destination = it }, "Ej: Italia")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                FieldLabel("Fecha de inicio *")
                AppDateField(start, { start = it })
            }
            Column(Modifier.weight(1f)) {
                FieldLabel("Fecha de fin *")
                AppDateField(end, { end = it })
            }
        }
        FormError(errorMessage)
        Spacer(Modifier.weight(1f))
        PrimaryButton("Guardar", { onSave(name, destination, start, end) }, isLoading = isLoading)
    }
}

@Composable
private fun PlaceFormScreen(
    isLoading: Boolean,
    categories: List<Category>,
    tripStart: String,
    tripEnd: String,
    errorMessage: String?,
    onBack: () -> Unit,
    onSave: (String, String, String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf(0) }
    var categoryName by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var date by remember { mutableStateOf("") }

    FormScaffold("Agregar lugar", onBack) {
        FieldLabel("Nombre del lugar *")
        AppField(name, { name = it }, "Ej: Coliseo Romano")
        FieldLabel("Descripcion *")
        AppField(description, { description = it }, "Describe brevemente el lugar", singleLine = false)
        FieldLabel("Ciudad *")
        AppField(city, { city = it }, "Ej: Roma")
        FieldLabel("Categoria *")
        Box {
            OutlinedTextField(
                value = categoryName,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Seleccione una categoria") },
                modifier = Modifier.fillMaxWidth().clickable { categoryExpanded = true },
                leadingIcon = { Icon(Icons.Default.Tag, null, tint = GreenPrimary) },
                shape = RoundedCornerShape(12.dp)
            )
            Box(Modifier.matchParentSize().clickable { categoryExpanded = true })
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            categoryId = category.id
                            categoryName = category.name
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        FieldLabel("Fecha tentativa *")
        AppDateField(date, { date = it })
        Text("Debe estar entre $tripStart y $tripEnd", fontSize = 12.sp, color = TextMuted)
        FormError(errorMessage)
        Spacer(Modifier.weight(1f))
        PrimaryButton("Guardar", { onSave(name, description, city, categoryId, date) }, isLoading = isLoading)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            content = content
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
}

@Composable
private fun AppField(value: String, onValueChange: (String) -> Unit, placeholder: String, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextMuted) },
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppDateField(value: String, onValueChange: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Seleccione una fecha") },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, null, tint = GreenPrimary) },
            shape = RoundedCornerShape(12.dp)
        )
        Box(Modifier.matchParentSize().clickable { showPicker = true })
    }
    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        formatter.timeZone = TimeZone.getTimeZone("UTC")
                        onValueChange(formatter.format(Date(millis)))
                    }
                    showPicker = false
                }) { Text("Aceptar", color = GreenPrimary) }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FormError(errorMessage: String?) {
    if (!errorMessage.isNullOrBlank()) {
        Text(errorMessage, color = Color(0xFFB00020), fontSize = 14.sp)
    }
}
