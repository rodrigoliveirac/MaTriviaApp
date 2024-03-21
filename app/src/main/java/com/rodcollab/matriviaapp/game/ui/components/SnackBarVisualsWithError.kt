package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

class SnackBarVisualsWithError(
    override val message: String,
    val isError: Boolean
) : SnackbarVisuals {
    override val actionLabel: String
        get() = if (isError) "Wrong" else "Right"
    override val withDismissAction: Boolean
        get() = false
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Indefinite
}