package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import uk.me.fantastic.retro.AbstractGameFactory
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.Game
import uk.me.fantastic.retro.isMobile
import uk.me.fantastic.retro.menu.MultiChoiceMenuItem
import uk.me.fantastic.retro.screens.GameSession

/*
 * Used by RetroWar to create our main class
 */
class PimpGameFactory : AbstractGameFactory("Ghost Jumpers", null) {

    override val image by lazy { Texture(Gdx.files.internal("addons/GhostJumpers/pimpenemy.png")) }

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
        return PimpGame(session, difficultyLevel, 0, howManyLevelsToPlay)
    }

    override fun createWithDefaultSettings(session: GameSession): Game {
        return PimpGame(session, difficultyLevel, 0, 3)
    }
}
