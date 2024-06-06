package com.cyb3rko.mensaguthaben2

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.cyb3rko.mensaguthaben2.cardreader.ValueData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class MainViewModel(
    autoStart: Boolean,
    nfcActivated: Boolean
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        UiState(
            autoStart = autoStart,
            showNfcDialog = !nfcActivated
        )
    )
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateValueData(valueData: ValueData) {
        _uiState.update {
            it.copy(
                valueData = valueData
            )
        }
    }

    fun toggleAutoStart() {
        _uiState.update {
            it.copy(
                autoStart = !it.autoStart
            )
        }
    }

    fun showNfcDialog(show: Boolean) {
        _uiState.update {
            it.copy(
                showNfcDialog = show
            )
        }
    }

    companion object {
        @Suppress("DEPRECATION")
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                MainViewModel(
                    autoStart = android.preference.PreferenceManager
                        .getDefaultSharedPreferences(application)
                        .getBoolean("autostart", true),
                    nfcActivated = (this[DEFAULT_ARGS_KEY] as Bundle).getBoolean("nfcActivated")
                )
            }
        }
    }
}

internal data class UiState(
    val valueData: ValueData? = null,
    val autoStart: Boolean = false,
    val showNfcDialog: Boolean = false
)
