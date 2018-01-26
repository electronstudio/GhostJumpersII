package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.utils.Array
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.input.StatefulController
import java.util.ArrayList

class PimpGuy(val player: Player, val background: TiledMap) :
        RSprite(Texture("PimpGame/pimpguy1.png")) {

    val runFrames = arrayOf(
            Texture("PimpGame/pimpguy2.png"),
            Texture("PimpGame/pimpguy3.png"))
    val runningAnim = Animation<Texture>(0.1f, *runFrames)

    val climbFrames = arrayOf(
            Texture("PimpGame/pimpguy4.png"),
            Texture("PimpGame/pimpguy5.png"))
    val climbAnim = Animation(0.1f, *climbFrames)

    val dieAnim = Animation(0.1f, Texture("PimpGame/pimpguy6.png"))

    var jumpTimer = 0f
    var deathTimer = 0f

    init {
        x = 30f
        y = 30f

    }

    override fun update(delta: Float) {
        super.update(delta)
        animation=defaultAnim

        if(deathTimer>0f){
            deathTimer-=delta
            animation=dieAnim
            return
        }

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

        if(collisionTest(PimpGame.ghosts)){
            deathTimer=3f
        }

        flip = (xVel<0f)
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
                animation=runningAnim
            }
        }
        player.input?.rightStick?.let {
            if (it.x < -0.3f) {
                xVel= -50f
                animation=runningAnim
            }
        }
        player.input?.leftStick?.let {
            if (it.x > 0.3f) {
                xVel= 50f
                animation=runningAnim
            }
        }
        player.input?.rightStick?.let {
            if (it.x > 0.3f) {
                xVel= 50f
                animation=runningAnim
            }
        }
    }

    private fun doClimbing() {

        player.input?.leftStick?.let {
            if (it.y < -0.3f) {
                yVel= 50f
                animation=climbAnim
            }
        }
        player.input?.rightStick?.let {
            if (it.y < -0.3f) {
                yVel= 50f
                animation=climbAnim
            }
        }
        player.input?.leftStick?.let {
            if (it.y > 0.3f) {
                yVel= -50f
                animation=climbAnim
            }
        }
        player.input?.rightStick?.let {
            if (it.y > 0.3f) {
                yVel= -50f
                animation=climbAnim
            }
        }
    }
}