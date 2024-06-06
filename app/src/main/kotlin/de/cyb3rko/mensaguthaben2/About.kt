package de.cyb3rko.mensaguthaben2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.cyb3rko.mensaguthaben2.ui.components.LinkText

@Composable
internal fun About(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxSize()
    ) {
        TextField(
            text = stringResource(
                id = R.string.version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        )
        TextField(
            text = stringResource(
                id = R.string.copyright
            ),
            links = mapOf(
                stringResource(R.string.copyright) to stringResource(R.string.copyright_url)
            ),
            vSpace = 6
        )
        TextField(
            text = stringResource(
                id = R.string.copyright2
            ),
            links = mapOf(
                stringResource(R.string.copyright2) to stringResource(R.string.github_url)
            ),
            vSpace = 6
        )
        TextField(
            text = stringResource(
                id = R.string.website
            ),
            links = mapOf(
                "GitHub" to stringResource(R.string.github_url)
            ),
            vSpace = 25
        )
        TextField(
            text = stringResource(
                id = R.string.farebot
            ),
            links = mapOf(
                "farebot" to stringResource(R.string.farebot_url)
            ),
            vSpace = 25
        )
        TextField(
            text = stringResource(
                id = R.string.github
            ),
            links = mapOf(
                "GitHub" to stringResource(R.string.github_url)
            ),
            vSpace = 25
        )
    }
}

@Composable
private fun TextField(text: String, vSpace: Int = 0, links: Map<String, String> = mapOf()) {
    LinkText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = vSpace.dp),
        text = text,
        hyperLinks = links,
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        ),
        linkTextColor = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp
    )
}
