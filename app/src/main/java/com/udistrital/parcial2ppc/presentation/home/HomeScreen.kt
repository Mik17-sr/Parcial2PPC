package com.udistrital.parcial2ppc.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial2ppc.data.model.PlanSummary
import com.udistrital.parcial2ppc.data.remote.RetrofitClient
import com.udistrital.parcial2ppc.presentation.components.EmptyStateCard
import com.udistrital.parcial2ppc.presentation.components.IconButtonWithBadge
import com.udistrital.parcial2ppc.presentation.components.MessageBanner
import com.udistrital.parcial2ppc.presentation.components.MetricCard
import com.udistrital.parcial2ppc.presentation.components.PrimaryButton
import com.udistrital.parcial2ppc.presentation.theme.CardBorder
import com.udistrital.parcial2ppc.presentation.theme.GreenPrimary
import com.udistrital.parcial2ppc.presentation.theme.GreenSecondary
import com.udistrital.parcial2ppc.presentation.theme.PlansPPCTheme
import com.udistrital.parcial2ppc.presentation.theme.TextDark
import com.udistrital.parcial2ppc.presentation.theme.TextMuted
import com.udistrital.parcial2ppc.utils.CurrencyUtils

@Composable
fun HomeScreen(
    planes: List<PlanSummary> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNavigateToCreatePlan: () -> Unit,
    onSelectPlan: (Long) -> Unit,
    onUpdateServerUrl: (String) -> Unit = {}
) {
    var showServerDialog by remember { mutableStateOf(false) }
    var serverUrlInput by remember { mutableStateOf(RetrofitClient.baseUrl) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = GreenPrimary,
                trackColor = Color.Transparent
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Spacer(modifier = Modifier.height(11.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis viajes",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                lineHeight = 34.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showServerDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "Configurar Servidor",
                        tint = TextDark
                    )
                }
                IconButtonWithBadge(
                    icon = Icons.Default.Notifications,
                    badgeCount = planes.size,
                    onClick = {}
                )
            }
        }

        if (!errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            MessageBanner(message = errorMessage, isError = true)
        }

        Spacer(modifier = Modifier.height(20.dp))

        HomeScreenPlansView(
            planes = planes,
            onNavigateToCreatePlan = onNavigateToCreatePlan,
            onSelectPlan = onSelectPlan
        )
    }

    if (showServerDialog) {
        AlertDialog(
            onDismissRequest = { showServerDialog = false },
            title = { Text("URL del Servidor API") },
            text = {
                OutlinedTextField(
                    value = serverUrlInput,
                    onValueChange = { serverUrlInput = it },
                    label = { Text("Base URL") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onUpdateServerUrl(serverUrlInput)
                    showServerDialog = false
                }) {
                    Text("Guardar", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun HomeScreenPlansView(
    planes: List<PlanSummary>,
    onNavigateToCreatePlan: () -> Unit,
    onSelectPlan: (Long) -> Unit
) {
    val totalAhorradoGeneral = planes.sumOf { it.totalAhorrado }
    val totalMetaGeneral = planes.sumOf { it.meta }

    Column(modifier = Modifier.fillMaxSize()) {

        MetricCard(
            title = "Ahorro Total Familiar",
            value = "$ ${CurrencyUtils.formatAmount(totalAhorradoGeneral)}",
            subtitle = "Meta global: $ ${CurrencyUtils.formatAmount(totalMetaGeneral)}",
            icon = Icons.Default.AccountBalanceWallet
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            text = "Crear nuevo plan de viaje",
            onClick = onNavigateToCreatePlan,
            icon = Icons.Default.Add
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (planes.isEmpty()) {
            EmptyStateCard(
                title = "No tienes viajes planeados",
                message = "Crea tu primer plan de viaje para comenzar a ahorrar en familia.",
                icon = Icons.Default.Savings
            )
        } else {
            Text(
                text = "Tus planes de viaje (${planes.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(planes) { plan ->
                    PlanItemCard(
                        plan = plan,
                        onClick = { onSelectPlan(plan.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlanItemCard(
    plan: PlanSummary,
    onClick: () -> Unit
) {
    val progress = if (plan.meta > 0) (plan.totalAhorrado / plan.meta).toFloat().coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.motivo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "$ ${CurrencyUtils.formatAmount(plan.totalAhorrado)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Meta: $ ${CurrencyUtils.formatAmount(plan.meta)}",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GreenPrimary,
                trackColor = GreenSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PlansPPCTheme {
        HomeScreen(
            planes = listOf(
                PlanSummary(
                    id = 1,
                    motivo = "Viaje a Cancún",
                    meta = 5000000.0,
                    totalAhorrado = 1500000.0,
                    totalMiembros = 3
                )
            ),
            onNavigateToCreatePlan = {},
            onSelectPlan = {}
        )
    }
}
