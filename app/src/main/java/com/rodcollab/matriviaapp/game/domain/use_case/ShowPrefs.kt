package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.game.domain.UserGamePrefs

interface ShowPrefs {
    suspend operator fun invoke(): UserGamePrefs
}