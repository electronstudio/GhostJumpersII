package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import uk.me.fantastic.retro.games.RetroGame
import uk.me.fantastic.retro.screens.GameSession

/**
 * A simple RetroWar game
 */
class PimpGame(session: GameSession) : RetroGame(session, 320f, 240f, font, font) {

    private val sprites = ArrayList<RSprite>()

   // private val background = Texture(Gdx.files.internal("PimpGame/sky4.png"))
    val map = TmxMapLoader().load("PimpGame/level1.tmx")!!
    val mapRenderer = OrthogonalTiledMapRenderer(map, 1f)


    init {                     // Constructor (required)
        // (actually too high for a retro look

    }// width and height of screen in pixels

    override fun doLogic(delta: Float) {  // Called automatically every frame

        for(sprite in sprites){
            sprite.update(delta)
            if(!onPlatform(sprite)){
                sprite.doGravity(delta)
            }
        }
    }

    private fun onPlatform(sprite: RSprite): Boolean {
        val layer = map.layers[1] as TiledMapTileLayer
        val x = ((sprite.x+sprite.width/2)/layer.tileWidth).toInt()
        val y = (sprite.y/layer.tileWidth).toInt()
        val cell = layer.getCell(x,y)
        //println("x $x y $y ${cell.tile.properties.})")
        if(cell != null){
            val platform = cell.tile.properties.get("platform", false, Boolean::class.java)
            println("platform $platform")
            return true
        }else{
            return false
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

    var playersAdded=0

    private fun updatePlayers() {                 // draw one sprite for each Player
        for (i in playersAdded until players.size) {
            val pimp = PimpGuy(Texture(Gdx.files.internal("PimpGame/pimpguy1.png")),players[i])

            sprites.add(pimp)
            playersAdded++
           // println("drawing $i at ${sprite.x}")
        }
    }

    private fun drawSprites(batch: Batch) {                 // draw one sprite for each Player
        for (i in 0 until sprites.size) {
            val sprite = sprites[i]
            sprite.draw(batch)
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
