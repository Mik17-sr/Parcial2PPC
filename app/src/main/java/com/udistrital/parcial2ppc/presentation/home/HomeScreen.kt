package com.udistrital.parcial2ppc.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.udistrital.parcial2ppc.presentation.components.MessageBanner
import com.udistrital.parcial2ppc.presentation.components.IconButtonWithBadge
import com.udistrital.parcial2ppc.presentation.components.MetricCard
import com.udistrital.parcial2ppc.presentation.components.PrimaryButton
import com.udistrital.parcial2ppc.presentation.theme.CardBorder
import com.udistrital.parcial2ppc.presentation.theme.GreenPrimary
import com.udistrital.parcial2ppc.presentation.theme.TextDark

import com.example.plansppc.utils.CurrencyUtils

@Composable
fun HomeScreen(
    //planes: List<PlanSummary>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onNavigateToCreatePlan: () -> Unit,
    onSelectPlan: (Long) -> Unit,
    onUpdateServerUrl: (String) -> Unit = {}
) {
    var showServerDialog by remember { mutableStateOf(false) }
    // var serverUrlInput by remember { mutableStateOf(RetrofitClient.baseUrl) }

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
                IconButtonWithBadge(
                    icon = Icons.Default.Notifications,
                    badgeCount = 3,
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
            //planes = planes,
            //isLoading = isLoading,
            onNavigateToCreatePlan = onNavigateToCreatePlan,
            onSelectPlan = onSelectPlan
        )
    }
}

@Composable
fun HomeScreenPlansView(
//    planes: List<PlanSummary>,
//    /**/isLoading: Boolean = false,
    onNavigateToCreatePlan: () -> Unit,
    onSelectPlan: (Long) -> Unit
) {
    //val totalAhorradoGeneral = planes.sumOf { it.totalAhorrado }
    //val totalMetaGeneral = planes.sumOf { it.meta }

    Column(modifier = Modifier.fillMaxSize()) {

        MetricCard(
                title = "Ahorro Total Familiar",
                value = "$",
                subtitle = "Meta global:",
                icon = Icons.Default.AccountBalanceWallet
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        PrimaryButton(
            text = "Crear primer plan",
            onClick = onNavigateToCreatePlan,
            icon = Icons.Default.Add
        )

}


