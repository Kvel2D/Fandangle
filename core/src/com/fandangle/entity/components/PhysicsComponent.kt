package com.fandangle.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class PhysicsComponent: Component {
    val halfWidth: Float
    val halfHeight: Float
    var velocity = Vector2(0f, 0f)
    var dx = 0f
    var dy = 0f
    var grounded = false

    constructor(width: Float, height: Float) {
        this.halfWidth = width / 2
        this.halfHeight = height / 2
    }
}
