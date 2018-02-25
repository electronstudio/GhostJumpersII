package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.Prefs
import uk.me.fantastic.retro.games.Player

/*
 * The player character sprite
 * Controlled by input, affected by gravity etc
 * */
class PimpGuy(
        val player: Player,
        val pimpGame: PimpGame,
        spriteSheetOffsetX: Int, spriteSheetOffsetY: Int
) :
        RetroSprite(pimpGame.textures[spriteSheetOffsetY][spriteSheetOffsetX]) {

    private val GRAVITY = 200f
    private val background: TiledMap = pimpGame.background
    private val textures = pimpGame.textures
    private val input = player.input!! // if player has no input device we are fucked so just crash
    // TODO work out why input is nullable and if it can ever really be null

    private val defaultAnim = Animation(0.1f, pimpGame.textures[spriteSheetOffsetY][spriteSheetOffsetX])
    private val runningAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 1], textures[spriteSheetOffsetY][spriteSheetOffsetX + 2])
    private val climbAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 3])
    private val dieAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 5])
    private val jumpAnimation = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 1])

    private var stunCounter = 0f
    private var fallTimer = 100f

    override val spriteCollisionShape = Rectangle(4f, 4f, 8f, 8f)
    val collisionShapeFeet = Rectangle(4f, -2f, 8f, 4f)

    fun isStunned() = stunCounter > 0f

    /* called every frame */
    override fun update() {
        doAnimation()
        animation = defaultAnim

        if (isStunned()) {
            doWeAreStunned()
        } else {
            doWeAreNotStunned()
        }

        doSimplePhysics()
        checkOutOfBounds()
        checkPushes()
    }

    private fun checkPushes() {
        for (c in backgroundCollisions) {
            if (c.equals("xPush")) {
                xVel = -50f
            }
        }
    }

    private fun checkOutOfBounds() {
        if (x < 0f) x = 0f
        if (x > 320f - 8f) x = 320f - 8f
        if (y < 16f) y = 16f
    }

    private fun doWeAreStunned() {
        animation = dieAnim
        xVel = 0f
        doFalling()
        stunCounter += yVel * Gdx.graphics.deltaTime
    }

    private fun doFalling() {
        yVel -= GRAVITY * Gdx.graphics.deltaTime
        fallTimer += Gdx.graphics.deltaTime
    }

    private fun weAreOn(s: String) = backgroundCollisions.contains(s)

    private fun doWeAreNotStunned() {
        checkBackgroundColisions()
        checkEnemyColisions()
        checkExitColisions()
    }

    private fun checkBackgroundColisions() {
        collisionTest(collisionShapeFeet, background)
        if (weAreOn("ladder")) {
            xVel = 0f
            yVel = 0f
            doClimbingDown()
            animation = climbAnim
        }
        if (weAreOn("platform")) {
            yVel = 0f
            doRunning()
            doJumping()
            fallTimer = 0f
        }
        if (weAreOn("ladder") || weAreOn("platform")) {
            doClimbingUp()
        }
        if (!weAreOn("ladder") && !weAreOn("platform")) {
            if (fallTimer < 0.1f) doJumping()
            doFalling()
            animation = jumpAnimation
        }
    }

    private fun checkEnemyColisions() {
        if (collisionTest(pimpGame.enemies)) {
            stunCounter = 64f
            playSound(pimpGame.stunSound)
        }
    }

    private fun checkExitColisions() {
        if (collisionTestRect(pimpGame.exits)) {
            playSound(pimpGame.bonusSound)
            pimpGame.levelComplete(this)
        }
    }

    private fun doRunning() {
        xVel = 0f
        if (input.leftStick.x < -0.3f || input.rightStick.x < -0.3f) {
            xVel = -50f
            animation = runningAnim
            flip = (xVel < 0f)
        }
        if (input.leftStick.x > 0.3f || input.rightStick.x > 0.3f) {
            xVel = 50f
            animation = runningAnim
            flip = (xVel < 0f)
        }
    }

    private fun doJumping() {
        if (input.fire) {
            yVel = 100f
            animation = jumpAnimation
            playSound(pimpGame.jumpSound)
            fallTimer = 100f
        }
    }

    private fun playSound(sound: Sound) {
        sound.stop()
        sound.play(Prefs.NumPref.FX_VOLUME.asVolume(), (player.id.toFloat() * 0.3f + 0.5f), 0f)
    }

    private fun doClimbingUp() {
        if (input.leftStick.y < -0.3f || input.rightStick.y < -0.3f) {
            y = y + 60f * Gdx.graphics.deltaTime
            animation = climbAnim
        }
    }

    private fun doClimbingDown() {
        if (input.leftStick.y > 0.3f || input.rightStick.y > 0.3f) {
            y = y - 60f * Gdx.graphics.deltaTime
            animation = climbAnim
        }
    }
}