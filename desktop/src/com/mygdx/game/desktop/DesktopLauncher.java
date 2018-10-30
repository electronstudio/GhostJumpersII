package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import uk.co.electronstudio.ghostjumpers.PimpGameFactory;
import uk.co.electronstudio.retrowar.App;
import uk.co.electronstudio.retrowar.SimpleApp;
import uk.co.electronstudio.retrowar.utils.DesktopCallback;
import uk.co.electronstudio.retrowar.utils.SimpleLogger;


public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopCallback callback = new DesktopCallback();
		App app = new SimpleApp(callback, "Ghost Jumpers", PimpGameFactory.class, new SimpleLogger(), null, false,
				false);
		new LwjglApplication(app, callback.getConfig());
	}
}
