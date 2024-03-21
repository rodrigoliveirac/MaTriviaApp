package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.game.domain.Question

interface GetQuestion {
    suspend operator fun invoke(): List<Question>
}