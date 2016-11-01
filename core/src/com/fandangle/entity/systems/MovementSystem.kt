package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.fandangle.Constants
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.*

/*
 * Changes physics.velocity based on values in movementC state(set by keyboardControlS, scriptS)
 * Applies gravity(specified in movementC)
 */
class MovementSystem : IteratingSystem {

    constructor() :
    super(Family.all(TransformComponent::class.java, PhysicsComponent::class.java, MovementComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(TransformComponent::class.java, PhysicsComponent::class.java, MovementComponent::class.java).get(), priority)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val movementC = Mappers.movementComponent.get(entity)
        val physicsC = Mappers.physicsComponent.get(entity)

        val currentVelocity = physicsC.velocity
        val lerpX =
                if (physicsC.grounded) {
                    movementC.lerpX
                } else {
                    movementC.lerpXJumping
                }
        val lerpY = movementC.lerpY


        if (movementC.horizontalVelocityMax != 0f) {
            // either left or right is pressed, horizontal acceleration
            if (!(movementC.left && movementC.right)) {
                if (movementC.left) {
                    physicsC.velocity.x = -lerpX * movementC.horizontalVelocityMax + (1 - lerpX) * currentVelocity.x;
                } else if (movementC.right) {
                    physicsC.velocity.x = lerpX * movementC.horizontalVelocityMax + (1 - lerpX) * currentVelocity.x;
                }
            }
            // idle(no buttons or both buttons), body decelerates
            if (!movementC.left && !movementC.right || (movementC.left && movementC.right)) {
                physicsC.velocity.x = lerpX * 0f + (1 - lerpX) * currentVelocity.x;
                if (Math.abs(currentVelocity.x) < movementC.horizontalVelocityMin) {
                    currentVelocity.x = 0f
                }
            }
        }

        if (movementC.verticalVelocityMax != 0f) {
            // either up or down is pressed, vertical acceleration
            if (!(movementC.up && movementC.down)) {
                if (movementC.up) {
                    physicsC.velocity.y = lerpY * movementC.verticalVelocityMax + (1 - lerpY) * currentVelocity.y;
                } else if (movementC.down) {
                    physicsC.velocity.y = -lerpY * movementC.verticalVelocityMax + (1 - lerpY) * currentVelocity.y;
                }
            }
            // idle(no buttons or both buttons), body decelerates
            if (!movementC.up && !movementC.down || (movementC.up && movementC.down)) {
                physicsC.velocity.y = lerpY * 0f + (1 - lerpY) * currentVelocity.y;
                if (Math.abs(currentVelocity.y) < movementC.verticalVelocityMin) {
                    currentVelocity.y = 0f
                }
            }
        }


        // Jump start
        if (physicsC.grounded && movementC.jump && movementC.jumpCooldown == 0) {
            movementC.jumpFramesLeft = movementC.jumpLength
            movementC.jumpCooldown = movementC.jumpCooldownLength
            physicsC.velocity.y = movementC.jumpVelocity
            physicsC.grounded = false
        }

        // Decrement jump cooldown while on the ground
        if (physicsC.grounded && movementC.jumpCooldown > 0) {
            movementC.jumpCooldown--
        }

        if (!physicsC.grounded) {
            // Jump in progress
            if (movementC.jump && movementC.jumpFramesLeft > 0) {
                movementC.jumpFramesLeft--
            } else if (!movementC.jump && movementC.jumpFramesLeft > 0) {
                // Decrease the ascent speed if jump was ended early
                movementC.jumpFramesLeft = 0
                physicsC.velocity.y /= 2
            }

            // I'm bouncing, whoop, whoop
            if (movementC.bounceFramesLeft > 0) {
                movementC.bounceFramesLeft--
            }

            // When either jumping or bouncing a different gravity is applied
            if (movementC.jumpFramesLeft > 0 || movementC.bounceFramesLeft > 0) {
                physicsC.velocity.y = currentVelocity.y + movementC.gravityJump * Constants.TIME_STEP
            } else {
                physicsC.velocity.y = currentVelocity.y + movementC.gravityFall * Constants.TIME_STEP
            }
        }
    }
}
