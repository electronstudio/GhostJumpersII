package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import uk.me.fantastic.retro.App
import uk.me.fantastic.retro.games.AbstractGameFactory
import uk.me.fantastic.retro.games.Game
import uk.me.fantastic.retro.input.GamepadInput
import uk.me.fantastic.retro.input.KeyboardMouseInput
import uk.me.fantastic.retro.input.SimpleTouchscreenInput
import uk.me.fantastic.retro.isMobile
import uk.me.fantastic.retro.menu.MultiChoiceMenuItem
import uk.me.fantastic.retro.screens.GameSession

/*
 * Used by RetroWar to create our main class
 */
class PimpGameFactory : AbstractGameFactory("Ghost Jumpers", null) {

    override val image by lazy { Texture(Gdx.files.internal("mods/PimpGame/pimpenemy.png")) }

    var howManyLevelsToPlay = -1
    var difficultyLevel = 1

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
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            val controller1 = App.mappedControllers.firstOrNull()
            if (controller1 != null) {
                session.preSelectedInputDevice = GamepadInput(controller1)
            } else {
                session.preSelectedInputDevice = KeyboardMouseInput(session)
                session.KBinUse = true
            }
        } else if (isMobile()) {
            session.preSelectedInputDevice = SimpleTouchscreenInput()
        }

        return PimpGame(session, difficultyLevel, 0, howManyLevelsToPlay)
    }

    override fun createWithTournamentSettings(session: GameSession): Game {

        return PimpGame(session, difficultyLevel, 0, 3)
    }
}
