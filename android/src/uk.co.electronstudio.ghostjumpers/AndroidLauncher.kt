package uk.co.electronstudio.ghostjumpers


import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import uk.co.electronstudio.retrowar.EmptyCallback
import uk.co.electronstudio.retrowar.utils.AndroidLogger
import uk.co.electronstudio.retrowar.SimpleApp


class Launcher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val app = SimpleApp(EmptyCallback(), "Ghost Jumpers", PimpGameFactory::class.java, AndroidLogger(BuildConfig.VERSION_NAME, ""))
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        initialize(app, config)
    }
}
