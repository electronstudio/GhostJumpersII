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
import com.badlogic.gdx.utils.Align
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.Prefs
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.games.RetroGame
import uk.me.fantastic.retro.screens.GameSession
import uk.me.fantastic.retro.unigame.Background

/* The God class */
class PimpGame(session: GameSession, val difficulty: Int, val level: Int) :
        RetroGame(session,
                320f, 240f, font, font) {

    val background = TmxMapLoader().load(maps[level])!!

    val bgTexture = Background.renderTileMapToTexture(background)

    val spriteSheet = TextureRegion(Texture("mods/PimpGame/simples_pimplest.png"))
    val jumpSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/jump_jade.wav"))
    val stunSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/fall_jade.wav"))
    val bonusSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/bonus_jade.wav"))
    val spawnSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/hit_jade.wav"))

    val music = Gdx.audio.newMusic(Gdx.files.internal("mods/PimpGame/justin1.wav"))

    val textures = spriteSheet.split(16, 16)
    val enemies = ArrayList<RetroSprite>()
    val exits = ArrayList<Rectangle>()
    val entry = Rectangle()
    val redFlash = Animation<Color>(0.1f, Color.BLACK, Color.RED)
    val multiFlash = Animation<String>(1f / 30f, "RED", "PURPLE", "BLUE", "CYAN", "GREEN", "YELLOW")

    val spawners = ArrayList<GhostFactory>()

    var noOfPlayersInGameAlready = 0

    val allSprites = ArrayList<RetroSprite>()

    val timeLimit: Float = difficulty.toFloat() * 10.0f + 50f
    var timer = 0f

    val scoreDisplay = players.sumBy { it.score }

    var levelFinished = false
    var winner: Player? = null
    var endOfLevelMessage = ""

    init {
        music.isLooping = true
        for (layer in background.layers) {
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
                                            textureRegion =
                                            textures[obj.properties["spriteSheetOffsetY"] as Int]
                                                    [obj.properties["spriteSheetOffsetX"] as Int]

                                    )
                            )
                        }
                        "goblin" -> {
                            val goblin = Goblin(initX = obj.rectangle.x,
                                    initY = obj.rectangle.y,
                                    speed = obj.properties["speed"] as Float * (difficulty.toFloat() / 2f),
                                    pimpGame = this,
                                    animation = Animation(0.1f, textures[5][27], textures[5][28]),
                                    background = background,
                                    leftWalk = obj.properties["leftWalk"] as Float,
                                    rightWalk = obj.properties["rightWalk"] as Float)
                            enemies.add(goblin)
                            allSprites.add(goblin)
                        }
                    }
                }
            }
        }
    }

    override fun doLogic(deltaTime: Float) {
        timer += deltaTime
        if (levelFinished) {
            if (players.any { it.input?.fire }) {
                gameover()
            }
        } else {
            doGameLogic()
        }
    }

    private fun doGameLogic() {

        allSprites.forEach {
            it.update()
        }

        val deadSprites: List<RetroSprite> = allSprites.filter { it.dead }
        deadSprites.forEach {
            allSprites.remove(it)
        } // JDK 8 / Android 7: allSprites.removeIf { it.dead }

        spawners.forEach { it.update(Gdx.graphics.deltaTime) }

        checkForNewPlayerJoins()

        if (timeleft() <= 0) {
            session.nextGame = null
            levelFinished = true
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            println(App.Companion.testSandbox()) // should crash if sandbox is working
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            levelComplete(null)
        }
    }

    override fun doDrawing(batch: Batch) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (levelFinished) {
            drawScoreTable(batch)
        } else {
            drawGame(batch)
        }
    }

    private fun drawGame(batch: Batch) {
        Prefs.BinPref.BILINEAR.filter(bgTexture)

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
            font.color = redFlash.getKeyFrame(timer, true)
        } else {
            font.color = Color.WHITE
        }

        font.draw(batch, "${timeleft()}", 150f, 240f)
        font.color = Color.WHITE
        font.draw(batch, "L${friendlyLevelNumber()}", 0f, 240f)
        font.draw(batch, "$scoreDisplay PTS", 0f, 240f, 320f, Align.right, false)
        batch.end()
    }

    private fun friendlyLevelNumber() = (difficulty - 1) * maps.size + level + 1

    fun drawSprites(batch: Batch) {

        allSprites.forEach {
            it.draw(batch)
        }
    }

    private fun friendlyCompletionStatus() = if (timeleft() > 0) "[BLUE]COMPLETE[]" else "[RED]TIME OVER[]"

    private fun drawScoreTable(batch: Batch) {

        val s = "\n\n\nLEVEL ${friendlyLevelNumber()}\n\n${endOfLevelMessage}\n" +
                players.joinToString("") {
                    (if (it == winner) "[${multiFlash.getKeyFrame(timer, true)}]" else "") +
                            "\n\n${it.name} ${it.score}[]"
                } +
                "\n\n\nTOTAL SCORE ${players.sumBy { it.score }}"

        batch.begin()
        font.draw(batch, s, 0f, 240f, 320f, Align.center, false)
        batch.end()
    }

    private fun timeleft(): Int {
        return (timeLimit - timer).toInt()
    }

    private fun checkForNewPlayerJoins() {
        for (i in noOfPlayersInGameAlready until players.size) { // loop only when there is a new player(s) joined
            val xy: Pair<Int, Int> = when (i) {
                0 -> Pair(26, 1)// different players get different texturegions within the spritesheet
                1 -> Pair(32, 1)// these are the offsets in the sheet
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
                "mods/PimpGame/level2.tmx",
                "mods/PimpGame/level3.tmx"
        )
    }

    fun levelComplete(winner: PimpGuy?) {
        this.winner = winner?.player
        music.stop()
        if (winner != null) {
            winner.player.score += timeleft() * difficulty
        }
        if (level == maps.lastIndex) {
            session.nextGame = PimpGame(session, difficulty + 1, 0)
        } else {
            session.nextGame = PimpGame(session, difficulty, level + 1)
        }
        levelFinished = true
        endOfLevelMessage = friendlyCompletionStatus()
        //gameover()
    }

    // These methods must be implemented but don't have to do anything
    override fun show() {
        music.play()
    }

    override fun hide() {
        music.stop()
    }

    override fun dispose() {
        spriteSheet.texture.dispose()
        jumpSound.dispose()
        stunSound.dispose()
        bonusSound.dispose()
        spawnSound.dispose()
        music.dispose()
    }
}
