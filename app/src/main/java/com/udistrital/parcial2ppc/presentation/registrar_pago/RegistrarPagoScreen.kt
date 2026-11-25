package com.udistrital.parcial2ppc.presentation.registrar_pago

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.parcial2ppc.data.model.MiembroEntity
import com.udistrital.parcial2ppc.presentation.components.AppTopBar
import com.udistrital.parcial2ppc.presentation.components.PrimaryButton
import com.udistrital.parcial2ppc.presentation.theme.CardBorder
import com.udistrital.parcial2ppc.presentation.theme.GreenPrimary
import com.udistrital.parcial2ppc.presentation.theme.TextDark
import com.udistrital.parcial2ppc.presentation.theme.TextMuted
import com.udistrital.parcial2ppc.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarPagoScreen(
    miembros: List<MiembroEntity>,
    errorMessage: String?,
    isLoading: Boolean = false,
    onBack: () -> Unit,
    onRegisterPayment: (miembroId: Long?, monto: String) -> Unit
) {
    var monto by remember { mutableStateOf("") }
    val selectedMiembroId = miembros.firstOrNull()?.id

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registrar Pago",
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
            Text(
                text = "Monto a abonar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = monto,
                onValueChange = { newValue ->
                    monto = CurrencyUtils.formatMoneyInput(newValue, monto)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                prefix = { Text("$  ", color = TextDark, fontWeight = FontWeight.Bold) },
                placeholder = { Text("50.000", color = TextMuted) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = CardBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Confirmar Pago",
                isLoading = isLoading,
                onClick = { onRegisterPayment(selectedMiembroId, monto) }
            )
        }
    }
}
