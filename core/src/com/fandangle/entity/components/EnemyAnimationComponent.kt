package com.fandangle.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion

class EnemyAnimationComponent: Component {
    val frames: Array<Array<TextureRegion>>
    val frameDuration = 10
    var currentFrame = 0
    var frameTimer = 0

    constructor(frames: Array<Array<TextureRegion>>) {
        this.frames = frames
    }
}

