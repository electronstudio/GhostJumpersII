package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.input.StatefulController

class PimpGuy(texture: Texture?, val player: Player) : RSprite(texture) {


    init {
        x = 30f
        y = 30f
    }

    override fun update(delta: Float) {
        doRunning(delta)
       // doGravity(delta)
    }

    private fun doRunning(delta: Float) {
        player.input?.leftStick?.let {
            if (it.x < -0.3f) {
                x -= 50f * delta
            }
        }
        player.input?.rightStick?.let {
            if (it.x < -0.3f) {
                x -= 50f * delta
            }
        }
        player.input?.leftStick?.let {
            if (it.x > 0.3f) {
                x += 50f * delta
            }
        }
        player.input?.rightStick?.let {
            if (it.x > 0.3f) {
                x += 50f * delta
            }
        }
    }
}