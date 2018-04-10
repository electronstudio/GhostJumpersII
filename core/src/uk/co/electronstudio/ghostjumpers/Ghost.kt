package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

/*
 * Enemy sprite that moves left or right until it hits screen edge, then turns around,
 * and falls with gravity when it runs off a platform
 */
class Ghost(
        val background: TiledMap, initX: Float, initY: Float, speed: Float,
        val pimpGame: PimpGame, textureRegion: TextureRegion
            ) :
        RetroSprite(textureRegion) {

    override var spriteCollisionShape = Rectangle(4f, 0f, 8f, 8f)

    init {
        x = initX
        y = initY
        xVel = speed
        if (speed < 0) flipSprite()
    }

    override fun update() {
        collisionTest(spriteCollisionShape, background)
        checkBackgroundColisions()
        doSimplePhysics()
        checkBounds()
    }

    private fun checkBounds() {
        if (x<0) {
            unFlipSprite()
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

    private fun checkBackgroundColisions() {
        if (backgroundCollisions.contains("platform")) {
            yVel = 0f
        } else {
            yVel = -70f
        }
    }
}