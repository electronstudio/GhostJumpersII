package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Align
import uk.co.electronstudio.retrowar.App
import uk.co.electronstudio.retrowar.Player
import uk.co.electronstudio.retrowar.Prefs
import uk.co.electronstudio.retrowar.SimpleGame
import uk.co.electronstudio.retrowar.isMobile
import uk.co.electronstudio.retrowar.renderTileMapToTexture
import uk.co.electronstudio.retrowar.screens.GameSession
import java.util.ArrayList

/* The God class */
class PimpGame(session: GameSession, val difficulty: Int, val level: Int, val howManyLevelsToPlay: Int, val 
pathPrefix: String, val factory: PimpGameFactory) :
        SimpleGame(session,
                320f, 240f, factory.font) {

    override val MAX_FPS = 250f
    override val MIN_FPS = 20f

    val music = if(friendlyLevelNumber()%2 == 0) factory.music2 else factory.music

    val background = TmxMapLoader().load(factory.maps[level])!!

    val bgTexture = renderTileMapToTexture(background)

    val spriteSheet = TextureRegion(Texture(pathPrefix+"simples_pimplest.png"))
    val jumpSound = Gdx.audio.newSound(Gdx.files.internal(pathPrefix+"jump_jade.wav"))
    val stunSound = Gdx.audio.newSound(Gdx.files.internal(pathPrefix+"fall_jade.wav"))
    val bonusSound = Gdx.audio.newSound(Gdx.files.internal(pathPrefix+"bonus_jade.wav"))
    val spawnSound = Gdx.audio.newSound(Gdx.files.internal(pathPrefix+"hit_jade.wav"))
    val collectSound = Gdx.audio.newSound(Gdx.files.internal(pathPrefix+"collect_jade.wav"))
    val controlsImageLayer = Texture(pathPrefix+"controls.png")


    val textures = spriteSheet.split(16, 16)
    val redFlash = Animation<Color>(0.1f, Color.BLACK, Color.RED)
    val multiFlash = Animation<String>(1f / 30f, "RED", "PURPLE", "BLUE", "CYAN", "GREEN", "YELLOW")
    val timeLimit: Float = difficulty.toFloat() * 10.0f + 50f

    val spawners = ArrayList<GhostFactory>()
    val allSprites = ArrayList<RetroSprite>()
//    val enemies = ArrayList<RetroSprite>()
//    val coins = ArrayList<RetroSprite>()
    val exits = ArrayList<Rectangle>()
    val entry = Rectangle()
    val scoreDisplay = players.sumBy { it.score } //not dynamic, taken from last level

    var timer = 0f

    var levelFinished = false
    var winner: Player? = null
    var endOfLevelMessage = ""

    val coinChance = 0.3f

    init {
        font.data.markupEnabled = true
        for (layer in background.layers) {
            for (obj in layer.objects) {
                createObjectFromMap(obj)
            }
        }
        val tiles = background.layers.first() as TiledMapTileLayer
        for(x in 0..tiles.width){
            for(y in 0..tiles.height){
                if(cellIsEmpty(x, y) && MathUtils.random() < coinChance){
                    createCoin(x*16f, y*16f)
                }
            }
        }

    }

    private fun cellIsEmpty(x: Int, y: Int) =
            !background.layers.filterIndexed { index, mapLayer -> index != 0 }.any { it is TiledMapTileLayer && it.getCell(x, y) != null }

    private fun createObjectFromMap(obj: MapObject) {
        val type = obj.properties["type"]
        if (type != null && obj is RectangleMapObject) {
            when (type) {
                "exit" -> createExit(obj)
                "entry" -> createEntry(obj)
                "spawner" -> createSpawner(obj)
                "goblin" -> createGoblin(obj)
                "coin" -> createCoin(obj)
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
    //    enemies.add(goblin)
        allSprites.add(goblin)
    }

    private fun createCoin(obj: RectangleMapObject) {
        createCoin(obj.rectangle.x, obj.rectangle.y)
    }

    private fun createCoin(x: Float, y: Float) {
        val coin = Coin(x = x,
                y = y,
                animation = Animation(0.5f, textures[47][27], textures[47][29]))
        allSprites.add(coin)
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
            doGameLogic(deltaTime)
        }
    }

    private fun doGameoverLogic() {
        if (timer > 1f && players.any { it.input.fire }) {
            gameover()
        }
    }




    private fun doGameLogic(delta: Float) {

        val pitch = if (timeleft() > 30) 1f else if (timeleft() >20) 1.25f else if (timeleft()>10) 1.5f else 1.75f//- timeleft() / 30f
        music.setPitch(pitch)

        allSprites.forEach {
            it.update(delta)
        }

        val deadSprites: List<RetroSprite> = allSprites.filter { it.dead }
        deadSprites.forEach {
            allSprites.remove(it)
        } // JDK 8 / Android 7: allSprites.removeIf { it.dead }

        spawners.forEach { it.update(delta) }

        if (timeleft() <= 0) {
            timeOver()
        }

        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            println(App.app.testSandbox()) // should crash if sandbox is working
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            levelComplete(null)
        }
    }


    override fun doDrawing(batch: Batch) {
        if (levelFinished) {
            drawEndOfLevelScreen(batch)
        } else {
            drawGame(batch)
        }

    }

    var yScroll = 0f

    private fun drawGame(batch: Batch) {
        calculateScroll()
        scrollBatch(batch, -yScroll)
        batch.draw(bgTexture, 0f, 0f)
        drawSprites(batch)
        scrollBatch(batch, yScroll)

        drawText(batch)
        if (isMobile && noFingersTouchingScreen()) {
            batch.draw(controlsImageLayer, 0f, 0f)
        }
    }

    private fun calculateScroll() {
        val pimps = allSprites.filter { it is PimpGuy }
        if (pimps.isNotEmpty()) {
            val heighestPimp = pimps.sortedByDescending { it.y }.first().y
            if (heighestPimp > yScroll + 120f) {
                yScroll = heighestPimp - 120f
            }
            if (heighestPimp < yScroll + 100f) {
                yScroll = heighestPimp - 100f
            }
            yScroll = MathUtils.clamp(yScroll, 0f, bgTexture.regionHeight.toFloat() - height)
        }
    }

    private fun scrollBatch(batch: Batch, y: Float) {
        batch.end()
        batch.projectionMatrix.translate(0f, y, 0f)
        batch.begin()
    }

    private fun drawText(batch: Batch) {
        font.color = if (timeleft() > 21) Color.WHITE else redFlash.getKeyFrame(timer, true)
        font.draw(batch, "${timeleft()}", 150f, 240f)
        font.color = Color.WHITE
        font.draw(batch, "L${friendlyLevelNumber()}", 0f, 240f)
        font.draw(batch, "$scoreDisplay PTS", 0f, 240f, 320f, Align.right, false)
    }

    private fun noFingersTouchingScreen(): Boolean = !Gdx.input.isTouched()

    private fun friendlyLevelNumber() = (difficulty - 1) * factory.maps.size + level + 1

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

    override fun playerJoined(player: Player) {

        val xy: Pair<Int, Int> = getCoordsOfSpriteInSheet(players.indexOf(player))

        val pimp = PimpGuy(player, this, xy.first, xy.second)
        pimp.x = entry.x
        pimp.y = entry.y

        allSprites.add(pimp)
    }



    private fun getCoordsOfSpriteInSheet(player: Int) = when (player) {
        0 -> Pair(26, 1)// different players get different texturegions within the spritesheet
        1 -> Pair(26, 3)// these are the offsets in the sheet
        3 -> Pair(26, 4)
        4 -> Pair(26, 6)
        6 -> Pair(32, 1)
        5 -> Pair(32, 3)
        7 -> Pair(32, 6)
        2 -> Pair(38, 2)
        else -> Pair(38, 6)
    }

    private fun timeOver() {
        session.nextGame = null
        levelFinished = true
        endOfLevelMessage = "[RED]TIME OVER[]"
        timer = 0f
        music.stop()
    }


    fun levelComplete(winner: PimpGuy?) {
        this.winner = winner?.player
        music.stop()
        if (winner != null) {
            winner.player.score += timeleft() * difficulty
        }
        if (howManyLevelsToPlay == 1) {
            session.nextGame = null
            val overallWinner = session.findWinners().first()
            overallWinner?.incMetaScore()
        } else if (level == factory.maps.lastIndex) {
            session.nextGame = PimpGame(session, difficulty + 1, 0, howManyLevelsToPlay - 1, pathPrefix, factory)

        } else {
            session.nextGame = PimpGame(session, difficulty, level + 1, howManyLevelsToPlay - 1, pathPrefix, factory)
        }
        levelFinished = true
        endOfLevelMessage = "[BLUE]COMPLETE[]"
        timer = 0f
    }

    override fun show() {
        if (Prefs.BinPref.MUSIC.isEnabled()) music.play()
        App.app.manualGC?.disable()
    }

    override fun hide() {
        music.stop()
        App.app.manualGC?.enable()
        App.app.manualGC?.doGC()
    }

    override fun dispose() {
        spriteSheet.texture.dispose()
        jumpSound.dispose()
        stunSound.dispose()
        bonusSound.dispose()
        spawnSound.dispose()
        //music.dispose()
    }
}
