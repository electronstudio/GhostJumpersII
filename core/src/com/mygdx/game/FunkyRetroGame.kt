package com.mygdx.game

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import uk.me.fantastic.retro.games.RetroGame
import uk.me.fantastic.retro.screens.GameSession
import java.util.*

//FIXME there must be a way to do this without needing to specify T four times, DNRY

abstract class FunkyRetroGame<T : Enum<T>>(
        clazz: Class<T>,
        enums: Array<T>,
        session: GameSession,
        bgFile: String,
        w: Float, h: Float, font: BitmapFont, font1: BitmapFont)
    : RetroGame(session, w, h, font, font1) {

    val allSprites = ArrayList<RSprite>()

    val spriteGroups: EnumMap<T, ArrayList<RSprite>> = EnumMap(clazz)

    val background = TmxMapLoader().load(bgFile)!!

    init {
        enums.forEach {
            spriteGroups.put(it, ArrayList())
        }
    }

    fun checkBackgroundCollisions() {
        allSprites.forEach {
            it.collisionTest(background)
        }
    }

    override fun doLogic(delta: Float) {  // Called automatically every frame

        allSprites.forEach {
            it.update(delta)
        }


    }
}
