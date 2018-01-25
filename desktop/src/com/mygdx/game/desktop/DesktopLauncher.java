package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.mygdx.game.PimpGame;
import com.mygdx.game.MyTitleScreen;
import uk.me.fantastic.retro.App;
import uk.me.fantastic.retro.SingleGameApp;
import uk.me.fantastic.retro.desktop.DesktopCallback;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		
		DesktopCallback callback = new DesktopCallback();
		
		
//		App app = new App(callback){
//			@Override
//			public void start(){
//				GameFactory factory = new GameFactory("ButtonMasher", (GameSession s) -> new PimpGame(s), null);
//
//				GameSession session = new GameSession(factory, null);
//				//session.setGame(new PimpGame(session));
//				setScreen(session);
//			}
//		};

		//Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));

	//	GameFactory factory = new GameFactory("ButtonMasher", (GameSession s) -> new PimpGame(s), null);

		//ArrayList<AbstractGameFactory> games = new ArrayList<AbstractGameFactory>();

	//	games.add(factory);
		App app = new SingleGameApp(callback, "Button Masher", PimpGame.class, MyTitleScreen.class, null);


		new Lwjgl3Application(app, callback.getConfig());
		

	}
}
