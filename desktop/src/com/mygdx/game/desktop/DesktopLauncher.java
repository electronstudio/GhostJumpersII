package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.mygdx.game.PimpGameFactory;
import uk.me.fantastic.retro.App;
import uk.me.fantastic.retro.SimpleApp;
import uk.me.fantastic.retro.desktop.DesktopCallback;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopCallback callback = new DesktopCallback();
		App app = new SimpleApp(callback, "Ghost Jumpers", new PimpGameFactory());
		new LwjglApplication(app, callback.getConfig());
	}
}
