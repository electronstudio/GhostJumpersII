package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import uk.me.fantastic.retro.App;
import uk.me.fantastic.retro.screens.SimpleTitleScreen;

public class MyTitleScreen extends SimpleTitleScreen {

    public MyTitleScreen() {
        super(160f, 120f, new BitmapFont(Gdx.files.internal("small.fnt")), "[RED]Ghost Jumpers[]", App.games.get
                (0));
    }
}
