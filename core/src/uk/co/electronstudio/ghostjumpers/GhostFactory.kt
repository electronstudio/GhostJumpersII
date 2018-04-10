package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import uk.me.fantastic.retro.Prefs

/*
 * A spawner for Ghosts. Spits them out at random intervals
 * */
class GhostFactory(val x: Float, val y: Float, val speed: Float, val mintime: Float,
                   val maxTime: Float, val pimpGame: PimpGame, val textureRegion: TextureRegion) {

    var enemyTimer = 0f

    private fun spawn(): Ghost = Ghost(pimpGame.background, x, y, speed, pimpGame, textureRegion)

    fun update(delta: Float) {
        enemyTimer -= delta
        if (enemyTimer < 0f) {
            val g = spawn()
            pimpGame.allSprites.add(g)
            pimpGame.enemies.add(g)
            enemyTimer = MathUtils.random(mintime, maxTime)
            playSound(pimpGame.spawnSound)
        }
    }

    private fun playSound(sound: Sound) {
        sound.stop()
        sound.play(Prefs.NumPref.FX_VOLUME.asVolume())
    }
}