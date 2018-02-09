package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle

class Ghost(val background: TiledMap, val initX:Float=30f, val initY:Float=230f, val speed:Float=50f) : RetroSprite(Texture
("mods/PimpGame/pimpenemy.png")) {

    override var spriteCollisionShape = Rectangle(0f, 0f, 2f, 2f)



    init {
        x = initX
        y = initY
        xVel=speed
    }

    override fun update(delta: Float) {
        collisionTest(spriteCollisionShape, background)
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
            xVel=xVel*-1
            if(y<50){
                dead=true
            }
        }

        if(x>320-16f){
            setScale(-1f,1f)
            x=320-16f
            xVel=xVel*-1
        }
    }


}