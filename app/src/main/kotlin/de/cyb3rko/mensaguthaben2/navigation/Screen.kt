package de.cyb3rko.mensaguthaben2.navigation

import androidx.annotation.StringRes
import de.cyb3rko.mensaguthaben2.R

internal enum class Screen(@StringRes val title: Int) {
    Main(R.string.full_app_name),
    About(R.string.title_about)
}
