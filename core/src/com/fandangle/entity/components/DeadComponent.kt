package com.fandangle.entity.components

import com.badlogic.ashley.core.Component

class DeadComponent: Component {
    val timerDuration: Int
    var timer = 0

    constructor(timerDuration: Int) {
        this.timerDuration = timerDuration
    }
}
