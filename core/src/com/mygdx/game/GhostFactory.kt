package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils

class GhostFactory(val x: Float, val y: Float, val speed: Float, val mintime: Float,
                   val maxTime: Float, val pimpGame: PimpGame, val textureRegion: TextureRegion) {

    var enemyTimer = 0f

    val spawnSound = Gdx.audio.newSound(FileHandle("mods/PimpGame/hit_jade.wav"))

    fun spawn(): Ghost = Ghost(pimpGame.background, x, y, speed, pimpGame, textureRegion)

    fun update(delta: Float) {
        enemyTimer -= delta
        if (enemyTimer < 0f) {
            val g = spawn()
            pimpGame.allSprites.add(g)
            pimpGame.enemies.add(g)
            enemyTimer = MathUtils.random(mintime, maxTime)
            spawnSound.play()
        }
    }
}