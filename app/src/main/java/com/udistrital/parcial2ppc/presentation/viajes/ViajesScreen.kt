package com.udistrital.parcial2ppc.presentation.viajes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial2ppc.presentation.components.PrimaryButton
import com.udistrital.parcial2ppc.presentation.theme.CardBorder
import com.udistrital.parcial2ppc.presentation.theme.GreenPrimary
import com.udistrital.parcial2ppc.presentation.theme.GreenSecondary
import com.udistrital.parcial2ppc.presentation.theme.TextDark
import com.udistrital.parcial2ppc.presentation.theme.TextMuted

private data class Place(val id: Long, val name: String, val description: String, val city: String, val category: String, val date: String)
private data class Trip(val id: Long, val name: String, val destination: String, val start: String, val end: String, val places: List<Place> = emptyList())

@Composable
fun ViajesScreen() {
    val trips = remember {
        mutableStateListOf(
            Trip(1, "Europa 2026", "Italia", "10 jun 2026", "20 jun 2026", listOf(
                Place(11, "Coliseo Romano", "Anfiteatro romano, uno de los principales atractivos de Roma.", "Roma", "Histórico", "12 jun 2026"),
                Place(12, "Fontana di Trevi", "Fuente monumental en el corazón de Roma.", "Roma", "Cultural", "13 jun 2026"),
                Place(13, "Vaticano", "Museos y basílica de San Pedro.", "Ciudad del Vaticano", "Cultural", "15 jun 2026")
            )),
            Trip(2, "Santa Marta", "Colombia", "5 jul 2026", "12 jul 2026"),
            Trip(3, "Bogotá Cultural", "Colombia", "18 ago 2026", "22 ago 2026")
        )
    }
    var selectedTripId by remember { mutableStateOf<Long?>(null) }
    var showingForm by remember { mutableStateOf(false) }
    var editingPlace by remember { mutableStateOf(false) }
    var deletingTrip by remember { mutableStateOf<Long?>(null) }
    var deletingPlace by remember { mutableStateOf<Long?>(null) }
    var tripMenu by remember { mutableStateOf<Long?>(null) }
    var placeMenu by remember { mutableStateOf<Long?>(null) }
    val selectedTrip = trips.firstOrNull { it.id == selectedTripId }

    when {
        showingForm && editingPlace -> PlaceFormScreen(
            onBack = { showingForm = false; editingPlace = false },
            onSave = { name, description, city, category, date ->
                selectedTrip?.let { trip ->
                    val updated = trip.copy(places = trip.places + Place(System.currentTimeMillis(), name, description, city, category, date))
                    trips[trips.indexOfFirst { it.id == trip.id }] = updated
                }
                showingForm = false
                editingPlace = false
            }
        )
        showingForm -> TripFormScreen(
            onBack = { showingForm = false },
            onSave = { name, destination, start, end ->
                trips.add(Trip(System.currentTimeMillis(), name, destination, start, end))
                showingForm = false
            }
        )
        selectedTrip != null -> TripDetailScreen(
            trip = selectedTrip,
            placeMenu = placeMenu,
            onPlaceMenu = { placeMenu = it },
            onBack = { selectedTripId = null },
            onAddPlace = { editingPlace = true; showingForm = true },
            onDeletePlace = { deletingPlace = it }
        )
        else -> TripListScreen(
            trips = trips,
            tripMenu = tripMenu,
            onTripMenu = { tripMenu = it },
            onOpenTrip = { selectedTripId = it },
            onCreateTrip = { showingForm = true },
            onDeleteTrip = { deletingTrip = it }
        )
    }

    deletingTrip?.let { id ->
        ConfirmDeleteDialog("¿Eliminar este viaje?", onConfirm = {
            trips.removeAll { it.id == id }
            deletingTrip = null
        }, onDismiss = { deletingTrip = null })
    }
    deletingPlace?.let { id ->
        ConfirmDeleteDialog("¿Eliminar este lugar?", onConfirm = {
            selectedTrip?.let { trip ->
                trips[trips.indexOfFirst { it.id == trip.id }] = trip.copy(places = trip.places.filterNot { it.id == id })
            }
            deletingPlace = null
        }, onDismiss = { deletingPlace = null })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripListScreen(trips: List<Trip>, tripMenu: Long?, onTripMenu: (Long?) -> Unit, onOpenTrip: (Long) -> Unit, onCreateTrip: () -> Unit, onDeleteTrip: (Long) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis viajes", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White)) },
        floatingActionButton = { FloatingActionButton(onClick = onCreateTrip, containerColor = GreenPrimary, contentColor = Color.White) { Icon(Icons.Default.Add, "Crear viaje") } }
    ) { padding ->
        if (trips.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(Icons.Default.Luggage, null, tint = GreenPrimary, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(12.dp)); Text("Aún no tienes viajes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Crea un viaje y agrega los lugares que quieres conocer.", color = TextMuted)
            }
        } else LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(trips, key = { it.id }) { trip ->
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, CardBorder), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth().clickable { onOpenTrip(trip.id) }) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        LeadingIcon(Icons.Default.Luggage)
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(trip.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(trip.destination, color = TextMuted, fontSize = 14.sp)
                        }
                        Box {
                            IconButton(onClick = { onTripMenu(trip.id) }) { Text("⋮", fontSize = 24.sp, color = TextDark) }
                            DropdownMenu(expanded = tripMenu == trip.id, onDismissRequest = { onTripMenu(null) }) {
                                DropdownMenuItem(text = { Text("Ver lugares") }, onClick = { onTripMenu(null); onOpenTrip(trip.id) })
                                DropdownMenuItem(text = { Text("Eliminar viaje") }, onClick = { onTripMenu(null); onDeleteTrip(trip.id) })
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
private fun TripDetailScreen(trip: Trip, placeMenu: Long?, onPlaceMenu: (Long?) -> Unit, onBack: () -> Unit, onAddPlace: () -> Unit, onDeletePlace: (Long) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(trip.name, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White)) },
        floatingActionButton = { FloatingActionButton(onClick = onAddPlace, containerColor = GreenPrimary, contentColor = Color.White) { Icon(Icons.Default.Add, "Agregar lugar") } }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, CardBorder)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        LeadingIcon(Icons.Default.Luggage)
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(trip.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                            Text("📍  ${trip.destination}", color = TextDark, fontSize = 14.sp)
                            Text("▦  ${trip.start} – ${trip.end}", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }
            }
            item { Text("Lugares a visitar (${trip.places.size})", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)) }
            if (trip.places.isEmpty()) item { Text("Agrega el primer lugar de este viaje.", color = TextMuted, modifier = Modifier.padding(vertical = 12.dp)) }
            items(trip.places, key = { it.id }) { place ->
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, CardBorder), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        LeadingIcon(Icons.Default.LocationOn)
                        Column(Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(place.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CalendarMonth, null, tint = GreenPrimary, modifier = Modifier.size(15.dp)); Text(" ${place.date} · ${place.city}", color = TextMuted, fontSize = 12.sp) }
                        }
                        Box {
                            IconButton(onClick = { onPlaceMenu(place.id) }) { Text("⋮", fontSize = 22.sp, color = TextDark) }
                            DropdownMenu(expanded = placeMenu == place.id, onDismissRequest = { onPlaceMenu(null) }) {
                                DropdownMenuItem(text = { Text("Eliminar lugar") }, onClick = { onPlaceMenu(null); onDeletePlace(place.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripFormScreen(onBack: () -> Unit, onSave: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }; var destination by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }; var end by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    FormScaffold("Crear viaje", onBack) {
        FieldLabel("Nombre del viaje *"); AppField(name, { name = it }, "Ej: Europa 2026")
        FieldLabel("Destino *"); AppField(destination, { destination = it }, "Ej: Italia")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) { FieldLabel("Fecha de inicio *"); AppField(start, { start = it }, "10 jun 2026", leading = Icons.Default.CalendarMonth) }
            Column(Modifier.weight(1f)) { FieldLabel("Fecha de fin *"); AppField(end, { end = it }, "20 jun 2026", leading = Icons.Default.CalendarMonth) }
        }
        Text("Puedes escribir las fechas como DD MMM AAAA", fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(top = 5.dp))
        FormError(error)
        Spacer(Modifier.weight(1f)); PrimaryButton("Guardar", { if (name.isBlank() || destination.isBlank() || start.isBlank() || end.isBlank()) error = "Completa todos los campos obligatorios" else onSave(name.trim(), destination.trim(), start.trim(), end.trim()) }, icon = Icons.Default.Save)
    }
}

@Composable
private fun PlaceFormScreen(onBack: () -> Unit, onSave: (String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }; var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }; var category by remember { mutableStateOf("Histórico") }; var date by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }; var error by remember { mutableStateOf<String?>(null) }
    FormScaffold("Agregar lugar", onBack) {
        FieldLabel("Nombre del lugar *"); AppField(name, { name = it }, "Ej: Coliseo Romano")
        FieldLabel("Descripción *"); AppField(description, { description = it }, "Describe brevemente el lugar", singleLine = false)
        FieldLabel("Ciudad *"); AppField(city, { city = it }, "Ej: Roma")
        FieldLabel("Categoría *")
        Box {
            OutlinedTextField(value = category, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().clickable { categoryExpanded = true }, leadingIcon = { Icon(Icons.Default.Tag, null, tint = GreenPrimary) }, trailingIcon = { Text("▾", color = TextDark) }, shape = RoundedCornerShape(12.dp), colors = fieldColors())
            Box(Modifier.matchParentSize().clickable { categoryExpanded = true })
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) { listOf("Histórico", "Cultural", "Naturaleza", "Gastronomía", "Aventura", "Otro").forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { category = option; categoryExpanded = false }) } }
        }
        FieldLabel("Fecha tentativa *"); AppField(date, { date = it }, "12 jun 2026", leading = Icons.Default.CalendarMonth)
        FormError(error)
        Spacer(Modifier.weight(1f)); PrimaryButton("Guardar", { if (name.isBlank() || description.isBlank() || city.isBlank() || date.isBlank()) error = "Completa todos los campos obligatorios" else onSave(name.trim(), description.trim(), city.trim(), category, date.trim()) }, icon = Icons.Default.Save)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormScaffold(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White)) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 22.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(7.dp), content = content)
    }
}

