package com.fandangle.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.fandangle.*
import com.fandangle.entity.components.*

object EntityFactory {

    private fun player(x: Float, y: Float): Entity {
        val e = Entity()

        val sheet: Texture = Main.assets.get(AssetPaths.PLAYER)
        val columns = 4
        val rows = 4
        val tmp = TextureRegion.split(sheet, sheet.width / columns, sheet.height / rows);
        val frames = Array(tmp.size, { i -> Array(columns, { j -> tmp[i][j] }) })
        val textureC = TextureComponent(frames[0][0])
        val animationComponent = PlayerAnimationComponent(frames)

        val transformC = TransformComponent()
        transformC.x = x
        transformC.y = y
        transformC.z = 100
        transformC.scale = 4f

        val width = textureC.region.regionWidth.toFloat() * transformC.scale
        val height = textureC.region.regionHeight.toFloat() * transformC.scale
        val physicsC = PhysicsComponent(width, height)

        val movementC = MovementComponent()
        val playerC = PlayerComponent()

        e.add(textureC)
                .add(animationComponent)
                .add(transformC)
                .add(physicsC)
                .add(movementC)
                .add(playerC)

        return e
    }

    fun playerControlled(x: Float, y: Float): Entity {
        val player = player(x, y)

        val keyboardInputC = KeyboardComponent()

        player.add(keyboardInputC)
        Main.engine.addEntity(player)

        return player
    }

    fun playerScripted(x: Float, y: Float, scriptName: String): Entity {
        val player = player(x, y)

        val scriptFile = Gdx.files.internal(scriptName)
        val script = scriptFile.toScript()
        val scriptC = ScriptComponent(script)

        player.add(scriptC)
        Main.engine.addEntity(player)

        return player
    }

    private fun enemy(x: Float, y: Float): Entity {
        val e = Entity()

        val sheet: Texture = Main.assets.get(AssetPaths.ENEMY)
        val columns = 2
        val rows = 2
        val tmp = TextureRegion.split(sheet, sheet.width / columns, sheet.height / rows);
        val frames = Array(tmp.size, { i -> Array(columns, { j -> tmp[i][j] }) })
        val textureC = TextureComponent(frames[0][0])
        val animationComponent = EnemyAnimationComponent(frames)

        val transformC = TransformComponent()
        transformC.x = x
        transformC.y = y
        transformC.scale = 4f

        // Enemy's movementC needs to be adjusted, because they don't jump and move slower
        val movementC = MovementComponent()
        movementC.horizontalVelocityMax = 3f
        movementC.jumpVelocity = 0f
        movementC.lerpXJumping = movementC.lerpX
        movementC.gravityJump = movementC.gravityFall

        val width = textureC.region.regionWidth.toFloat() * transformC.scale
        val height = textureC.region.regionHeight.toFloat() * transformC.scale
        val physicsC = PhysicsComponent(width, height)

        val enemyC = EnemyComponent()

        e.add(textureC)
                .add(animationComponent)
                .add(transformC)
                .add(movementC)
                .add(physicsC)
                .add(enemyC)

        return e
    }

    fun enemyControlled(x: Float, y: Float): Entity {
        val enemy = enemy(x, y)

        val keyboardInputC = KeyboardComponent()

        enemy.add(keyboardInputC)
        Main.engine.addEntity(enemy)

        return enemy
    }

    fun enemyScripted(x: Float, y: Float, scriptName: String): Entity {
        val enemy = enemy(x, y)

        val scriptFile = Gdx.files.internal(scriptName)
        val script = scriptFile.toScript()
        val scriptC = ScriptComponent(script)

        enemy.add(scriptC)
        Main.engine.addEntity(enemy)

        return enemy
    }

    fun enemyScriptedFlipped(x: Float, y: Float, scriptName: String): Entity {
        val enemy = enemy(x, y)

        val scriptFile = Gdx.files.internal(scriptName)
        val script = scriptFile.toScript()
        val scriptC = ScriptComponent(script)

        val movementC = enemy.getComponent(MovementComponent::class.java)
        movementC.gravityFall = -movementC.gravityFall

        val animationC = enemy.getComponent(EnemyAnimationComponent::class.java)
        animationC.frames.forEach {
            it.forEach { it.flip(false, true) }
        }

        enemy.add(scriptC)
        Main.engine.addEntity(enemy)

        return enemy
    }

    private fun movingBlock(x: Float, y: Float): Entity {
        val e = Entity()

        val texture: Texture = Main.assets.get(AssetPaths.BLOCK)
        val region = TextureRegion(texture)
        val textureC = TextureComponent(region)

        val transformC = TransformComponent()
        transformC.x = x
        transformC.y = y
        transformC.z = -100
        transformC.scale = 3f

        val width = textureC.region.regionWidth.toFloat() * transformC.scale
        val height = textureC.region.regionHeight.toFloat() * transformC.scale
        val physicsC = PhysicsComponent(width, height)

        val movementC = MovementComponent()
        movementC.horizontalVelocityMax = 3f
        movementC.lerpX = 0.1f
        movementC.lerpXJumping = movementC.lerpX
        movementC.jumpVelocity = 0f
        movementC.gravityFall = 0f
        movementC.gravityJump = 0f
        movementC.jumpLength = 0
        movementC.jumpCooldownLength = 0
        movementC.verticalVelocityMax = 3f
        movementC.verticalVelocityMin = 1f

        e.add(textureC)
                .add(transformC)
                .add(physicsC)
                .add(movementC)

        return e
    }

