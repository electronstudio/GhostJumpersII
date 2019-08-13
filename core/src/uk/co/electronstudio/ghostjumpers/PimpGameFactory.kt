package uk.co.electronstudio.ghostjumpers


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.MathUtils
import uk.co.electronstudio.retrowar.*
import uk.co.electronstudio.retrowar.menu.MultiChoiceMenuItem
import uk.co.electronstudio.retrowar.screens.GameSession

/*
 * Used by RetroWar to create our main class
 */
class PimpGameFactory(pathPrefix: String) : AbstractGameFactory("Ghost Jumpers", null, pathPrefix) {

    override val image by lazy { Texture(Gdx.files.internal("$pathPrefix/pimpenemy.png")) }

    val music = CrossPlatformMusic.create(desktopFile = pathPrefix+"GhostJumpersSpooooky.ogg", androidFile =
    pathPrefix+"hostJumpersSpooooky.ogg", iOSFile = pathPrefix+"GhostJumpersSpooooky.mp3")

    val music2 = CrossPlatformMusic.create(desktopFile = pathPrefix+"JustinLong.ogg", androidFile =
    pathPrefix+"JustinLong.ogg", iOSFile = pathPrefix+"justin1.wav")


    val font = BitmapFont(Gdx.files.internal(pathPrefix+"english.fnt")) // for drawing text
    val maps = listOf<String>(
            pathPrefix+"level1.tmx",
            pathPrefix+"level2.tmx",
            pathPrefix+"level3.tmx"
    )

    var howManyLevelsToPlay = -1
    var difficultyLevel = 1

    override val description = "A platform game. Avoid the ghosts and get to the exit as quickly as you can.  May be " +
            "played single player!"

    override val options = listOf(
            MultiChoiceMenuItem(
                    "Difficulty: ",
                    choices = listOf("EASY", "MEDIUM", "HARD"),
                    intValues = listOf(1, 4, 7),
                    onUpdate = { _, i -> difficultyLevel = i }
            ),
            MultiChoiceMenuItem(
                    "Maximum levels: ",
                    choices = listOf("UNLIMITED", "1", "3", "6"),
                    intValues = listOf(-1, 1, 3, 6),
                    onUpdate = { _, i -> howManyLevelsToPlay = i }
            )
    )


    override fun create(session: GameSession): Game {
        return PimpGame(session, difficultyLevel, 0, howManyLevelsToPlay, pathPrefix, this)
    }

    override fun createWithSimpleSettings(session: GameSession): Game {
        return PimpGame(session, 1, 0, -1, pathPrefix, this)
    }

    override fun createWithTournamentSettings(session: GameSession): Game {
        return PimpGame(session, MathUtils.random(2,6), MathUtils.random(maps.size-1), 1, pathPrefix, this)
    }
}
