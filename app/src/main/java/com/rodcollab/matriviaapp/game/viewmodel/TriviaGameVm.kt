package com.rodcollab.matriviaapp.game.viewmodel

import androidx.lifecycle.ViewModel
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TriviaGameVm @Inject constructor(
    private val preferences: Preferences,
    private val gameUseCases: GameUseCases
) : ViewModel() {


}