    fun movingBlockControlled(x: Float, y: Float): Entity {
        val block = movingBlock(x, y)

        val keyboardInputC = KeyboardComponent()

        block.add(keyboardInputC)
        Main.engine.addEntity(block)

        return block
    }

    fun movingBlockScripted(x: Float, y: Float, scriptName: String): Entity {
        val block = movingBlock(x, y)

        val scriptFile = Gdx.files.internal(scriptName)
        val script = scriptFile.toScript()
        val scriptC = ScriptComponent(script)

        block.add(scriptC)

        Main.engine.addEntity(block)

        return block
    }

    fun flag(): Entity {
        val e = Entity()

        val texture: Texture = Main.assets.get(AssetPaths.FLAG)
        val region = TextureRegion(texture)
        val textureC = TextureComponent(region)

        val transformC = TransformComponent()
        transformC.x = 2336f
        transformC.y = 300f
        transformC.scale = 3f

        val width = 2f
        val height = textureC.region.regionHeight.toFloat() * transformC.scale
        val physicsC = PhysicsComponent(width, height)

        val flagC = FlagComponent()

        e.add(textureC)
                .add(transformC)
                .add(physicsC)
                .add(flagC)

        Main.engine.addEntity(e)


        return e
    }

    fun flagFlipped(): Entity {
        val e = Entity()

        val texture: Texture = Main.assets.get(AssetPaths.FLAG)
        val region = TextureRegion(texture)
        region.flip(false, true)
        val textureC = TextureComponent(region)

        val transformC = TransformComponent()
        transformC.x = 2336f
        transformC.y = 420f
        transformC.scale = 3f

        val width = 2f
        val height = textureC.region.regionHeight.toFloat() * transformC.scale
        val physicsC = PhysicsComponent(width, height)

        val flagC = FlagComponent()

        e.add(textureC)
                .add(transformC)
                .add(physicsC)
                .add(flagC)

        Main.engine.addEntity(e)


        return e
    }

    fun levelBody(rectangle: Rectangle): Entity {
        val e = Entity()

        // Convert to pixels and round rectangle variables
        rectangle.x = Math.round(rectangle.x * Constants.MAP_TO_PIXEL).toFloat()
        rectangle.y = Math.round(rectangle.y * Constants.MAP_TO_PIXEL).toFloat()
        rectangle.width = Math.round(rectangle.width * Constants.MAP_TO_PIXEL).toFloat()
        rectangle.height = Math.round(rectangle.height * Constants.MAP_TO_PIXEL).toFloat()

        val transformC = TransformComponent()
        transformC.x = (rectangle.x + rectangle.width / 2)
        transformC.y = (rectangle.y + rectangle.height / 2)
        val physicsC = PhysicsComponent(rectangle.width, rectangle.height)

        e.add(transformC)
                .add(physicsC)

        Main.engine.addEntity(e)

        return e
    }

    fun image(x: Float, y: Float, textureName: String): Entity {
        val e = Entity()

        val texture: Texture = Main.assets.get(textureName)
        val region = TextureRegion(texture)
        val textureC = TextureComponent(region)

        val transformC = TransformComponent()
        transformC.x = x
        transformC.y = y
        transformC.z = -100

        e.add(textureC)
                .add(transformC)

        Main.engine.addEntity(e)

        return e
    }
}

// Convert a text script to an array of operations containing instructions and operands encoded as integers
fun FileHandle.toScript(): Array<IntArray> {
    val text = this.readString()
    val strings = text.lines()

    val operations = mutableListOf<IntArray>()
    strings.forEach {
        // Remove tabs
        val scriptString = it.replace("\t", "")
        // Separate instruction and operands part
        val spaceSeparator = scriptString.indexOf(" ")

        // A valid space index means that there are operands in the line
        if (spaceSeparator != -1) {
            val instructionString = scriptString.substring(0, spaceSeparator)
            val instruction = Constants.operationCodes[instructionString]!!

            if (instructionString == "if") {
                val operandString = scriptString.substring(spaceSeparator + 1)
                val operandsList = operandString.split(" ")
                val variable = Constants.variableCodes[operandsList[0]]!!
                val condition = Constants.conditionCodes[operandsList[1]]!!
                val value = operandsList[2].toInt()
                operations.add(intArrayOf(instruction, variable, condition, value))
            } else {
                val operandString = scriptString.substring(spaceSeparator + 1)
                val operand = operandString.toInt()
                operations.add(intArrayOf(instruction, operand))
            }
        } else { // No operands found
            val instructionString = scriptString
            val instruction = Constants.operationCodes[instructionString]!!
            operations.add(intArrayOf(instruction))
        }
    }

    return Array(operations.size, { i -> operations[i] })
}
