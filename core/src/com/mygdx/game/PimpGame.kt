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
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Align
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.Prefs
import uk.me.fantastic.retro.games.Player
import uk.me.fantastic.retro.games.SimpleGame
import uk.me.fantastic.retro.isMobile
import uk.me.fantastic.retro.screens.GameSession
import uk.me.fantastic.retro.unigame.Background

/* The God class */
class PimpGame(session: GameSession, val difficulty: Int, val level: Int) :
        SimpleGame(session,
                320f, 240f, font, font) {

    companion object {
        private val font = BitmapFont(Gdx.files.internal("mods/PimpGame/c64_low3_black.fnt")) // for drawing text
        val maps = listOf<String>(
                "mods/PimpGame/level1.tmx",
                "mods/PimpGame/level2.tmx",
                "mods/PimpGame/level3.tmx"
        )
    }

    val background = TmxMapLoader().load(maps[level])!!

    val bgTexture = Background.renderTileMapToTexture(background)

    val spriteSheet = TextureRegion(Texture("mods/PimpGame/simples_pimplest.png"))
    val jumpSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/jump_jade.wav"))
    val stunSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/fall_jade.wav"))
    val bonusSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/bonus_jade.wav"))
    val spawnSound = Gdx.audio.newSound(Gdx.files.internal("mods/PimpGame/hit_jade.wav"))
    val controlsImageLayer = Texture("mods/PimpGame/controls.png")
    val music = CrossPlatformMusic.create(desktopFile = "mods/PimpGame/justin1.ogg", mobileFile = "mods/PimpGame/JustinLong.ogg")




    val textures = spriteSheet.split(16, 16)
    val redFlash = Animation<Color>(0.1f, Color.BLACK, Color.RED)
    val multiFlash = Animation<String>(1f / 30f, "RED", "PURPLE", "BLUE", "CYAN", "GREEN", "YELLOW")
    val timeLimit: Float = difficulty.toFloat() * 10.0f + 50f

    val spawners = ArrayList<GhostFactory>()
    val allSprites = ArrayList<RetroSprite>()
    val enemies = ArrayList<RetroSprite>()
    val exits = ArrayList<Rectangle>()
    val entry = Rectangle()
    val scoreDisplay = players.sumBy { it.score } //not dynamic, taken from last level

    var noOfPlayersInGameAlready = 0
    var timer = 0f

    var levelFinished = false
    var winner: Player? = null
    var endOfLevelMessage = ""

    init {

        for (layer in background.layers) {
            for (obj in layer.objects) {
                createObjectFromMap(obj)
            }
        }
    }

    private fun createObjectFromMap(obj: MapObject) {
        val type = obj.properties["type"]
        if (type != null && obj is RectangleMapObject) {
            when (type) {
                "exit" -> createExit(obj)
                "entry" -> createEntry(obj)
                "spawner" -> createSpawner(obj)
                "goblin" -> createGoblin(obj)
            }
        }
    }

    private fun createGoblin(obj: RectangleMapObject) {
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

    private fun createSpawner(obj: RectangleMapObject) {
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

    private fun createEntry(obj: RectangleMapObject) {
        entry.x = obj.rectangle.x
        entry.y = obj.rectangle.y
        entry.width = obj.rectangle.width
        entry.height = obj.rectangle.height
    }

    private fun createExit(obj: RectangleMapObject) {
        exits.add(obj.rectangle)
    }

    override fun doLogic(deltaTime: Float) {
        timer += deltaTime
        if (levelFinished) {
            doGameoverLogic()
        } else {
            doGameLogic()
        }
    }

    private fun doGameoverLogic() {
        if (timer > 1f && players.any { it.input?.fire }) {
            gameover()
        }
    }

    private fun doGameLogic() {

        val pitch = if (timeleft() > 30) 1.0f else 2.0f - timeleft() / 30f
        music.setPitch(pitch)

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
            timeOver()
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
        batch.begin()
        if (levelFinished) {
            drawEndOfLevelScreen(batch)
        } else {
            drawGame(batch)
        }
        batch.end()
    }

    private fun drawGame(batch: Batch) {
        Prefs.BinPref.BILINEAR.filter(bgTexture)
        batch.draw(bgTexture, 0f, 0f)
        drawSprites(batch)
        drawText(batch)
        if (isMobile() && noFingersTouchingScreen()) {
            batch.draw(controlsImageLayer, 0f, 0f)
        }
    }

    private fun drawText(batch: Batch) {
        font.color = if (timeleft() > 21) Color.WHITE else redFlash.getKeyFrame(timer, true)
        font.draw(batch, "${timeleft()}", 150f, 240f)
        font.color = Color.WHITE
        font.draw(batch, "L${friendlyLevelNumber()}", 0f, 240f)
        font.draw(batch, "$scoreDisplay PTS", 0f, 240f, 320f, Align.right, false)
    }

    private fun noFingersTouchingScreen(): Boolean = !Gdx.input.isTouched()

    private fun friendlyLevelNumber() = (difficulty - 1) * maps.size + level + 1

    fun drawSprites(batch: Batch) {
        allSprites.forEach {
            it.draw(batch)
        }
    }

    private fun drawEndOfLevelScreen(batch: Batch) {
        val s = "\n\n\nLEVEL ${friendlyLevelNumber()}\n\n${endOfLevelMessage}\n" +
                scoreTable() +
                "\n\n\nTOTAL SCORE ${players.sumBy { it.score }}"

        font.draw(batch, s, 0f, 240f, 320f, Align.center, false)
    }

    fun scoreTable() = players.joinToString("") {
        (if (it == winner) "[${multiFlash.getKeyFrame(timer, true)}]" else "") +
                "\n\n${it.name} ${it.score}[]"
    }

    private fun timeleft(): Int {
        return (timeLimit - timer).toInt()
    }

    private fun checkForNewPlayerJoins() {
        for (i in noOfPlayersInGameAlready until players.size) { // loop only when there is a new player(s) joined
            val xy: Pair<Int, Int> = getCoordsOfSpriteInSheet(i)

            val pimp = PimpGuy(players[i], this, xy.first, xy.second)
            pimp.x = entry.x
            pimp.y = entry.y

            allSprites.add(pimp)
            noOfPlayersInGameAlready++
        }
    }

    private fun getCoordsOfSpriteInSheet(player: Int) = when (player) {
        0 -> Pair(26, 1)// different players get different texturegions within the spritesheet
        1 -> Pair(32, 1)// these are the offsets in the sheet
        2 -> Pair(38, 1)
        3 -> Pair(26, 2)
        4 -> Pair(32, 2)
        5 -> Pair(38, 2)
        else -> Pair(26, 1)
    }

    private fun timeOver() {
        session.nextGame = null
        levelFinished = true
        endOfLevelMessage = "[RED]TIME OVER[]"
        timer=0f
        music.stop()
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
        endOfLevelMessage = "[BLUE]COMPLETE[]"
        timer=0f
    }

    override fun show() {
        if (Prefs.BinPref.MUSIC.isEnabled()) music.play()
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
