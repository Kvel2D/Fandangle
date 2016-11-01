package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.fandangle.Main
import com.fandangle.entity.Mappers

/*
 * Makes camera follow the target entity
 */
class CameraSystem : EntitySystem {
    private var target: Entity? = null
    private enum class STATE { LERP1, LERP2, LERP3, NO_LERP }
    private var state = STATE.NO_LERP
    private var lerp = 1f
    private var actualX = 0f
    val camera: OrthographicCamera

    constructor() {
        val camWidth = Gdx.graphics.width.toFloat()
        val camHeight = Gdx.graphics.height.toFloat()
        camera = OrthographicCamera(camWidth, camHeight)
        camera.position.set(0f, camHeight / 2, 0f)
        camera.update()
    }

    constructor(priority: Int) : super(priority) {
        val camWidth = Gdx.graphics.width.toFloat()
        val camHeight = Gdx.graphics.height.toFloat()
        camera = OrthographicCamera(camWidth, camHeight)
        camera.position.set(0f, camHeight / 2, 0f)
        camera.update()
    }

    override fun update(deltaTime: Float) {
        if (target == null) {
            return
        }

        val targetTransformC = Mappers.transformComponent.get(target)

        // Increase lerp as camera gets closer to target
        // Stop lerping when camera has reached target
        when (state) {
            STATE.LERP1 -> {
                if (Math.abs(actualX - targetTransformC.x) <= 200f) {
                    lerp = 0.2f
                    state = STATE.LERP2
                }
            }
            STATE.LERP2 -> {
                if (Math.abs(actualX - targetTransformC.x) <= 40f) {
                    lerp = 0.3f
                    state = STATE.LERP3
                }
            }
            STATE.LERP3 -> {
                if (Math.abs(actualX - targetTransformC.x) <= 1f) {
                    state = STATE.NO_LERP
                }
            }
            STATE.NO_LERP -> {}
        }

        // Move camera's position to target
        if (state == STATE.NO_LERP) {
            actualX += (targetTransformC.x - camera.position.x)
        } else {
            actualX += (targetTransformC.x - camera.position.x) * lerp
        }

        // Follow horizontally during normal levels and vertically during endings
        if (Main.currentLevel != 8) {
            camera.position.x = Math.round(actualX).toFloat()
        } else {
            camera.position.y = Math.round(targetTransformC.y).toFloat()
        }
        camera.update()
    }

    fun setTarget(target: Entity?) {
        this.target = target
        state = STATE.LERP1
        lerp = 0.1f
    }

    fun setTargetInstant(target: Entity?) {
        this.target = target
        state = STATE.NO_LERP
    }
}
