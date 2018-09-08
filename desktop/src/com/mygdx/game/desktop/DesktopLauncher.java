package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import uk.co.electronstudio.ghostjumpers.PimpGameFactory;
import uk.me.fantastic.retro.App;
import uk.me.fantastic.retro.SimpleApp;
import uk.me.fantastic.retro.utils.DesktopCallback;
import uk.me.fantastic.retro.utils.SimpleLogger;


public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopCallback callback = new DesktopCallback();
		App app = new SimpleApp(callback, "Ghost Jumpers", new PimpGameFactory(), new SimpleLogger(), null, true);
		new LwjglApplication(app, callback.getConfig());
	}
}
