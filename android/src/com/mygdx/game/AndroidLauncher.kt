package com.mygdx.game

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import uk.me.fantastic.retro.SingleGameApp

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val callback = AndroidCallback()

        val app = SingleGameApp(callback, "Ghost Jumpers", PimpGameFactory(), MyTitleScreen::class.java, null)
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        initialize(app, config)
    }
}
