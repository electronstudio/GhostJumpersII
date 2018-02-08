package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.games.Player

class PimpGuy(val player: Player, val background: TiledMap) :
        RetroSprite(Texture("mods/PimpGame/pimpguy1.png")) {

    val runFrames = arrayOf(
            Texture("mods/PimpGame/pimpguy2.png"),
            Texture("mods/PimpGame/pimpguy3.png"))
    val runningAnim = Animation<Texture>(0.1f, *runFrames)

    val climbFrames = arrayOf(
            Texture("mods/PimpGame/pimpguy4.png"),
            Texture("mods/PimpGame/pimpguy5.png"))
    val climbAnim = Animation(0.1f, *climbFrames)

    val dieAnim = Animation(0.1f, Texture("mods/PimpGame/pimpguy6.png"))

    var jumpTimer = 0f
    var deathTimer = 0f

    override var collisionShape = Rectangle(0f, 0f, texture.width.toFloat(), 2f)

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
        } else if (jumpTimer<0f){
           // yVel=-1000f*delta //falling
            yVel-=200f*delta
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
        if(player.input?.fire == true && backgroundCollisions.contains("platform")){
            //yVel=100f
            jumpTimer=0.5f
          //  y=y+26f
        }
        if(jumpTimer>0.30f){
            yVel=140f //* Gdx.graphics.deltaTime
            println("jump frame")
        }else if(jumpTimer>0f){
            yVel=0f
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