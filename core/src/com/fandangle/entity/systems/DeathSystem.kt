package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.fandangle.Main
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.DeadComponent
import com.fandangle.entity.components.MovementComponent
import com.fandangle.entity.components.PhysicsComponent

class DeathSystem : IteratingSystem {

    constructor() :
    super(Family.all(DeadComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(DeadComponent::class.java).get(), priority)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val deadC = Mappers.deadComponent.get(entity)
        val transformC = Mappers.transformComponent.get(entity)

        // Start death
        if (deadC.timer == 0) {
            entity.remove(MovementComponent::class.java)
            entity.remove(PhysicsComponent::class.java)
            deadC.timer = deadC.timerDuration
        }

        // Do the death animation(moving down and rotating)
        transformC.x -= 1f
        transformC.y -= 4f
        if (deadC.timer % 10 == 0) {
            transformC.angle -= 90
        }

        deadC.timer--
        // End death
        if (deadC.timer == 0) {
            // Game over if the dead entity is a player
            if (Mappers.playerComponent.has(entity)) {
                Main.gameState = Main.Companion.STATE.GAMEOVER
            }
            engine.removeEntity(entity)
        }
    }
}

