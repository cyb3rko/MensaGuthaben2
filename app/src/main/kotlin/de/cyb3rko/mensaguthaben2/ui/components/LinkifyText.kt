package de.cyb3rko.mensaguthaben2.ui.components

import android.util.Log
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.TextUnit

@Composable
internal fun LinkText(
    modifier: Modifier = Modifier,
    text: String,
    hyperLinks: Map<String, String>,
    textStyle: TextStyle = TextStyle.Default,
    linkTextColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val annotatedString = buildAnnotatedString {
        append(text)
        for((key, value) in hyperLinks) {
            val startIndex = text.indexOf(key)
            val endIndex = startIndex + key.length
            addStyle(
                style = SpanStyle(
                    color = linkTextColor,
                    fontSize = fontSize
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = value,
                start = startIndex,
                end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(fontSize = fontSize),
            start = 0,
            end = text.length
        )
    }
    val uriHandler = LocalUriHandler.current
    ClickableText(
        modifier = modifier,
        text = annotatedString,
        style = textStyle,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    try {
                        uriHandler.openUri(stringAnnotation.item)
                    } catch (e: Exception) {
                        Log.e("MensaGuthaben2", "Failed to open URI")
                    }
                }
        }
    )
}
