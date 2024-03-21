package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.game.domain.GameCriteria
import com.rodcollab.matriviaapp.game.domain.UserGamePrefs

interface ShowPrefsAndCriteria {
    suspend operator fun invoke(): Pair<UserGamePrefs, GameCriteria>
}