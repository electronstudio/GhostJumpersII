package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import uk.me.fantastic.retro.AbstractGameFactory
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.Game
import uk.me.fantastic.retro.isMobile
import uk.me.fantastic.retro.menu.MultiChoiceMenuItem
import uk.me.fantastic.retro.screens.GameSession

/*
 * Used by RetroWar to create our main class
 */
class PimpGameFactory(pathPrefix: String) : AbstractGameFactory("Ghost Jumpers", null, pathPrefix) {

    override val image by lazy { Texture(Gdx.files.internal("addons/GhostJumpers/pimpenemy.png")) }


    val font = BitmapFont(Gdx.files.internal(pathPrefix+"c64_low3_black.fnt")) // for drawing text
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
        App.app.configureSessionWithPreSelectedInputDevice(session)
        return PimpGame(session, difficultyLevel, 0, howManyLevelsToPlay, pathPrefix, this)
    }

    override fun createWithDefaultSettings(session: GameSession): Game {
        return PimpGame(session, difficultyLevel, 0, 3, pathPrefix, this)
    }
}
