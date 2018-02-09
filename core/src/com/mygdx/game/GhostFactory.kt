package com.mygdx.game

import com.badlogic.gdx.math.MathUtils

class GhostFactory(val x: Float, val y: Float, val speed: Float, val mintime: Float,
                   val maxTime: Float, val pimpGame: PimpGame) {

    var enemyTimer=0f

    fun spawn(): Ghost = Ghost(pimpGame.background, x, y, speed)

    fun update(delta: Float) {
        enemyTimer-=delta
        if(enemyTimer<0f){
            val g = spawn()
            pimpGame.allSprites.add(g)
            pimpGame.ghosts.add(g)
            enemyTimer= MathUtils.random(mintime, maxTime)
        }
    }

}