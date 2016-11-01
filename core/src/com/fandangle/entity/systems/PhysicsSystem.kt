package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Rectangle
import com.fandangle.Main
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.PhysicsComponent
import com.fandangle.entity.components.TransformComponent

/*
 * Collides physicsC entities with other physicsC entities
 * Sets physics.grounded
 * Adds contacts to ContactS, when entity touches another entity
 * Processes collisions per entity one by one, so lots of bad stuff can happen when moving entities collide
 */
class PhysicsSystem : IteratingSystem {

    constructor(priority: Int) :
    super(Family.all(PhysicsComponent::class.java, TransformComponent::class.java).get(), priority)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val physicsC = Mappers.physicsComponent.get(entity)
        val transformC = Mappers.transformComponent.get(entity)

        var xMax = transformC.x + physicsC.dx + physicsC.halfWidth
        var xMin = transformC.x + physicsC.dx - physicsC.halfWidth
        var yMax = transformC.y + physicsC.dy + physicsC.halfHeight
        var yMin = transformC.y + physicsC.dy - physicsC.halfHeight

        // Horizontal movement
        if (physicsC.velocity.x > 0) {
            var closestDistance = 100000f
            var closestEntityIndex = 0
            for (i in 0..entities.size() - 1) {
                // Skip itself
                if (entities[i] == entity) {
                    continue
                }

                val physicsCOther = Mappers.physicsComponent.get(entities[i])
                val transformCOther = Mappers.transformComponent.get(entities[i])
                val xMaxOther = transformCOther.x + physicsCOther.dx + physicsCOther.halfWidth
                val xMinOther = transformCOther.x + physicsCOther.dx - physicsCOther.halfWidth
                val yMaxOther = transformCOther.y + physicsCOther.dy + physicsCOther.halfHeight
                val yMinOther = transformCOther.y + physicsCOther.dy - physicsCOther.halfHeight

                // Skip if to the left, below or above
                if (xMin >= xMaxOther || yMin >= yMaxOther || yMax <= yMinOther) {
                    continue
                }

                val distance = xMinOther - xMax
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestEntityIndex = i
                }
            }

