package com.mygdx.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.ButtonMasherGame;
import com.mygdx.game.MyTitleScreen;
import uk.me.fantastic.retro.App;
import uk.me.fantastic.retro.SingleGameApp;
import uk.me.fantastic.retro.desktop.DesktopCallback;
import uk.me.fantastic.retro.games.AbstractGameFactory;
import uk.me.fantastic.retro.games.GameFactory;
import uk.me.fantastic.retro.screens.GameSession;
import uk.me.fantastic.retro.screens.GenericTitleScreen;

import java.util.ArrayList;
import java.util.List;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		
		DesktopCallback callback = new DesktopCallback();
		
		
//		App app = new App(callback){
//			@Override
//			public void start(){
//				GameFactory factory = new GameFactory("ButtonMasher", (GameSession s) -> new ButtonMasherGame(s), null);
//
//				GameSession session = new GameSession(factory, null);
//				//session.setGame(new ButtonMasherGame(session));
//				setScreen(session);
//			}
//		};

		//Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));

	//	GameFactory factory = new GameFactory("ButtonMasher", (GameSession s) -> new ButtonMasherGame(s), null);

		//ArrayList<AbstractGameFactory> games = new ArrayList<AbstractGameFactory>();

	//	games.add(factory);
		App app = new SingleGameApp(callback, "Button Masher", ButtonMasherGame.class, MyTitleScreen.class, null);


		new Lwjgl3Application(app, callback.getConfig());
		

	}
}
