package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Rectangle
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.games.RetroGame
import uk.me.fantastic.retro.screens.GameSession
import uk.me.fantastic.retro.unigame.Background

class PimpGame(session: GameSession, val difficulty: Int, val level: Int) :
        RetroGame(session,
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

    val allSprites = ArrayList<RetroSprite>()

    val timeLimit: Float = difficulty.toFloat() * 10.0f + 50f
    var timer = 0f


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
                                            speed = obj.properties["speed"] as Float * (difficulty.toFloat() / 2f),
                                            mintime = obj.properties["minTime"] as Float / (difficulty.toFloat() / 2f),
                                            maxTime = obj.properties["maxTime"] as Float / (difficulty.toFloat() / 2f),
                                            pimpGame = this,
                                            spriteSheetOffsetX = obj.properties["spriteSheetOffsetX"] as Int,
                                            spriteSheetOffsetY = obj.properties["spriteSheetOffsetY"] as Int
                                    )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun doLogic(delta: Float) {
        timer += delta

        allSprites.forEach {
            it.update()
        }
        allSprites.removeIf { it.dead }

        spawners.forEach { it.update(delta) }

        checkForNewPlayerJoins()

        if (timeleft() <= 0) {
            session.nextGame = null
            gameover()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            println(App.Companion.testSandbox())
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            levelComplete(null)
        }
    }


    val colors = Animation<Color>(0.1f, Color.BLACK, Color.RED
    )

    override fun doDrawing(batch: Batch) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        batch.draw(bgTexture, 0f, 0f)
        batch.end()

        batch.begin()
        drawSprites(batch)
        batch.end()

        batch.begin()

        if (timeleft() < 21) {
            font.color = colors.getKeyFrame(timer, true)
        } else {
            font.color = Color.WHITE
        }

        font.draw(batch, "${timeleft()}", 150f, 240f)
        font.color = Color.WHITE
        font.draw(batch, "${(difficulty - 1) * maps.size + level + 1}", 0f, 240f)
        batch.end()
    }


    fun drawSprites(batch: Batch) {

        allSprites.forEach {
            it.draw(batch)
        }
    }

    private fun timeleft(): Int {
        return (timeLimit - timer).toInt()
    }

    private fun checkForNewPlayerJoins() {
        for (i in noOfPlayersInGameAlready until players.size) { // loop enters only when there is a new player joined
            // different players get different texturegions within the spritesheet
            // these are the offsets in the sheet

            val xy: Pair<Int, Int> = when (i) {
                0 -> Pair(26, 1)
                1 -> Pair(32, 1)
                2 -> Pair(38, 1)
                3 -> Pair(26, 2)
                4 -> Pair(32, 2)
                5 -> Pair(38, 2)
                else -> Pair(26, 1)
            }

            val pimp = PimpGuy(players[i], this, xy.first, xy.second)
            pimp.x = entry.x
            pimp.y = entry.y

            allSprites.add(pimp)
            noOfPlayersInGameAlready++
        }
    }

    companion object {
        private val font = BitmapFont(Gdx.files.internal("mods/PimpGame/c64_low3_black.fnt")) // for drawing text
        val maps = listOf<String>(
                "mods/PimpGame/level1.tmx",
                "mods/PimpGame/level2.tmx"
                // "mods/PimpGame/level3.tmx"
        )
    }

    fun levelComplete(winner: PimpGuy?) {
        if (level == maps.lastIndex) {
            session.nextGame = PimpGame(session, difficulty + 1, 0)
        } else {
            session.nextGame = PimpGame(session, difficulty, level + 1)
        }
        gameover()
    }

    // These methods must be implemented but don't have to do anything
    override fun show() {}

    override fun hide() {}

    override fun dispose() {
        spriteSheet.texture.dispose()
    }
}