            if (closestDistance < physicsC.velocity.x) {
                physicsC.velocity.x = 0f
                transformC.x += closestDistance
                xMax += closestDistance
                xMin += closestDistance
                engine.getSystem(ContactSystem::class.java).addContact(entity, entities[closestEntityIndex])
            } else {
                transformC.x += physicsC.velocity.x
                xMax += physicsC.velocity.x
                xMin += physicsC.velocity.x
            }
        } else if (physicsC.velocity.x < 0) {
            var closestDistance = 100000f
            var closestEntityIndex = 0
            for (i in 0..entities.size() - 1) {
                // Skip itself
                if (entities[i] == entity) {
                    continue
                }

                val physicsCOther = Mappers.physicsComponent.get(entities[i])
                val transformCOther = Mappers.transformComponent.get(entities[i])
                val xMaxOther = transformCOther.x + physicsCOther.dx + physicsCOther.halfWidth
                val xMinOther = transformCOther.x + physicsCOther.dx - physicsCOther.halfWidth
                val yMaxOther = transformCOther.y + physicsCOther.dy + physicsCOther.halfHeight
                val yMinOther = transformCOther.y + physicsCOther.dy - physicsCOther.halfHeight

                // Skip if to the right, below or above
                if (xMax <= xMinOther || yMin >= yMaxOther || yMax <= yMinOther) {
                    continue
                }

                val distance = xMin - xMaxOther
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestEntityIndex = i
                }
            }

            if (closestDistance < - physicsC.velocity.x) {
                physicsC.velocity.x = 0f
                transformC.x -= closestDistance
                xMax -= closestDistance
                xMin -= closestDistance
                engine.getSystem(ContactSystem::class.java).addContact(entity, entities[closestEntityIndex])
            } else {
                transformC.x += physicsC.velocity.x
                xMax += physicsC.velocity.x
                xMin += physicsC.velocity.x
            }
        }

        // Vertical movement
        if (physicsC.velocity.y > 0) {
            var closestDistance = 100000f
            var closestEntityIndex = 0
            for (i in 0..entities.size() - 1) {
                // Skip itself
                if (entities[i] == entity) {
                    continue
                }

                val physicsCOther = Mappers.physicsComponent.get(entities[i])
                val transformCOther = Mappers.transformComponent.get(entities[i])
                val xMaxOther = transformCOther.x + physicsCOther.dx + physicsCOther.halfWidth
                val xMinOther = transformCOther.x + physicsCOther.dx - physicsCOther.halfWidth
                val yMaxOther = transformCOther.y + physicsCOther.dy + physicsCOther.halfHeight
                val yMinOther = transformCOther.y + physicsCOther.dy - physicsCOther.halfHeight

                // Skip if below, to the left or to the right
                if (yMin >= yMaxOther || xMin >= xMaxOther || xMax <= xMinOther) {
                    continue
                }

                val distance = yMinOther - yMax
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestEntityIndex = i
                }
            }

            if (closestDistance < physicsC.velocity.y) {
                physicsC.velocity.y = 0f
                transformC.y += closestDistance
                yMax += closestDistance
                yMin += closestDistance
                engine.getSystem(ContactSystem::class.java).addContact(entity, entities[closestEntityIndex])
            } else {
                transformC.y += physicsC.velocity.y
                yMax += physicsC.velocity.y
                yMin += physicsC.velocity.y
            }
        } else if (physicsC.velocity.y < 0) {
            var closestDistance = 100000f
            var closestEntityIndex = 0
            for (i in 0..entities.size() - 1) {
                // Skip itself
                if (entities[i] == entity) {
                    continue
                }

                val physicsCOther = Mappers.physicsComponent.get(entities[i])
                val transformCOther = Mappers.transformComponent.get(entities[i])
                val xMaxOther = transformCOther.x + physicsCOther.dx + physicsCOther.halfWidth
                val xMinOther = transformCOther.x + physicsCOther.dx - physicsCOther.halfWidth
                val yMaxOther = transformCOther.y + physicsCOther.dy + physicsCOther.halfHeight
                val yMinOther = transformCOther.y + physicsCOther.dy - physicsCOther.halfHeight

                // Skip if above, to the left or to the right
                if (yMax <= yMinOther || xMin >= xMaxOther || xMax <= xMinOther) {
                    continue
                }

                val distance = yMin - yMaxOther
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestEntityIndex = i
                }
            }

            if (closestDistance < - physicsC.velocity.y) {
                physicsC.velocity.y = 0f
                transformC.y -= closestDistance
                yMax -= closestDistance
                yMin -= closestDistance
                engine.getSystem(ContactSystem::class.java).addContact(entity, entities[closestEntityIndex])
            } else {
                transformC.y += physicsC.velocity.y
                yMax += physicsC.velocity.y
                yMin += physicsC.velocity.y
            }
        }

        // Grounded check
        physicsC.grounded = false
        if (physicsC.velocity.y == 0f) {
            // Update bounds with new transform
            yMin = transformC.y + physicsC.dy - physicsC.halfHeight - 1f // move yMin down by 1 for collision check

            for (i in 0..entities.size() - 1) {
                // Skip itself
                if (entities[i] == entity) {
                    continue
                }

                val physicsCOther = Mappers.physicsComponent.get(entities[i])
                val transformCOther = Mappers.transformComponent.get(entities[i])
                val xMaxOther = transformCOther.x + physicsCOther.dx + physicsCOther.halfWidth
                val xMinOther = transformCOther.x + physicsCOther.dx - physicsCOther.halfWidth
                val yMaxOther = transformCOther.y + physicsCOther.dy + physicsCOther.halfHeight
                // Entity is grounded if it's intersecting with a physics object that is below it
                if (yMax >= yMaxOther
                        && xMin < xMaxOther && xMax > xMinOther && yMin < yMaxOther) {
                    physicsC.grounded = true
                }
            }
        }

        // Round position coordinates and save the remainder for the next update
        val xReal = transformC.x + physicsC.dx
        val yReal = transformC.y + physicsC.dy
        transformC.x = Math.round(xReal).toFloat()
        transformC.y = Math.round(yReal).toFloat()
        physicsC.dx = xReal - transformC.x
        physicsC.dy = yReal - transformC.y
    }
}