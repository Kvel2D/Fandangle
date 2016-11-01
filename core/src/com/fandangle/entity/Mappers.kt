package com.fandangle.entity

import com.badlogic.ashley.core.ComponentMapper
import com.fandangle.entity.components.*

object Mappers {
    val enemyAnimationComponent = ComponentMapper.getFor(EnemyAnimationComponent::class.java)
    val enemyComponent = ComponentMapper.getFor(EnemyComponent::class.java)
    val flagComponent = ComponentMapper.getFor(FlagComponent::class.java)
    val keyboardComponent = ComponentMapper.getFor(KeyboardComponent::class.java)
    val movementComponent = ComponentMapper.getFor(MovementComponent::class.java)
    val physicsComponent = ComponentMapper.getFor(PhysicsComponent::class.java)
    val playerAnimationComponent = ComponentMapper.getFor(PlayerAnimationComponent::class.java)
    val playerComponent = ComponentMapper.getFor(PlayerComponent::class.java)
    val deadComponent = ComponentMapper.getFor(DeadComponent::class.java)
    val scriptComponent = ComponentMapper.getFor(ScriptComponent::class.java)
    val textureComponent = ComponentMapper.getFor(TextureComponent::class.java)
    val transformComponent = ComponentMapper.getFor(TransformComponent::class.java)
    val upsideDownPlayerAnimationComponent = ComponentMapper.getFor(UpsideDownPlayerAnimationComponent::class.java)
}