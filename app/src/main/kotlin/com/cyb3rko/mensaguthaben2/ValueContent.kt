package com.cyb3rko.mensaguthaben2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.cyb3rko.mensaguthaben2.cardreader.ValueData
import java.util.Currency
import java.util.Locale

@Composable
internal fun ValueContent(
    innerPadding: PaddingValues = PaddingValues(),
    valueData: ValueData?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPadding)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val mainText = if (valueData != null) {
            moneyStr(valueData.value)
        } else {
            ContextCompat.getString(context, R.string.place_on_card)
        }
        val subtext = if (valueData != null) {
            context.getString(
                R.string.last_withdrawal, moneyStr(valueData.lastTransaction)
            )
        } else {
            ""
        }
        Text(
            mainText,
            style = TextStyle(
                fontSize = dimensionResource(id = R.dimen.text_size_main).value.sp
            ),
            textAlign = TextAlign.Center
        )
        if (subtext == "") return
        Text(
            modifier = Modifier.padding(top = 32.dp),
            text = subtext,
            style = TextStyle(
                fontSize = dimensionResource(id = R.dimen.text_size_secondary).value.sp
            ),
            textAlign = TextAlign.Center,
        )
    }
}

private fun moneyStr(i: Int?): String {
    if (i != null) {
        val germany = Locale.GERMANY
        val currencySymbol = Currency.getInstance(germany).symbol
        val amount = i.toFloat() / 1000
        return String.format(germany, "%.2f%s", amount, currencySymbol)
    } else {
        return "-"
    }
}
