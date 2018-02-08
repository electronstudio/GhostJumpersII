package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle

open class RetroSprite(texture: Texture, var animation: Animation<Texture>?=null) : Sprite(texture) {
    //val bgCollisionPoints = listOf<Vec>(Vec(0f,0f), Vec(16f, 0f))

    open val collisionShape = Rectangle(0f, 0f, texture.width.toFloat(), texture.height.toFloat())

    var dead=false

    val spriteCollisions = ArrayList<String>()
    val backgroundCollisions = HashSet<String>()

    var xVel=0f
    var yVel=0f

    var timer=0f

    val defaultAnim = Animation(0.1f, texture)

    var flip=false

    open fun update(delta: Float) {
        timer+=delta
        animation?.let {
            texture = it.getKeyFrame(timer, true)
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
        val rect1 = Rectangle(x+ collisionShape.x, y+ collisionShape.y, collisionShape.width, collisionShape.height)
        val rect2 = Rectangle()
        others.forEach {
            rect2.x=it.x+it.collisionShape.x
            rect2.y=it.y+it.collisionShape.y
            rect2.width=it.width
            rect2.height=it.height
            if(rect1.overlaps(rect2)){
                return true
            }
        }
        return false
    }

    /** only tests the 4 corners of the collision box */
    fun collisionTest(background: TiledMap) {
        backgroundCollisions.removeIf { s -> true } // is this clearing faster than making a new HashSet object?
        background.layers.forEach {
            val layer = it as TiledMapTileLayer
            testPointBackgroundCollision(x+ collisionShape.x, y+ collisionShape.y, layer)
            testPointBackgroundCollision(x+ collisionShape.x+collisionShape.width, y+ collisionShape.y, layer)
            testPointBackgroundCollision(x+ collisionShape.x, y+ collisionShape.y+collisionShape.height, layer)
            testPointBackgroundCollision(x+ collisionShape.x+collisionShape.width, y+ collisionShape.y+collisionShape.height, layer)
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