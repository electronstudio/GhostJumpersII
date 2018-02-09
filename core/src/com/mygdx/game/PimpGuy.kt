package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.games.Player

class PimpGuy(val player: Player, val pimpGame: PimpGame, val spriteSheetOffsetX:Int=26, val spriteSheetOffsetY:Int=5) :
        RetroSprite(pimpGame.textures[spriteSheetOffsetY][spriteSheetOffsetX]) {

    val background: TiledMap = pimpGame.background

    val textures = pimpGame.textures


    val runFrames = arrayOf(
            textures[spriteSheetOffsetY][spriteSheetOffsetX+1],textures[spriteSheetOffsetY][spriteSheetOffsetX+2])
    val runningAnim = Animation<TextureRegion>(0.1f, *runFrames)

    val climbFrames = arrayOf(
            textures[spriteSheetOffsetY][spriteSheetOffsetX+3],textures[spriteSheetOffsetY][spriteSheetOffsetX+4])
    val climbAnim = Animation(0.1f, *climbFrames)

    val dieAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX+5])

    var jumpTimer = 0f
    var deathTimer = 0f

    override var collisionShape = Rectangle(4f, 0f, 8f, 2f)

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

        if(collisionTest(pimpGame.ghosts)){
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