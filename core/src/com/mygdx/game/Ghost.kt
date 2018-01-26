package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.input.StatefulController

class Ghost(val background: TiledMap) : RSprite(Texture("PimpGame/pimpenemy.png")) {

    var jumpTimer = 0f

    init {
        x = 30f
        y = 230f
        xVel=50f
    }

    override fun update(delta: Float) {
        collisionTest(background)
        if (backgroundCollisions.contains("platform")) {
            yVel=0f
        } else{
            yVel=-70f
        }

        x+=xVel*delta
        y+=yVel*delta

        if(x<0){
            setScale(1f,1f)
            x=0f
            xVel=50f
        }

        if(x>320){
            setScale(-1f,1f)
            x=320f
            xVel=-50f
        }
    }


}