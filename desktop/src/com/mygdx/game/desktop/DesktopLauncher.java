package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.mygdx.game.ButtonMasherGame;
import uk.me.fantastic.retro.App;
import uk.me.fantastic.retro.desktop.DesktopCallback;
import uk.me.fantastic.retro.screens.GameSession;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		
		DesktopCallback callback = new DesktopCallback();
		
		
		App app = new App(callback){
			@Override
			public void start(){
				GameSession session = new GameSession();
				session.setGame(new ButtonMasherGame(session));
				setScreen(session);
			}
		};
		
		new LwjglApplication(app, callback.getConfig());
		

	}
}