@Composable
private fun FieldLabel(text: String) { Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark, modifier = Modifier.padding(top = 7.dp, bottom = 1.dp)) }

@Composable
private fun AppField(value: String, onValueChange: (String) -> Unit, placeholder: String, leading: androidx.compose.ui.graphics.vector.ImageVector? = null, singleLine: Boolean = true) {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text(placeholder, color = TextMuted, fontSize = 14.sp) }, leadingIcon = if (leading == null) null else ({ Icon(leading, null, tint = GreenPrimary) }), shape = RoundedCornerShape(12.dp), singleLine = singleLine, minLines = if (singleLine) 1 else 2, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), colors = fieldColors())
}

@Composable
private fun fieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, unfocusedBorderColor = CardBorder, focusedLabelColor = GreenPrimary, cursorColor = GreenPrimary)

@Composable
private fun FormError(error: String?) { if (error != null) Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }

@Composable
private fun LeadingIcon(image: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(Modifier.size(48.dp).background(GreenSecondary, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(image, null, tint = GreenPrimary, modifier = Modifier.size(25.dp)) }
}

@Composable
private fun ConfirmDeleteDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(message) }, confirmButton = { TextButton(onClick = onConfirm) { Text("Eliminar", color = MaterialTheme.colorScheme.error) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = GreenPrimary) } })
}
