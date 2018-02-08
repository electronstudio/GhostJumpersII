package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.screens.GameSession


class PimpGame(session: GameSession) :
        BasicRetroGame(session, "mods/PimpGame/level1.tmx",
                320f, 240f, font, font) {


    val mapRenderer = OrthogonalTiledMapRenderer(background, 1f)

    var enemyTimer=0f






    override fun doLogic(delta: Float) {
        super.doLogic(delta)
        enemyTimer-=delta
        if(enemyTimer<0f){
            val g = Ghost(background)
            allSprites.add(g)
            ghosts.add(g)
            enemyTimer=MathUtils.random(10f)
        }
        updatePlayers()
        if(Gdx.input.isKeyPressed(Input.Keys.P)) {
            println(App.Companion.testSandbox());
        }
    }



    private fun play(deltaTime: Float) {

    }



    override fun doDrawing(batch: Batch) {

        //  Gdx.gl.glClearColor(0f, 0f, 0f, 1f)     // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(renderer.fboCam)


        batch.begin()

        mapRenderer.render()
        batch.end()
        batch.begin()


        drawSprites(batch)

        batch.end()
    }

    var playersAdded = 0

    private fun updatePlayers() {                 // draw one sprite for each Player
        for (i in playersAdded until players.size) {
            val pimp = PimpGuy(players[i], background)

            allSprites.add(pimp)
            playersAdded++
        }
    }





    companion object {
        val ghosts = java.util.ArrayList<RetroSprite>()

        private val font = BitmapFont(Gdx.files.internal("mods/PimpGame/c64_low3_black.fnt"))   // for drawing text




    }
}
