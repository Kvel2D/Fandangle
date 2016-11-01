package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.fandangle.Main
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.KeyboardComponent
import com.fandangle.entity.components.MovementComponent

class KeyboardSystem : IteratingSystem {
    val inputProcessor = KeyboardInputProcessor()
    private var left = false
    private var right = false
    private var up = false
    private var down = false
    private var jump = false

    constructor() :
    super(Family.all(KeyboardComponent::class.java, MovementComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(KeyboardComponent::class.java, MovementComponent::class.java).get(), priority)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val movementC = Mappers.movementComponent.get(entity)

        movementC.left = left
        movementC.right = right
        movementC.up = up
        movementC.down = down
        movementC.jump = jump
    }

    inner class KeyboardInputProcessor() : InputAdapter() {
        override fun keyDown(keycode: Int): Boolean {
            when (keycode) {
                Controls.LEFT -> left = true
                Controls.RIGHT -> right = true
                Controls.UP -> up = true
                Controls.DOWN -> down = true
                Controls.JUMP -> jump = true
            }

            return false
        }

        override fun keyUp(keycode: Int): Boolean {
            when (keycode) {
                Controls.LEFT -> left = false
                Controls.RIGHT -> right = false
                Controls.UP -> up = false
                Controls.DOWN -> down = false
                Controls.JUMP -> jump = false
            }

            return false
        }
    }

    object Controls {
        const val LEFT = Input.Keys.A
        const val RIGHT = Input.Keys.D
        const val DOWN = Input.Keys.S
        const val UP = Input.Keys.W
        const val JUMP = Input.Keys.SPACE
    }
}

