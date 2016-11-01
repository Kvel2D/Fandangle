package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.fandangle.*
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.*
import com.fandangle.entity.toScript

/*
 * Resolves contacts obtained from PhysicsS
 */
class ContactSystem : EntitySystem {
    val contacts = mutableListOf<Contact>()

    constructor(priority: Int) : super(priority)

    override fun update(deltaTime: Float) {
        contacts.forEach {
            processContact(it)
        }
        contacts.clear()
    }

    fun processContact(contact: Contact) {

        // Contact between player and enemy
        if (Mappers.playerComponent.has(contact.entityA) && Mappers.enemyComponent.has(contact.entityB)) {
            val player = contact.entityA
            val enemy = contact.entityB
            val playerTransformC = Mappers.transformComponent.get(player)
            val enemyTransformC = Mappers.transformComponent.get(enemy)
            val playerPhysicsC = Mappers.physicsComponent.get(player)
            val enemyPhysicsC = Mappers.physicsComponent.get(enemy)
            val playerYMin = playerTransformC.y - playerPhysicsC.halfHeight
            val enemyYMax = enemyTransformC.y + enemyPhysicsC.halfHeight
            // Kill player if not on top of enemy
            if (playerYMin < enemyYMax) {
                player.add(DeadComponent(Constants.DEATH_DURATION))
            } else {
                // Kill enemy and bounce the player otherwise
                enemy.add(DeadComponent(Constants.DEATH_DURATION))
                val movementC = Mappers.movementComponent.get(player)
                movementC.bounceFramesLeft = movementC.bounceLength
                playerPhysicsC.velocity.y = movementC.jumpVelocity
                playerPhysicsC.grounded = false
            }
        } else if (Mappers.playerComponent.has(contact.entityB) && Mappers.enemyComponent.has(contact.entityA)) {
            val player = contact.entityB
            val enemy = contact.entityA
            val playerTransformC = Mappers.transformComponent.get(player)
            val enemyTransformC = Mappers.transformComponent.get(enemy)
            val playerPhysicsC = Mappers.physicsComponent.get(player)
            val enemyPhysicsC = Mappers.physicsComponent.get(enemy)
            val playerYMin = playerTransformC.y - playerPhysicsC.halfHeight
            val enemyYMax = enemyTransformC.y + enemyPhysicsC.halfHeight
            // Kill player if not on top of enemy
            if (playerYMin < enemyYMax) {
                player.add(DeadComponent(Constants.DEATH_DURATION))
            } else {
                // Kill enemy and bounce the player otherwise
                enemy.add(DeadComponent(Constants.DEATH_DURATION))
                val movementC = Mappers.movementComponent.get(player)
                movementC.bounceFramesLeft = movementC.bounceLength
                playerPhysicsC.velocity.y = movementC.jumpVelocity
                playerPhysicsC.grounded = false
            }
        }
        else if (Mappers.playerComponent.has(contact.entityA) && Mappers.flagComponent.has(contact.entityB)) {
            val player = contact.entityA
            val flag = contact.entityB

            // Turn flag into just a sprite, so there's no player-flag contacts after this one
            flag.remove(FlagComponent::class.java)
            flag.remove(PhysicsComponent::class.java)
            // Detach camera from current target and center camera on flag
            // Change lerp to make the movement smooth
            engine.getSystem(CameraSystem::class.java).setTarget(flag)

            // Set up player entity for the ending sequence
            // Remove any kind of input
            player.remove(KeyboardComponent::class.java)
            player.remove(ScriptComponent::class.java)
            // Reset all physics values
            val physicsC = Mappers.physicsComponent.get(player)
            physicsC.velocity.x = 0f
            physicsC.velocity.y = 0f
            physicsC.grounded = false
            // Change movementC for movement without gravity
            val movementC = Mappers.movementComponent.get(player)
            movementC.gravityFall = 0f
            movementC.gravityJump = 0f
            movementC.verticalVelocityMax = 6f
            movementC.verticalVelocityMin = 1f
            movementC.left = false
            movementC.right = false
            movementC.jump = false
            movementC.down = false
            movementC.up = false
            movementC.jump = false
            // Add a script to act out the "ending cutscene"
            // level 5 has a special upside down ending
            val scriptFile: FileHandle =
                    if (Main.currentLevel == 5) {
                        Gdx.files.internal(AssetPaths.PLAYER_EXIT_FLIPPED)
                    } else {
                        Gdx.files.internal(AssetPaths.PLAYER_EXIT)
                    }
            // Replace animationC with an upside down version
            if (Main.currentLevel == 5) {
                val oldAnimationC = Mappers.playerAnimationComponent.get(player)
                val newAnimationC = UpsideDownPlayerAnimationComponent(oldAnimationC.frames)
                player.remove(PlayerAnimationComponent::class.java)
                player.add(newAnimationC)
            }
            val script = scriptFile.toScript()
            val scriptC = ScriptComponent(script)
            player.add(scriptC)
        }
    }

    fun addContact(entityA: Entity, entityB: Entity) {
        contacts.add(Contact(entityA, entityB))
    }

    class Contact {
        val entityA: Entity
        val entityB: Entity

        constructor(entityA: Entity, entityB: Entity) {
            this.entityA = entityA
            this.entityB = entityB
        }
    }
}

