package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import uk.me.fantastic.retro.screens.GenericTitleScreen;

public class MyTitleScreen extends GenericTitleScreen {

    public MyTitleScreen() {
        super(160f, 120f, new BitmapFont(Gdx.files.internal("small.fnt")), "[RED]Ghost Jumpers[]");
    }
}
