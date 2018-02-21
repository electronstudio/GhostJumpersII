package com.mygdx.game

import com.badlogic.gdx.Audio
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.screens.GameSession
import uk.me.fantastic.retro.unigame.Background


class PimpGame(session: GameSession, val difficulty:Int, var timeLimit:Float, val level:Int) :
        BasicRetroGame(session,
                320f, 240f, font, font) {

    val background = TmxMapLoader().load(maps[level])!!

    val bgTexture = Background.renderTileMapToTexture(background)

    //val mapRenderer = OrthogonalTiledMapRenderer(background, 1f)

    val spriteSheet = TextureRegion(Texture("mods/PimpGame/simples_pimplest.png"))

    val textures = spriteSheet.split(16, 16)
    val ghosts = ArrayList<RetroSprite>()
    val exits = ArrayList<Rectangle>()
    val entry = Rectangle()

    val spawners = ArrayList<GhostFactory>()

    var noOfPlayersInGameAlready = 0



    init {

        for (layer in background.layers) {
            //  println("layer $layer")
            for (obj in layer.objects) {
                val type = obj.properties["type"]
                if (type != null && obj is RectangleMapObject) {
                    when (type) {
                        "exit" -> {
                            exits.add(obj.rectangle)
                        }
                        "entry" -> {
                            entry.x = obj.rectangle.x
                            entry.y = obj.rectangle.y
                            entry.width = obj.rectangle.width
                            entry.height = obj.rectangle.height
                        }
                        "spawner" -> {
                            spawners.add(
                                    GhostFactory(
                                            x = obj.rectangle.x,
                                            y = obj.rectangle.y,
                                            speed = obj.properties["speed"] as Float * (difficulty.toFloat()/2f),
                                            mintime = obj.properties["minTime"] as Float / (difficulty.toFloat()/2f),
                                            maxTime = obj.properties["maxTime"] as Float / (difficulty.toFloat()/2f),
                                            pimpGame = this,
                                            spriteSheetOffsetX=obj.properties["spriteSheetOffsetX"] as Int,
                                            spriteSheetOffsetY=obj.properties["spriteSheetOffsetY"] as Int
                                    )
                            )
                        }
                    }

                }
            }
        }
    }

    override fun doLogic(delta: Float) {
        super.doLogic(delta)

        spawners.forEach { it.update(delta) }

        checkForNewPlayerJoins()

        if(timeleft()<=0){
            session.nextGame=null
            gameover()
        }



        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            println(App.Companion.testSandbox());
        }
    }


    override fun doDrawing(batch: Batch) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

       // mapRenderer.setView(renderer.fboCam)

        batch.begin()
        batch.draw(bgTexture, 0f, 0f)
        batch.end()

        batch.begin()
        drawSprites(batch)
        batch.end()

        batch.begin()
        font.draw(batch,"${timeleft()}",150f,240f)
        font.draw(batch,"${(difficulty-1)*maps.size + level + 1}",0f,240f)
        batch.end()
    }

    private fun timeleft(): Int{
        return (timeLimit-timer).toInt()
    }


    private fun checkForNewPlayerJoins() {
        for (i in noOfPlayersInGameAlready until players.size) { // loop enters only when there is a new player joined
            var xOffset = 26 // different players get different texturegions within the spritesheet
            var yOffset = 1  // these are the offsets in the sheet
            if (i == 1) {
                xOffset = 32
                yOffset = 1
            } else if (i == 2) {
                xOffset = 26
                yOffset = 5
            } else if (i == 3) {
                xOffset = 26
                yOffset = 6
            }

            val pimp = PimpGuy(players[i], this, xOffset, yOffset)
            pimp.x = entry.x
            pimp.y = entry.y

            allSprites.add(pimp)
            noOfPlayersInGameAlready++
        }
    }

    companion object {
        private val font = BitmapFont(Gdx.files.internal("mods/PimpGame/c64_low3_black.fnt"))   // for drawing text
        val maps = listOf<String>(
               "mods/PimpGame/level1.tmx",
                "mods/PimpGame/level2.tmx"
               // "mods/PimpGame/level3.tmx"
        )
    }

    fun levelComplete(winner: PimpGuy) {
        if(level==maps.lastIndex) {
            session.nextGame = PimpGame(session, difficulty + 1, timeLimit, 0)
        }else{
            session.nextGame = PimpGame(session, difficulty, timeLimit, level+1)
        }
        gameover()
    }
}


