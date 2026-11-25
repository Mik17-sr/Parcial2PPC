package com.udistrital.parcial2ppc.presentation.detalle_plan

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial2ppc.data.model.PlanDetail
import com.udistrital.parcial2ppc.presentation.components.AppTopBar
import com.udistrital.parcial2ppc.presentation.components.PrimaryButton
import com.udistrital.parcial2ppc.presentation.theme.TextDark
import com.udistrital.parcial2ppc.presentation.theme.TextMuted
import com.udistrital.parcial2ppc.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePlanScreen(
    planDetail: PlanDetail?,
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onNavigateToRegisterPayment: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = planDetail?.motivo ?: "Detalle del Plan",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            if (planDetail != null) {
                Text(
                    text = planDetail.motivo,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Meta: ${CurrencyUtils.formatAmount(planDetail.meta)}",
                    fontSize = 16.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ahorrado: ${CurrencyUtils.formatAmount(planDetail.totalAhorrado)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(32.dp))
                PrimaryButton(
                    text = "Registrar Pago",
                    onClick = { onNavigateToRegisterPayment(planDetail.id) }
                )
            } else {
                Text(
                    text = if (isLoading) "Cargando detalle..." else "Plan no encontrado",
                    fontSize = 16.sp,
                    color = TextMuted
                )
            }
        }
    }
}
