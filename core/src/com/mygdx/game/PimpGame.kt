package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.MathUtils
import uk.me.fantastic.retro.games.RetroGame
import uk.me.fantastic.retro.screens.GameSession
import java.util.*
import kotlin.collections.ArrayList


enum class SpriteGroup() {
    PLAYERS, ENEMIES
}

enum class BackgroundType {
    PLATFORM, LADDER
}

class PimpGame(session: GameSession) :
        FunkyRetroGame<SpriteGroup>(
                SpriteGroup::class.java,
                SpriteGroup.values(),
                session, "PimpGame/level1.tmx",
                320f, 240f, font, font) {


    // private val background = Texture(Gdx.files.internal("PimpGame/sky4.png"))

    val mapRenderer = OrthogonalTiledMapRenderer(background, 1f)

    var enemyTimer=0f

    init {


    }


    override fun doLogic(delta: Float) {
        super.doLogic(delta)
        enemyTimer-=delta
        if(enemyTimer<0f){
            allSprites.add(Ghost(Texture("PimpGame/pimpenemy.png"), background))
            enemyTimer=MathUtils.random(10f)
        }
    }



    private fun play(deltaTime: Float) {

    }


    override fun doDrawing(batch: Batch) {            // called automatically every frame

        //  Gdx.gl.glClearColor(0f, 0f, 0f, 1f)     // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        //  spriteRenderer.setupCam(320f,240f)
        mapRenderer.setView(renderer.fboCam)
        //  drawShapes();

        batch.begin()

        mapRenderer.render()
        batch.end()
        batch.begin()
        //   batch.draw(background, 0f, 0f)
        updatePlayers()
        drawSprites(batch)
        // font.draw(batch, message, 0f, 312f, 256f, Align.center, false)
        batch.end()
    }

    var playersAdded = 0

    private fun updatePlayers() {                 // draw one sprite for each Player
        for (i in playersAdded until players.size) {
            val pimp = PimpGuy(Texture(Gdx.files.internal("PimpGame/pimpguy1.png")), players[i], background)

            allSprites.add(pimp)
            playersAdded++
            // println("drawing $i at ${sprite.x}")
        }
    }

    private fun drawSprites(batch: Batch) {                 // draw one sprite for each Player
        //for (i in 0 until sprites.size) {
        //    val sprite = sprites[i]
        //    sprite.draw(batch)
        // }
        allSprites.forEach{
            it.draw(batch)
        }
    }

    // These methods must be implemented but don't have to do anything
    override fun show() {}

    override fun hide() {}
    override fun dispose() {}

    companion object {

        private val font = BitmapFont(Gdx.files.internal("PimpGame/c64_low3_black.fnt"))   // for drawing text
    }
}
