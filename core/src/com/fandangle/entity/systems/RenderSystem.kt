package com.fandangle.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMapRenderer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.fandangle.AssetPaths
import com.fandangle.Constants
import com.fandangle.Main
import com.fandangle.entity.Mappers
import com.fandangle.entity.components.TextureComponent
import com.fandangle.entity.components.TransformComponent
import java.util.*

class RenderSystem : SortedIteratingSystem {
    private val camera: OrthographicCamera
    private val batch: SpriteBatch
    private var viewportWidth = 0f
    private var viewportHeight = 0f
    val mapRenderer = OrthogonalTiledMapRenderer(Main.assets.get(AssetPaths.LEVELS_FOLDER + "/map1.tmx"), Constants.MAP_TO_PIXEL)

    constructor(batch: SpriteBatch, cameraSystem: CameraSystem) :
    super(Family.all(TextureComponent::class.java, TransformComponent::class.java).get(), ZComparator()) {
        this.batch = batch
        this.camera = cameraSystem.camera
        this.mapRenderer.setView(camera)
    }

    constructor(priority: Int, batch: SpriteBatch, cameraSystem: CameraSystem) :
    super(Family.all(TextureComponent::class.java, TransformComponent::class.java).get(), ZComparator(), priority) {
        this.batch = batch
        this.camera = cameraSystem.camera
        this.mapRenderer.setView(camera)
    }

    override fun update(deltaTime: Float) {
        mapRenderer.setView(camera)
        mapRenderer.render()

        viewportWidth = camera.viewportWidth * camera.zoom
        viewportHeight = camera.viewportHeight * camera.zoom

        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        getEngine()
        val transformC = Mappers.transformComponent.get(entity)
        val textureC = Mappers.textureComponent.get(entity)

        val scale = transformC.scale
        val width = textureC.region.regionWidth.toFloat()
        val height = textureC.region.regionHeight.toFloat()
        val originX = 0.5f * width
        val originY = 0.5f * height

        // Frustum check
        val xMin = transformC.x + width * scale - originX
        val xMax = transformC.x - originX
        val yMin = transformC.y + height * scale - originY
        val yMax = transformC.y - originY
        if (xMin < camera.position.x - viewportWidth / 2
                || xMax > camera.position.x + viewportWidth / 2
                || yMin < camera.position.y - viewportHeight / 2
                || yMax > camera.position.y + viewportHeight / 2) {
            return
        }

        batch.draw(textureC.region,
                transformC.x - originX,
                transformC.y - originY,
                originX,
                originY,
                width,
                height,
                scale,
                scale,
                transformC.angle)
    }

    private class ZComparator : Comparator<Entity> {
        override fun compare(e1: Entity, e2: Entity): Int {
            return Integer.signum(Mappers.transformComponent.get(e1).z - Mappers.transformComponent.get(e2).z)
        }
    }
}