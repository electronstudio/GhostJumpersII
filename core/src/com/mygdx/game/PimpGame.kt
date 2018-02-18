package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.screens.GameSession


class PimpGame(session: GameSession) :
        BasicRetroGame(session, pickLevel(session),
                320f, 240f, font, font) {

    val mapRenderer = OrthogonalTiledMapRenderer(background, 1f)

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
                                            speed = obj.properties["speed"] as Float,
                                            mintime = obj.properties["minTime"] as Float,
                                            maxTime = obj.properties["maxTime"] as Float,
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

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
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
    }

    fun levelComplete(winner: PimpGuy) {
        session.level++
        session.nextGame = PimpGame(session)
        gameover()
    }
}

fun pickLevel(session: GameSession): String {
    return when (session.level) {
      //  0 -> "mods/PimpGame/level1.tmx"
        else -> "mods/PimpGame/level3.tmx"
    }
}

