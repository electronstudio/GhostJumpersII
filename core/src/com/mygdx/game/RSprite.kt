package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.utils.Vec

open class RSprite(texture: Texture) : Sprite(texture) {
    //val bgCollisionPoints = listOf<Vec>(Vec(0f,0f), Vec(16f, 0f))

    val spriteCollisionShape = Rectangle(0f, 0f, texture.width.toFloat(), texture.height.toFloat())

    val spriteCollisions = ArrayList<String>()
    val backgroundCollisions = HashSet<String>()

    var xVel=0f
    var yVel=0f

    open fun update(delta: Float) {

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

    fun collisionTest(background: TiledMap) {
        backgroundCollisions.removeIf { s -> true } // is this faster than making a new one?
        background.layers.forEach {
            val layer = it as TiledMapTileLayer
            testPointBackgroundCollision(x,y, layer)
            testPointBackgroundCollision(x+width,y, layer)
            testPointBackgroundCollision(x,y+height, layer)
            testPointBackgroundCollision(x+width,y+height, layer)
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