package com.fandangle.entity.components

import com.badlogic.ashley.core.Component

class MovementComponent : Component {
    var left = false
    var right = false
    var up = false
    var down = false
    var jump = false

    var horizontalVelocityMax = 10f
    var horizontalVelocityMin = 1f
    var lerpX = 0.1f
    var lerpXJumping = 0.05f

    var verticalVelocityMax = 0f
    var verticalVelocityMin = 0f
    var lerpY = 0.1f

    var jumpVelocity = 20f
    var gravityFall = -60f
    var gravityJump = -40f
    var jumpLength = 10 // frames
    var jumpFramesLeft = 0
    var jumpCooldownLength = 5
    var jumpCooldown = 0
    var bounceLength = 15 // frames
    var bounceFramesLeft = 0
}

