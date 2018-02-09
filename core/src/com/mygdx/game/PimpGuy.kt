package com.mygdx.game

import com.badlogic.gdx.Gdx
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
    var stunTimer = 0f

    override var spriteCollisionShape = Rectangle(4f, 4f, 8f, 8f)

    var collisionShapeFeet = Rectangle(4f, -2f, 8f, 4f)

    init {
        x = 30f
        y = 30f

    }

    fun stunned() = stunTimer >0f

    override fun update(delta: Float) {
        super.update(delta)
        animation=defaultAnim

        if(stunned()){
            doWeAreStunned(delta)
        }else{
            doWeAreNotStunned()
        }

        flip = (xVel<0f)
        x+=xVel*delta
        y+=yVel*delta
        checkOutOfBounds()
    }

    private fun checkOutOfBounds() {
        if(x<0f) x=0f
        if(x>320f-8f) x=320f-8f
        if(y<0f) y=0f
    }

    private fun doWeAreStunned(delta: Float) {
        stunTimer -= delta
        animation = dieAnim
        xVel = 0f
        gravityHappens()
    }

    private fun weAreOn(s:String) = backgroundCollisions.contains(s)
    private fun weAreJumping() = jumpTimer>0f

    private fun doWeAreNotStunned() {
        collisionTest(collisionShapeFeet, background)

        if (weAreOn("ladder")) {
            doRunning()
            doClimbingUp()
            doClimbingDown()
        } else if (weAreOn("platform")) {
            doRunning()
            doClimbingUp()
            doJumping()
        }else {
            gravityHappens()
        }


        jumpTimer-=Gdx.graphics.deltaTime

        checkGhostColisions()

    }

    private fun checkGhostColisions() {
        if(collisionTest(pimpGame.ghosts)){
            stunTimer =1f
        }
    }

    private fun gravityHappens() {
        yVel-=200f*Gdx.graphics.deltaTime
    }


    private fun doJumping() {

        if(jumpTimer>0.30f){
            yVel=140f //* Gdx.graphics.deltaTime
      //      println("jump frame")
        }else if(jumpTimer>0f){
            yVel=0f
        }else   if(player.input?.fire == true && backgroundCollisions.contains("platform")){
            //yVel=100f
            jumpTimer=0.4f
            //  y=y+26f
        }
    }


    private fun doRunning() {
        xVel=0f
        yVel=0f
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

    private fun doClimbingUp() {

        player.input?.leftStick?.let {
            if (it.y < -0.3f) {
                yVel = 100f
                animation = climbAnim
            }
        }
        player.input?.rightStick?.let {
            if (it.y < -0.3f) {
                yVel = 100f
                animation = climbAnim
            }
        }
    }
    private fun doClimbingDown(){
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