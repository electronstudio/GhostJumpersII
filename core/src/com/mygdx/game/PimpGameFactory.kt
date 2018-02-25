package com.mygdx.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import uk.me.fantastic.retro.games.AbstractGameFactory
import uk.me.fantastic.retro.games.Game
import uk.me.fantastic.retro.screens.GameSession

/*
 * Used by RetroWar to create our main class
 */
class PimpGameFactory : AbstractGameFactory("Pimp Game", null) {

    override val image by lazy { Texture(Gdx.files.internal("badlogic.jpg")) }

    override fun create(gameSession: GameSession): Game {
        return PimpGame(gameSession, 1, 0)
    }
}
