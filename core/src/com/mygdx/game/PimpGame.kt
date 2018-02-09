package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.screens.GameSession


class PimpGame(session: GameSession) :
        BasicRetroGame(session, "mods/PimpGame/level1.tmx",
                320f, 240f, font, font) {

    val mapRenderer = OrthogonalTiledMapRenderer(background, 1f)

    val spriteSheet = TextureRegion(Texture("mods/PimpGame/simples_pimplest.png"))
    val textures = spriteSheet.split(16, 16)
    val ghosts = java.util.ArrayList<RetroSprite>()

    val spawners = listOf<GhostFactory>(GhostFactory(x = 0f, y = 230f, speed = 50f, mintime = 2f, maxTime = 5f,
            pimpGame = this))

    var noOfPlayersInGameAlready = 0

    override fun doLogic(delta: Float) {
        super.doLogic(delta)

        spawners.forEach { it.update(delta) }

        checkForNewPlayerJoins()

        if(Gdx.input.isKeyPressed(Input.Keys.P)) {
            println(App.Companion.testSandbox());
        }
    }



    override fun doDrawing(batch: Batch) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mapRenderer.setView(renderer.fboCam)

        batch.begin()
        mapRenderer.render()
        batch.end()

        batch.begin()
        drawSprites(batch)
        batch.end()
    }



    private fun checkForNewPlayerJoins() {
        for (i in noOfPlayersInGameAlready until players.size) { // loop enters only when there is a new player joined
            var xOffset=26 // different players get different texturegions within the spritesheet
            var yOffset=1  // these are the offsets in the sheet
            if(i==1) {
                xOffset = 32
                yOffset = 1
            }else if(i==2){
                xOffset = 26
                yOffset = 5
            }else if(i==3){
                xOffset = 26
                yOffset = 6
            }

            val pimp = PimpGuy(players[i], this, xOffset, yOffset)

            allSprites.add(pimp)
            noOfPlayersInGameAlready++
        }
    }

    companion object {
        private val font = BitmapFont(Gdx.files.internal("mods/PimpGame/c64_low3_black.fnt"))   // for drawing text
    }
}

