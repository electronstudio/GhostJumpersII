package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

class Ghost(val background: TiledMap, val initX: Float = 30f, val initY: Float = 230f, val speed: Float = 50f,
            val pimpGame: PimpGame, val spriteSheetOffsetX: Int = 26, val spriteSheetOffsetY: Int = 7
            ) :
        RetroSprite(pimpGame.textures[spriteSheetOffsetY][spriteSheetOffsetX]) {

    override var spriteCollisionShape = Rectangle(4f, 0f, 8f, 4f)

    init {
        x = initX
        y = initY
        xVel = speed
    }

    override fun update() {
        collisionTest(spriteCollisionShape, background)
        if (backgroundCollisions.contains("platform")) {
            yVel = 0f
        } else {
            yVel = -70f
        }

        x += xVel * Gdx.graphics.deltaTime
        y += yVel * Gdx.graphics.deltaTime

        if (x<0) {
            setScale(1f, 1f)
            x = 0f
            xVel = xVel * -1
            if (y<50) {
                dead = true
            }
        }

        if (x>320 - 16f) {
            setScale(-1f, 1f)
            x = 320 - 16f
            xVel = xVel * -1
        }
    }
}