package com.fandangle.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion

class PlayerAnimationComponent : Component {
    val frames: Array<Array<TextureRegion>>
    val frameDuration = 7
    var frameTimer = 0
    var direction = 1
    var currentFrame = 0

    constructor(frames: Array<Array<TextureRegion>>) {
        this.frames = frames
    }
}
