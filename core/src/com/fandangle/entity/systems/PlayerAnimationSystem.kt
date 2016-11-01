package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.PhysicsComponent
import com.fandangle.entity.components.PlayerAnimationComponent
import com.fandangle.entity.components.TextureComponent

/*
 * Changes textureC.region based on state
 */
class PlayerAnimationSystem : IteratingSystem {

    constructor() :
    super(Family.all(PlayerAnimationComponent::class.java, TextureComponent::class.java, PhysicsComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(PlayerAnimationComponent::class.java, TextureComponent::class.java, PhysicsComponent::class.java).get(), priority)

    // Animation frames:
    // 1st row - running
    // 2nd row - jump, idle
    // 3rd, 4th - mirrors of 1st and 2nd
    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationC = Mappers.playerAnimationComponent.get(entity)
        val textureC = Mappers.textureComponent.get(entity)
        val physicsC = Mappers.physicsComponent.get(entity)

        if (physicsC.velocity.x > 0f && animationC.direction == -1) {
            animationC.direction = 1
        } else if (physicsC.velocity.x < 0f && animationC.direction == 1) {
            animationC.direction = -1
        }

        if (physicsC.grounded) {
            if (physicsC.velocity.x == 0f) {
                if (animationC.direction == 1) {
                    textureC.region = animationC.frames[1][1]
                } else {
                    textureC.region = animationC.frames[3][1]
                }

                animationC.frameTimer = 0
                animationC.currentFrame = 0
            } else {
                animationC.frameTimer -= 1
                if (animationC.frameTimer <= 0) {
                    animationC.frameTimer =
                            if (Math.abs(physicsC.velocity.x) > 5f) animationC.frameDuration
                            else animationC.frameDuration * 2
                    animationC.currentFrame++
                    // Loop back around
                    if (animationC.currentFrame == animationC.frames[0].size) {
                        animationC.currentFrame = 0
                    }
                    if (animationC.direction == 1) {
                        textureC.region = animationC.frames[0][animationC.currentFrame]
                    } else {
                        textureC.region = animationC.frames[2][animationC.currentFrame]
                    }
                }
            }
        } else {
            if (animationC.direction == 1) {
                textureC.region = animationC.frames[1][0]
            } else {
                textureC.region = animationC.frames[3][0]
            }
        }
    }
}
