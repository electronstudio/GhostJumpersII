package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

/*
 * An enemy sprite that marches left and right in a line, forever
 */
class Goblin(
        val background: TiledMap, val initX: Float, val initY: Float, val speed: Float, leftWalk: Float,
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

    override fun update(delta: Float) {
        doAnimation(delta)

        if (x < leftLimit) {
            unFlipSprite()
            x = leftLimit
            xVel = xVel * -1
        }
        if (x > rightLimit) {
            flipSprite()
            x = rightLimit
            xVel = xVel * -1
        }
        flip = xVel < 0
        x += xVel * delta
    }
}