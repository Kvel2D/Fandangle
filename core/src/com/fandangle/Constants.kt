package com.fandangle

object Constants {
    const val VIEWPORT_WIDTH = 1280f
    const val VIEWPORT_HEIGHT = 720f
    const val MAP_TO_PIXEL = 3f
    const val TIME_STEP = 1 / 60f
    const val DEATH_DURATION = 90
    const val GAMEOVER_DURATION = 30

    val operationCodes = mapOf(
            "goto" to 0,
            "left" to 1,
            "right" to 2,
            "jump" to 3,
            "jumpleft" to 4,
            "jumpright" to 5,
            "sleep" to 6,
            "land" to 7,
            "if" to 8,
            "up" to 9,
            "down" to 10
    )

    val variableCodes = mapOf(
            "xposition" to 0,
            "yposition" to 1,
            "grounded" to 2
    )

    val conditionCodes = mapOf(
            "<" to 0,
            "<=" to 1,
            ">" to 2,
            ">=" to 3,
            "==" to 4
    )
}
