package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Rectangle
import uk.co.electronstudio.retrowar.Player
import uk.co.electronstudio.retrowar.Prefs


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

    var completionTime: Int = 0
    private val GRAVITY = 200f
    private val background: TiledMap = pimpGame.background
    private val textures = pimpGame.textures
    private val input = player.input

    private val defaultAnim = Animation(0.1f, pimpGame.textures[spriteSheetOffsetY][spriteSheetOffsetX])
    private val runningAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 1], textures[spriteSheetOffsetY][spriteSheetOffsetX + 2])
    private val climbAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 3])
    private val dieAnim = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 5])
    private val jumpAnimation = Animation(0.1f, textures[spriteSheetOffsetY][spriteSheetOffsetX + 1])

    private var stunCounter = 0f
    private var fallTimer = 0f
    private var jumpTimer = 0f

    override val collisionRect = Rectangle(4f, 4f, 8f, 8f)
    val collisionRectFeet = Rectangle(4f, -2f, 8f, 4f)
    val collisionRectShadow = Rectangle(4f, -24f, 8f, 24f)

    fun isStunned() = stunCounter > 0f

    var label = player.name
    var labelTimer = 5f
    var coins = 0
    var ghosts = 0

    val spritesJumpedOver = ArrayList<RetroSprite>()

    /* called every frame */
    override fun update(delta: Float) {
        jumpTimer += delta
        labelTimer -= delta
        doAnimation(delta)
        animation = defaultAnim

        if (isStunned()) {
            doWeAreStunned(delta)
        } else {
            doWeAreNotStunned(delta)
        }

        doSimplePhysics(delta)
        checkOutOfBounds()
        checkPushes()
    }

    override fun draw(batch: Batch?) {
        super.draw(batch)
        if(labelTimer>0f) {
            pimpGame.font.color = player.color2
            val layout = GlyphLayout(pimpGame.font, label)
            pimpGame.font.draw(batch, layout, x-layout.width/2+8f, y+24f)
        }
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

    private fun doWeAreStunned(delta: Float) {
        animation = dieAnim
        xVel = 0f
        doFalling(delta)
        stunCounter += yVel * delta
    }

    private fun doFalling(delta: Float) {
        yVel -= GRAVITY * delta
        fallTimer += delta
    }

    private fun weAreOn(s: String) = backgroundCollisions.contains(s)

    private fun doWeAreNotStunned(delta: Float) {
        checkBackgroundColisions(delta)
        checkEnemyColisions()
        checkShadowColisions()
        checkExitColisions()
        checkScreenEdgeColisions()
    }

    private fun checkScreenEdgeColisions() {
        if(y<pimpGame.yScroll){
           //stunCounter = 10f
            playSound(pimpGame.stunSound)
            yVel = 100f
        }
    }

    private fun checkBackgroundColisions(delta: Float) {
        collisionTest(collisionRectFeet, background)
        if (weAreOn("ladder") && jumpTimer > 0.3f) {
            xVel = 0f
            yVel = 0f
            doClimbingDown(delta)
            animation = climbAnim
            doLadderJump()
        }
        if (weAreOn("platform") && jumpTimer > 0.3f) {
            yVel = 0f
            doRunning()
            doJumping()
            fallTimer = 0f
        }
        if (weAreOn("ladder") || weAreOn("platform")) {
            doClimbingUp(delta)
        }
        if (!weAreOn("ladder") && !weAreOn("platform")) {
            if (fallTimer < 0.1f) doJumping()
            doFalling(delta)
            animation = jumpAnimation
        }
    }

    private fun checkEnemyColisions() {
        val colliding = getCollisions(pimpGame.allSprites, collisionRect)
        colliding.forEach{
            when(it){
                is Ghost, is Goblin -> {
                    stunCounter = 64f
                    playSound(pimpGame.stunSound)
                }
                is Coin -> {
                    it.dead = true
                    coins++
                    label = "${coins+ghosts}"
                    labelTimer=2f
                    playSound(pimpGame.collectSound)
                }
            }
        }
    }

    private fun checkShadowColisions() {
        if(fallTimer>0f) {
            val colliding = getCollisions(pimpGame.allSprites, collisionRectShadow)
            colliding.forEach {
                when (it) {
                    is Ghost, is Goblin -> {
                        if (!spritesJumpedOver.contains(it)) {
                            ghosts++
                            label = "${coins+ghosts}"
                            labelTimer = 2f
                            playSound(pimpGame.bonusSound)
                            spritesJumpedOver.add(it)
                        }
                    }
                    is Coin -> {
                    }
                }
            }
        }
    }

    private fun checkExitColisions() {
        if (collisionTestRect(pimpGame.exits)) {
            playSound(pimpGame.bonusSound)
            dead=true
            if(pimpGame.winner==null) {
                pimpGame.winner = this.player
                completionTime=pimpGame.timeleft()
            }
        }
    }

    private fun doRunning() {
        xVel = 0f
        if (input.leftStick.x < -0.3f) {
            xVel = -50f
            animation = runningAnim
            flip = (xVel < 0f)
        }
        if (input.leftStick.x > 0.3f) {
            xVel = 50f
            animation = runningAnim
            flip = (xVel < 0f)
        }
    }

    private fun doJumping() {
        if (input.fire && jumpTimer > 0.5f) {
            jump()
        }
    }

    private fun jump() {
        yVel = 100f
        animation = jumpAnimation
        playSound(pimpGame.jumpSound)
        fallTimer = 100f
        jumpTimer = 0f
        flip = (xVel < 0f)
    }

    private fun doLadderJump(){
        if (input.fire && input.leftStick.x < -0.3f) {
            xVel = -50f
            jump()
        }else  if (input.fire && input.leftStick.x > 0.3f) {
            xVel = 50f
            jump()
        }
    }

    private fun playSound(sound: Sound) {
        sound.stop()
        sound.play(Prefs.NumPref.FX_VOLUME.asVolume(), (pimpGame.session.players.indexOf(player) * 0.3f + 0.5f), 0f)
    }

    private fun doClimbingUp(delta: Float) {
        if (input.leftStick.y < -0.3f) {
            y = y + 60f * delta
            animation = climbAnim
        }
    }

    private fun doClimbingDown(delta: Float) {
        if (input.leftStick.y > 0.3f) {
            y = y - 60f * delta
            animation = climbAnim
        }
    }
}
