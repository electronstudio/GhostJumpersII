package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.Prefs

/*
 * Some functions all our sprites have in common
 */
abstract class RetroSprite(textureRegion: TextureRegion) : Sprite
(textureRegion) {
    var animation: Animation<TextureRegion>? = null

    constructor(a: Animation<TextureRegion>) : this(a.keyFrames.first()) {
        animation = a
    }

    abstract val spriteCollisionShape: Rectangle

    var dead = false

    val backgroundCollisions = HashSet<String>()

    var xVel = 0f
    var yVel = 0f

    var timer = 0f

    var flip = false

    /*
     * We will call this every frame
     */
    abstract fun update()

    override fun draw(batch: Batch?) {
        super.draw(batch)
    }

    /* Some sprites dont animate so dont need to call this */
    fun doAnimation() {
        timer += Gdx.graphics.deltaTime
        animation?.let {
            setRegion(it.getKeyFrame(timer, true))
            if (flip) flipSprite() else unFlipSprite()
        }
    }

    fun collisionTest(others: ArrayList<RetroSprite>): Boolean {
        val rect1 = Rectangle(
                x + spriteCollisionShape.x, y + spriteCollisionShape.y,
                spriteCollisionShape.width, spriteCollisionShape.height
        )
        val rect2 = Rectangle()
        others.forEach {
            rect2.x = it.x + it.spriteCollisionShape.x
            rect2.y = it.y + it.spriteCollisionShape.y
            rect2.width = it.spriteCollisionShape.width
            rect2.height = it.spriteCollisionShape.height
            if (rect1.overlaps(rect2)) {
                return true
            }
        }
        return false
    }

    fun collisionTestRect(others: List<Rectangle>): Boolean {
        val rect1 = Rectangle(
                x + spriteCollisionShape.x, y + spriteCollisionShape.y,
                spriteCollisionShape.width, spriteCollisionShape.height)
        others.forEach {
            if (rect1.overlaps(it)) {
                return true
            }
        }
        return false
    }

    /* only tests the 4 corners of the collision box */
    fun collisionTest(collisionShape: Rectangle, background: TiledMap) {
        backgroundCollisions.removeAll(backgroundCollisions)
     //   backgroundCollisions.removeIf { _ -> true } // is this clearing faster than making a new HashSet object?
        background.layers.forEach {
            if (it is TiledMapTileLayer) {
                testPointBackgroundCollision(x + collisionShape.x, y + collisionShape.y, it)
                testPointBackgroundCollision(x + collisionShape.x + collisionShape.width, y + collisionShape.y, it)
                testPointBackgroundCollision(x + collisionShape.x, y + collisionShape.y + collisionShape.height, it)
                testPointBackgroundCollision(x + collisionShape.x + collisionShape.width, y + collisionShape.y + collisionShape.height, it)
            } else { // it is an object map layer
            }
        }
    }

    private fun testPointBackgroundCollision(x1: Float, y1: Float, layer: TiledMapTileLayer) {
        val cell = layer.getCell((x1 / layer.tileWidth).toInt(), (y1 / layer.tileHeight).toInt())
        if (cell != null) {
            cell.tile.properties.keys.forEach {
                backgroundCollisions.add(it)
            }
        }
    }

    fun doSimplePhysics() {
        x += xVel * Gdx.graphics.deltaTime
        y += yVel * Gdx.graphics.deltaTime
    }

    fun flipSprite() {
        setScale(-1f, 1f)
    }

    fun unFlipSprite() {
        setScale(1f, 1f)
    }
}