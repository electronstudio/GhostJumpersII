package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.input
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import sun.audio.AudioPlayer.player
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.input.InputDevice

class PimpGuy(val player: Player, val pimpGame: PimpGame, val spriteSheetOffsetX:Int=26, val spriteSheetOffsetY:Int=5) :
        RetroSprite(pimpGame.textures[spriteSheetOffsetY][spriteSheetOffsetX]) {

    val GRAVITY = 200f
    val background: TiledMap = pimpGame.background
    val textures = pimpGame.textures
    val input: InputDevice


    val runFrames = arrayOf(
            textures[spriteSheetOffsetY][spriteSheetOffsetX+1],textures[spriteSheetOffsetY][spriteSheetOffsetX+2])
    val runningAnim = Animation<TextureRegion>(0.1f, *runFrames)
    val climbFrames = arrayOf(
            textures[spriteSheetOffsetY][spriteSheetOffsetX+3],textures[spriteSheetOffsetY][spriteSheetOffsetX+4])
    val climbAnim = Animation(0.1f, *climbFrames)
    val dieAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX+5])

    var stunTimer = 0f

    override var spriteCollisionShape = Rectangle(4f, 4f, 8f, 8f)
    var collisionShapeFeet = Rectangle(4f, -2f, 8f, 4f)

    init {
        x = 30f
        y = 30f
        input=player.input!! // if player has no input device we are fucked so just crash
                                // TODO work out why input is nullable and if it can ever really be null
    }

    fun stunned() = stunTimer >0f

    override fun update() {
        super.update()
        animation=defaultAnim

        if(stunned()){
            doWeAreStunned()
        }else{
            doWeAreNotStunned()
        }

        flip = (xVel<0f)
        x+=xVel*Gdx.graphics.deltaTime
        y+=yVel*Gdx.graphics.deltaTime
        checkOutOfBounds()
    }

    private fun checkOutOfBounds() {
        if(x<0f) x=0f
        if(x>320f-8f) x=320f-8f
        if(y<0f) y=0f
    }

    private fun doWeAreStunned() {
        stunTimer -= Gdx.graphics.deltaTime
        animation = dieAnim
        xVel = 0f
        gravityHappens()
    }

    private fun weAreOn(s:String) = backgroundCollisions.contains(s)


    private fun doWeAreNotStunned() {
        collisionTest(collisionShapeFeet, background)

        if (weAreOn("ladder")) {
            doRunning()
            doClimbingUp()
            doClimbingDown()
        } else if (weAreOn("platform")) {
            doRunning()
            doClimbingUp()
        }else {
            gravityHappens()
        }


        checkGhostColisions()

    }

    private fun checkGhostColisions() {
        if(collisionTest(pimpGame.ghosts)){
            stunTimer =1f
        }
    }

    private fun gravityHappens() {
        yVel-=GRAVITY*Gdx.graphics.deltaTime
    }


    private fun doRunning() {
        xVel=0f
        yVel=0f
        if (input.leftStick.x < -0.3f || input.rightStick.x < -0.3f) {
                xVel= -50f
                animation=runningAnim
        }
        if (input.leftStick.x > 0.3f || input.rightStick.x > 0.3f) {
                xVel= 50f
                animation=runningAnim
            }
        }


    private fun doClimbingUp() {
            if (input.leftStick.y < -0.3f || input.rightStick.y < -0.3f || input.fire) {
                yVel = 100f
                animation = climbAnim
            }
    }
    private fun doClimbingDown(){
            if (input.leftStick.y > 0.3f || input.rightStick.y > 0.3f) {
                yVel= -50f
                animation=climbAnim
            }
        }
}