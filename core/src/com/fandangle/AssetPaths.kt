package com.fandangle


object AssetPaths {
    const val PLAYER = "player.png"
    const val ENEMY = "enemy.png"
    const val BLOCK = "block.png"
    const val FLAG = "flag.png"
    const val END1_BACKGROUND = "end1_background.png"
    const val END2_BACKGROUND = "end2_background.png"
    val textures: List<String> = listOf(PLAYER, ENEMY, BLOCK, FLAG, END1_BACKGROUND, END2_BACKGROUND)

    const val NOTO = "noto.fnt"
    val fonts: List<String> = listOf(NOTO)

    val sounds = listOf<String>()

    const val LEVELS_FOLDER = "levels/"

    // SCRIPTS
    const val ENEMY_PATROL = "scripts/enemy_patrol.txt"
    const val ENEMY_PATROL_LONG = "scripts/enemy_patrol_long.txt"
    const val ENEMY_DANCE = "scripts/enemy_dance.txt"
    const val PLAYER_LEVEL2 = "scripts/player_2.txt"
    const val PLAYER_LEVEL3 = "scripts/player_3.txt"
    const val PLAYER_LEVEL4 = "scripts/player_4.txt"
    const val PLAYER_LEVEL5 = "scripts/player_5.txt"
    const val PLAYER_EXIT = "scripts/player_exit.txt"
    const val PLAYER_EXIT_FLIPPED = "scripts/player_exit_flipped.txt"
    const val PLAYER_END1 = "scripts/player_end1.txt"
    const val PLAYER_END2 = "scripts/player_end2.txt"
}