package com.mygdx.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.input.StatefulController

class PimpGuy(texture: Texture, val player: Player, val background: TiledMap) : RSprite(texture) {

    var jumpTimer = 0f

    init {
        x = 30f
        y = 30f
    }

    override fun update(delta: Float) {
        collisionTest(background)
        if (backgroundCollisions.contains("platform") || backgroundCollisions.contains("ladder")) {
            doRunning()
            yVel=0f
        } else{
            yVel=-50f
        }
        if (backgroundCollisions.contains("ladder")) {
            doClimbing()
        }
        doJumping()
        jumpTimer-=delta
        x+=xVel*delta
        y+=yVel*delta
    }

    private fun doJumping() {
        if(player.input?.fire == true){
            jumpTimer=0.5f
        }
        if(jumpTimer>0f){
            yVel=50f
        }
    }


    private fun doRunning() {
        xVel=0f
        player.input?.leftStick?.let {
            if (it.x < -0.3f) {
                xVel= -50f
            }
        }
        player.input?.rightStick?.let {
            if (it.x < -0.3f) {
                xVel= -50f
            }
        }
        player.input?.leftStick?.let {
            if (it.x > 0.3f) {
                xVel= 50f
            }
        }
        player.input?.rightStick?.let {
            if (it.x > 0.3f) {
                xVel= 50f
            }
        }
    }

    private fun doClimbing() {

        player.input?.leftStick?.let {
            if (it.y < -0.3f) {
                yVel= 50f
            }
        }
        player.input?.rightStick?.let {
            if (it.y < -0.3f) {
                yVel= 50f
            }
        }
        player.input?.leftStick?.let {
            if (it.y > 0.3f) {
                yVel= -50f
            }
        }
        player.input?.rightStick?.let {
            if (it.y > 0.3f) {
                yVel= -50f
            }
        }
    }
}