package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import uk.me.fantastic.retro.games.RetroGame
import uk.me.fantastic.retro.screens.GameSession

abstract class BasicRetroGame(session: GameSession,

                              w: Float, h: Float, font: BitmapFont, font1: BitmapFont)
    : RetroGame(session, w, h, font, font1) {

    val allSprites = ArrayList<RetroSprite>()

    var timer = 0f

    override fun doLogic(delta: Float) { // Called automatically every frame
        timer += delta

        allSprites.forEach {
            it.update()
        }
        allSprites.removeIf { it.dead }
    }

    fun drawSprites(batch: Batch) {

        allSprites.forEach {
            it.draw(batch)
        }
    }

    // These methods must be implemented but don't have to do anything
    override fun show() {}

    override fun hide() {}
    override fun dispose() {}
}
