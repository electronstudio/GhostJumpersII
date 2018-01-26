package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import uk.me.fantastic.retro.games.RetroGame
import uk.me.fantastic.retro.screens.GameSession
import java.util.*


abstract class FunkyRetroGame(session: GameSession,
        bgFile: String,
        w: Float, h: Float, font: BitmapFont, font1: BitmapFont)
    : RetroGame(session, w, h, font, font1) {

    val allSprites = ArrayList<RSprite>()

    val background = TmxMapLoader().load(bgFile)!!


    override fun doLogic(delta: Float) {  // Called automatically every frame

        allSprites.forEach {
            it.update(delta)
        }
    }

    fun drawSprites(batch: Batch) {

        allSprites.forEach{
            it.draw(batch)
        }
    }

    // These methods must be implemented but don't have to do anything
    override fun show() {}

    override fun hide() {}
    override fun dispose() {}
}
