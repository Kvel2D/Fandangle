package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.EnemyAnimationComponent
import com.fandangle.entity.components.PhysicsComponent
import com.fandangle.entity.components.TextureComponent

/*
 * Changes textureC.region based on state
 */
class EnemyAnimationSystem : IteratingSystem {
    constructor() : super(Family.all(EnemyAnimationComponent::class.java, TextureComponent::class.java, PhysicsComponent::class.java).get())

    constructor(priority: Int) : super(Family.all(EnemyAnimationComponent::class.java, TextureComponent::class.java, PhysicsComponent::class.java).get(), priority)

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val animationC = Mappers.enemyAnimationComponent.get(entity)
        val textureC = Mappers.textureComponent.get(entity)
        val physicsC = Mappers.physicsComponent.get(entity)

        if (Mappers.deadComponent.has(entity)) {
            textureC.region = animationC.frames[1][0]
        }
        else if (physicsC.velocity.x == 0f) {
            textureC.region = animationC.frames[0][0]

            animationC.frameTimer = 0
            animationC.currentFrame = 0
        } else {
            animationC.frameTimer -= 1
            if (animationC.frameTimer <= 0) {
                animationC.frameTimer =
                        if (Math.abs(physicsC.velocity.x) > 5f) {
                            animationC.frameDuration
                        }
                        else {
                            animationC.frameDuration * 2
                        }
                animationC.currentFrame++
                // Loop back around
                if (animationC.currentFrame == animationC.frames[0].size) {
                    animationC.currentFrame = 0
                }
                textureC.region = animationC.frames[0][animationC.currentFrame]
            }
        }
    }
}
