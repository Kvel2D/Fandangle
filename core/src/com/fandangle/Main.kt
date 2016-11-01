package com.fandangle

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Rectangle
import com.fandangle.entity.EntityFactory
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.MovementComponent
import com.fandangle.entity.components.PhysicsComponent
import com.fandangle.entity.systems.*

class Main : ApplicationAdapter() {
    companion object {
        val engine = Engine()
        val assets = AssetManager()
        lateinit private var batch: SpriteBatch
        lateinit private var player: Entity

        enum class STATE {
            NORMAL,
            GAMEOVER,
            LEVEL_END
        }

        var gameState = STATE.NORMAL
        var currentLevel = 1
        private var deaths = 0
        private var gameoverTimer = Constants.GAMEOVER_DURATION
    }

    override fun create() {
        batch = SpriteBatch()
        Texture.setAssetManager(assets)

        // Load assets
        AssetPaths.textures.forEach { assets.load(it, Texture::class.java) }
        AssetPaths.sounds.forEach { assets.load(it, Sound::class.java) }
        AssetPaths.fonts.forEach { assets.load(it, BitmapFont::class.java) }
        assets.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()));
        for (i in 1..8) {
            assets.load(AssetPaths.LEVELS_FOLDER + "/map$i.tmx", TiledMap::class.java)
        }
        assets.finishLoading()

        // Set up engine
        engine.addSystem(KeyboardSystem(0))
        engine.addSystem(ScriptSystem(1))
        engine.addSystem(MovementSystem(2))
        engine.addSystem(PhysicsSystem(10))
        engine.addSystem(ContactSystem(11))
        engine.addSystem(CameraSystem(20))
        engine.addSystem(PlayerAnimationSystem(30))
        engine.addSystem(UpsideDownPlayerAnimationSystem(31))
        engine.addSystem(EnemyAnimationSystem(32))
        engine.addSystem(DeathSystem(40))
        engine.addSystem(RenderSystem(50, batch, engine.getSystem(CameraSystem::class.java)))
        Gdx.input.inputProcessor = engine.getSystem(KeyboardSystem::class.java).inputProcessor

        loadLevel()
    }

    override fun render() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit()
        }

        when (gameState) {
            STATE.NORMAL -> {
                Gdx.gl.glClearColor(186f / 255f, 203f / 255f, 254f / 255f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

                engine.update(Gdx.graphics.deltaTime)

                val playerTransformC = Mappers.transformComponent.get(player)
                if (playerTransformC.y < -50f && currentLevel <= 6) {
                    // Death by falling
                    gameState = STATE.GAMEOVER
                } else if (playerTransformC.x > 3000f && currentLevel <= 6) {
                    // Reached the end of the level
                    gameState = STATE.LEVEL_END
                } else if (currentLevel == 8 && playerTransformC.y < -490f) {
                    // End2 ending
                    engine.getSystem(CameraSystem::class.java).setTarget(null)
                    player.remove(MovementComponent::class.java)
                    player.remove(PhysicsComponent::class.java)
                    playerTransformC.y = 1000f
                }
            }
            STATE.GAMEOVER -> {
                Gdx.gl.glClearColor(186f / 255f, 203f / 255f, 254f / 255f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

                engine.update(Gdx.graphics.deltaTime)

                // Start timer
                if (gameoverTimer == 0) {
                    gameoverTimer = Constants.GAMEOVER_DURATION
                }

                if (gameoverTimer > 0) {
                    gameoverTimer--
                    // End timer
                    if (gameoverTimer == 0) {
                        gameState = STATE.NORMAL
                        deaths++
                        engine.removeAllEntities()
                        loadLevel()
                    }
                }
            }
            STATE.LEVEL_END -> {
                gameState = STATE.NORMAL
                engine.removeAllEntities()
                engine.getSystem(CameraSystem::class.java).setTarget(null)
                if (currentLevel == 6) {
                    // last "real" level
                    if (deaths > 10) {
                        currentLevel = 8
                    } else {
                        currentLevel = 7
                    }
                } else {
                    currentLevel++
                }
                loadLevel()
            }
        }
    }

    private fun loadLevel() {
        when (currentLevel) {
            1 -> {
                player = EntityFactory.playerControlled(100f, 80f)
                engine.getSystem(CameraSystem::class.java).setTarget(player)
                EntityFactory.enemyScripted(500f, 76f, AssetPaths.ENEMY_PATROL)
                EntityFactory.enemyScripted(700f, 76f, AssetPaths.ENEMY_PATROL)
                EntityFactory.flag()
            }
            2 -> {
                player = EntityFactory.playerScripted(100f, 80f, AssetPaths.PLAYER_LEVEL2)
                EntityFactory.enemyScripted(500f, 76f, AssetPaths.ENEMY_PATROL)
                val enemy = EntityFactory.enemyControlled(800f, 76f)
                engine.getSystem(CameraSystem::class.java).setTarget(enemy)
                EntityFactory.flag()
            }
            3 -> {
                player = EntityFactory.playerScripted(100f, 80f, AssetPaths.PLAYER_LEVEL3)
                EntityFactory.enemyScripted(500f, 76f, AssetPaths.ENEMY_PATROL)
                val enemy = EntityFactory.enemyControlled(800f, 76f)
                engine.getSystem(CameraSystem::class.java).setTarget(enemy)
                EntityFactory.flag()
            }
            4 -> {
                player = EntityFactory.playerScripted(100f, 80f, AssetPaths.PLAYER_LEVEL4)
                EntityFactory.enemyScripted(500f, 76f, AssetPaths.ENEMY_PATROL)
                EntityFactory.enemyScripted(800f, 76f, AssetPaths.ENEMY_PATROL)
                EntityFactory.flag()
                val block = EntityFactory.movingBlockControlled(1896f, 71f)
                engine.getSystem(CameraSystem::class.java).setTarget(block)
            }
            5 -> {
                EntityFactory.enemyScriptedFlipped(500f, 630f, AssetPaths.ENEMY_PATROL_LONG)
                EntityFactory.enemyScriptedFlipped(800f, 630f, AssetPaths.ENEMY_PATROL_LONG)
                EntityFactory.flagFlipped()
                val block = EntityFactory.movingBlockControlled(120f, 71f)
                engine.getSystem(CameraSystem::class.java).setTarget(block)
                player = EntityFactory.playerScripted(120f, 630f, AssetPaths.PLAYER_LEVEL5)
            }
            6 -> {
                EntityFactory.enemyControlled(710f, 76f)
                EntityFactory.enemyControlled(960f, 76f)
                player = EntityFactory.playerControlled(100f, 80f)
                engine.getSystem(CameraSystem::class.java).setTarget(player)
                EntityFactory.flag()
            }
            7 -> {
                EntityFactory.enemyScripted(3500f, 76f, AssetPaths.ENEMY_DANCE)
                EntityFactory.enemyScripted(3700f, 76f, AssetPaths.ENEMY_DANCE)
                EntityFactory.enemyScripted(3900f, 76f, AssetPaths.ENEMY_DANCE)
                player = EntityFactory.playerScripted(3001f, 80f, AssetPaths.PLAYER_END1)
                val cameraSystem = engine.getSystem(CameraSystem::class.java)
                cameraSystem.camera.position.x += 1280f
                cameraSystem.camera.update()
                EntityFactory.image(3616f, 360f, AssetPaths.END1_BACKGROUND)
            }
            8 -> {
                player = EntityFactory.playerScripted(3001f, 80f, AssetPaths.PLAYER_END2)
                val cameraSystem = engine.getSystem(CameraSystem::class.java)
                cameraSystem.setTargetInstant(player)
                cameraSystem.camera.position.x += 1279f
                EntityFactory.image(3870f, -500f, AssetPaths.END2_BACKGROUND)
            }
        }
        // Load level entities from the map
        val map: TiledMap = assets.get(AssetPaths.LEVELS_FOLDER + "/map$currentLevel.tmx")
        val objects = map.layers.get("objects").objects;
        objects.forEach {
            val rectangle = Rectangle((it as RectangleMapObject).rectangle)
            EntityFactory.levelBody(rectangle)
        }

        val mapRenderer = engine.getSystem(RenderSystem::class.java).mapRenderer
        mapRenderer.map = map
    }

    override fun dispose() {
        assets.dispose()
        batch.dispose()
    }
}