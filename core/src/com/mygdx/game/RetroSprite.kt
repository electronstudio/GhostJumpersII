package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle

open class RetroSprite(textureRegion: TextureRegion, var animation: Animation<TextureRegion>?=null) : Sprite
(textureRegion) {
    //val bgCollisionPoints = listOf<Vec>(Vec(0f,0f), Vec(16f, 0f))

    constructor(tex: Texture) : this(TextureRegion(tex))

    open val spriteCollisionShape = Rectangle(0f, 0f, textureRegion.regionWidth.toFloat(), textureRegion.regionHeight
            .toFloat())

    var dead=false

  //  val spriteCollisions = ArrayList<String>()
    val backgroundCollisions = HashSet<String>()

    var xVel=0f
    var yVel=0f

    var timer=0f

    val defaultAnim = Animation(0.1f, textureRegion)

    var flip=false

    open fun update() {
        timer+=Gdx.graphics.deltaTime
        animation?.let {
            setRegion(it.getKeyFrame(timer, true))
            setScale(if (flip) -1f else 1f, 1f)
        }
    }



    var savedX = 0f
    var savedY = 0f

    fun savePosition() {
        savedX = x
        savedY = y
    }

    fun restorePosition() {
        x = savedX
        y = savedY
    }

    fun collisionTest(others: java.util.ArrayList<RetroSprite>):Boolean {
        val rect1 = Rectangle(x+ spriteCollisionShape.x, y+ spriteCollisionShape.y, spriteCollisionShape.width, spriteCollisionShape.height)
        val rect2 = Rectangle()
        others.forEach {
            rect2.x=it.x+it.spriteCollisionShape.x
            rect2.y=it.y+it.spriteCollisionShape.y
            rect2.width=it.width
            rect2.height=it.height
            if(rect1.overlaps(rect2)){
                return true
            }
        }
        return false
    }

    fun collisionTestRect(others: List<Rectangle>):Boolean {
        val rect1 = Rectangle(x+ spriteCollisionShape.x, y+ spriteCollisionShape.y, spriteCollisionShape.width, spriteCollisionShape.height)
        others.forEach {
            if(rect1.overlaps(it)){
                return true
            }
        }
        return false
    }

    /** only tests the 4 corners of the collision box */
    fun collisionTest(collisionShape: Rectangle, background: TiledMap) {
        backgroundCollisions.removeIf { s -> true } // is this clearing faster than making a new HashSet object?
        background.layers.forEach {
            if(it is TiledMapTileLayer) {
//                val layer = it as TiledMapTileLayer
                testPointBackgroundCollision(x + collisionShape.x, y + collisionShape.y, it)
                testPointBackgroundCollision(x + collisionShape.x + collisionShape.width, y + collisionShape.y, it)
                testPointBackgroundCollision(x + collisionShape.x, y + collisionShape.y + collisionShape.height, it)
                testPointBackgroundCollision(x + collisionShape.x + collisionShape.width, y + collisionShape.y + collisionShape.height, it)
            }else { // it is an object map layer

            }
        }
    }

    private fun testPointBackgroundCollision(x1: Float, y1: Float, layer: TiledMapTileLayer) {
        val cell = layer.getCell((x1/layer.tileWidth).toInt(), (y1/layer.tileHeight).toInt())
        if(cell!=null) {
            cell.tile.properties.keys.forEach {
                backgroundCollisions.add(it)
            }
        }
    }

//    open fun onPlatform(): Boolean{
//
//    }

}