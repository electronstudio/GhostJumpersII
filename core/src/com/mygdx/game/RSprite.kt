package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.utils.Vec

open class RSprite(texture: Texture?) : Sprite(texture) {
    val bgCollisionPoints = listOf<Vec>(Vec(0f,0f), Vec(16f, 0f))

    open fun update(delta: Float) {}

    open fun doGravity(delta: Float){
        y=y-50f*delta
    }

//    open fun onPlatform(): Boolean{
//
//    }

}