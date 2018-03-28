package com.mygdx.game


import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import uk.me.fantastic.retro.AndroidLogger
import uk.me.fantastic.retro.SimpleApp

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val callback = AndroidCallback()

        val app = SimpleApp(callback, "Ghost Jumpers", PimpGameFactory(), AndroidLogger(BuildConfig.VERSION_NAME, getString(R.string.dsn)))
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        initialize(app, config)
    }
}
