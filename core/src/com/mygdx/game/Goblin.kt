package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

class Goblin(val background: TiledMap, val initX: Float, val initY: Float, val speed: Float = 50f, leftWalk: Float,
             rightWalk: Float,
             val pimpGame: PimpGame, animation: Animation<TextureRegion>
) :
        RetroSprite(animation) {

    override val spriteCollisionShape = Rectangle(4f, 0f, 8f, 8f)

    val leftLimit = initX - leftWalk
    val rightLimit = initX + rightWalk

    init {
        x = initX
        y = initY
        xVel = speed

    }

    override fun update() {
        super.update()

        if (x < leftLimit) {
            setScale(1f, 1f)
            x = leftLimit
            xVel = xVel * -1
        }
        if (x > rightLimit) {
            setScale(-1f, 1f)
            x = rightLimit
            xVel = xVel * -1
        }
        flip = xVel < 0
        x += xVel * Gdx.graphics.deltaTime
    }
}