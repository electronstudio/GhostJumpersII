package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.mygdx.game.MyTitleScreen;
import com.mygdx.game.PimpGameFactory;
import uk.me.fantastic.retro.App;
import uk.me.fantastic.retro.SingleGameApp;
import uk.me.fantastic.retro.desktop.DesktopCallback;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopCallback callback = new DesktopCallback();

		//FIXME remove name and t, they are unneccessary
		App app = new SingleGameApp(callback, "Ghost Jumpers", new PimpGameFactory(), MyTitleScreen.class, null);

		new LwjglApplication(app, callback.getConfig());
	}
}
