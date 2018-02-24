package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.Prefs
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.input.InputDevice

class PimpGuy(val player: Player, val pimpGame: PimpGame, val spriteSheetOffsetX: Int = 26, val spriteSheetOffsetY: Int = 5) :
        RetroSprite(pimpGame.textures[spriteSheetOffsetY][spriteSheetOffsetX]) {

    val GRAVITY = 200f
    val background: TiledMap = pimpGame.background
    val textures = pimpGame.textures
    val input: InputDevice

    val jumpFrames = arrayOf(textures[spriteSheetOffsetY][spriteSheetOffsetX + 1])
    val runFrames = arrayOf(
            textures[spriteSheetOffsetY][spriteSheetOffsetX + 1], textures[spriteSheetOffsetY][spriteSheetOffsetX + 2])
    val runningAnim = Animation<TextureRegion>(0.1f, *runFrames)
    val climbFrames = arrayOf(
            //textures[spriteSheetOffsetY][spriteSheetOffsetX+2],
            textures[spriteSheetOffsetY][spriteSheetOffsetX + 3])
    val climbAnim = Animation(0.1f, *climbFrames)
    val dieAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 5])
    val jumpAnimation = Animation<TextureRegion>(0.1f, *jumpFrames)

    val jumpSound = Gdx.audio.newSound(FileHandle("mods/PimpGame/jump_jade.wav"))
    val stunSound = Gdx.audio.newSound(FileHandle("mods/PimpGame/fall_jade.wav"))
    val bonusSound = Gdx.audio.newSound(FileHandle("mods/PimpGame/bonus_jade.wav"))

    var stunCounter = 0f

    override var spriteCollisionShape = Rectangle(4f, 4f, 8f, 8f)
    var collisionShapeFeet = Rectangle(4f, -2f, 8f, 4f)

    init {
        x = 30f
        y = 30f
        input = player.input!! // if player has no input device we are fucked so just crash
        // TODO work out why input is nullable and if it can ever really be null
    }

    fun stunned() = stunCounter > 0f

    override fun update() {
        super.update()
        animation = defaultAnim

        if (stunned()) {
            doWeAreStunned()
        } else {
            doWeAreNotStunned()
        }

        flip = (xVel < 0f)
        x += xVel * Gdx.graphics.deltaTime
        y += yVel * Gdx.graphics.deltaTime
        checkOutOfBounds()
    }

    private fun checkOutOfBounds() {
        if (x < 0f) x = 0f
        if (x > 320f - 8f) x = 320f - 8f
        if (y < 16f) y = 16f
    }

    private fun doWeAreStunned() {

        animation = dieAnim
        xVel = 0f
        yVel -= GRAVITY * Gdx.graphics.deltaTime
        stunCounter += yVel * Gdx.graphics.deltaTime
        println("stunned $stunCounter")
    }

    private fun weAreOn(s: String) = backgroundCollisions.contains(s)

    private fun doWeAreNotStunned() {
        collisionTest(collisionShapeFeet, background)

        if (weAreOn("ladder")) {
            xVel = 0f
            yVel = 0f

            doClimbingDown()
            animation = climbAnim
        }
        if (weAreOn("platform")) {
            //  xVel=0f
            // yVel=0f

            yVel = 0f
            doRunning()

            doJumping()
        }
        if (weAreOn("ladder") || weAreOn("platform")) {
            doClimbingUp()
        }
        if (!weAreOn("ladder") && !weAreOn("platform")) {
            yVel -= GRAVITY * Gdx.graphics.deltaTime
            animation = jumpAnimation
        }

        checkGhostColisions()
        checkExitColisions()
    }

    private fun checkGhostColisions() {
        if (collisionTest(pimpGame.ghosts)) {
            stunCounter = 64f
            playSound(stunSound)
        }
    }

    private fun checkExitColisions() {
        if (collisionTestRect(pimpGame.exits)) {
            playSound(bonusSound)
            pimpGame.levelComplete(this)
        }
    }

    private fun doRunning() {
        xVel = 0f
        if (input.leftStick.x < -0.3f || input.rightStick.x < -0.3f) {
            xVel = -50f
            animation = runningAnim
        }
        if (input.leftStick.x > 0.3f || input.rightStick.x > 0.3f) {
            xVel = 50f
            animation = runningAnim
        }
    }

    private fun doJumping() {
        if (input.fire) {
            yVel = 100f
            animation = jumpAnimation
            playSound(jumpSound)
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