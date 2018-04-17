package uk.co.electronstudio.ghostjumpers

import com.badlogic.gdx.utils.TimeUtils
import java.lang.Thread.sleep

class FPSLimiter(private val fps: Float) {

    private var previousTime = TimeUtils.nanoTime()
    private var currentTime = TimeUtils.nanoTime()
    private var deltaTime: Long = 0

    fun delay() {
        currentTime = TimeUtils.nanoTime()
        deltaTime += currentTime - previousTime
        while (deltaTime < 1000000000 / fps) {
            previousTime = currentTime
            val diff = (1000000000 / fps - deltaTime).toLong()
            if (diff / 1000000 > 1) {
                println("fopo")
                try {
                    Thread.sleep(diff / 1000000 - 1)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
            currentTime = TimeUtils.nanoTime()
            deltaTime += currentTime - previousTime
            previousTime = currentTime
        }
        deltaTime -= (1000000000 / fps).toLong()
    }
}