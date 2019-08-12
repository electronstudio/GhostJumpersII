package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

/*

 */
class Coin(
        x: Float, y: Float, animation: Animation<TextureRegion>
) :
        RetroSprite(animation) {

    override var collisionRect = Rectangle(4f, 0f, 8f, 8f)

    init {
        this.x = x
        this.y = y
    }

    override fun update(delta: Float) {
        doAnimation(delta)
    }


}
