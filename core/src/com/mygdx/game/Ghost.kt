package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

class Ghost(val background: TiledMap) : RetroSprite(Texture("mods/PimpGame/pimpenemy.png")) {

    override var collisionShape = Rectangle(0f, 0f, 2f, 2f)

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
            if(y<50){
                dead=true
            }
        }

        if(x>320-16f){
            setScale(-1f,1f)
            x=320-16f
            xVel=-50f
        }
    }


}