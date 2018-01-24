package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Align;
import org.jetbrains.annotations.NotNull;
import uk.me.fantastic.retro.games.Player;
import uk.me.fantastic.retro.games.RetroGame;
import uk.me.fantastic.retro.screens.GameSession;

/**
 * A simple RetroWar game
 */
@SuppressWarnings("ClassWithoutNoArgConstructor")
public class ButtonMasherGame extends RetroGame {
    private float time = 15;                            // The length of the game in seconds
    private String message = "";                        // We will print this to screen
    private final boolean[] buttonMashed = new boolean[4];   // Remember if button was held down
    
    private final Sprite[] sprites = {                         // An array of 4 sprites
            new Sprite(new Texture(Gdx.files.internal("ButtonMasherGame/baloon.png"))),
            new Sprite(new Texture(Gdx.files.internal("ButtonMasherGame/baloon.png"))),
            new Sprite(new Texture(Gdx.files.internal("ButtonMasherGame/baloon.png"))),
            new Sprite(new Texture(Gdx.files.internal("ButtonMasherGame/baloon.png")))
    };

    private final Texture background = new Texture(Gdx.files.internal("ButtonMasherGame/sky4.png"));
    
    private static final BitmapFont font = new BitmapFont(Gdx.files.internal("ButtonMasherGame/c64_low3_black.fnt"));   // for drawing text
    
    public ButtonMasherGame(GameSession session) {                     // Constructor (required)
        super(session, 256, 320, font, font);              // width and height of screen in pixels
                                                                        // (actually too high for a retro look
        for (int i = 0; i < sprites.length; i++) {                      // Set initial positions for the sprites
            sprites[i].setPosition(36 + i * 56, 33);
        }
    }
    
    @Override
    public void doLogic(float deltaTime) {  // Called automatically every frame
        doTimer(deltaTime);                 // deltaTime is time passed since last frame
        if (time > 10) {                    // Game has three phases, current phase depends on how much time is left
            message = "GET READY\nPress A or SPACE to join game";  // ready phase
        } else if (time > 0) {
            play(deltaTime);                                        // playing phase
        } else {
            message = "WINNER IS " + findWinner().getName();        // end phase
        }
    }
    
    private void doTimer(float deltaTime) {
        if (getPlayers().size() >= 1) {         // Dont start timing until there are at least one players
            time -= deltaTime;                  // Subtract the time since last frame from the time remaining
        }
        if (time < -10) {
            gameover();                         // end the game
        }
    }
    
    private void play(float deltaTime) {
        message = "MASH BUTTON " + Float.valueOf(time).intValue();
        for (int i = 0; i < getPlayers().size(); i++) {
            Sprite sprite = sprites[i];
            Player player = getPlayers().get(i);
            sprite.setY(sprite.getY() - deltaTime * 20);            // move sprite downwards
            if (player.getInput() != null                             // should never be null!
                        && player.getInput().getA()) {              // if button is pressed
                if (!buttonMashed[i]) {                             // prevent holding down button to cheat
                    sprite.setY(sprite.getY() + deltaTime * 300);   // move sprite upwards
                    buttonMashed[i] = true;
                }
            } else {
                buttonMashed[i] = false;
            }
            if (sprite.getY() < 33) sprite.setY(33);                 // dont fall through floor
            player.setScore(Float.valueOf(sprite.getY()).intValue());  // score = height
        }
    }
    
    private Player findWinner() {                   // search for Player with highest score
        int highScore = -99999;
        Player winner = null;
        for (Player player : getPlayers()) {
            if (player.getScore() > highScore) {
                highScore = player.getScore();
                winner = player;
            }
        }
        return winner;
    }
    
    @Override
    public void doDrawing(@NotNull Batch batch) {            // called automatically every frame

        Gdx.gl.glClearColor(0, 0, 0, 1);     // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
      //  drawShapes();
        
        batch.begin();

        batch.draw(background, 0, 0);
        drawPlayers(batch);
       
        font.draw(batch, message, 0, 312, 256, Align.center, false);
        batch.end();
    }
    
    // This is very 16 bit looking so you probably shouldn't draw shapes like this....
    // private void drawShapes() {
    //     ShapeRenderer shape = getRenderer().getShape();
    //     shape.begin(ShapeRenderer.ShapeType.Filled);
    //     shape.setColor(0f, 1f, 0f, 1f);
    //     shape.rect(0f, 0f, 640f, 200f);
    //     shape.end();                                        // draw the ground
        
    //     shape.begin(ShapeRenderer.ShapeType.Line);
    //     float b = 1;
    //     float r = 0;
    //     for (float i = 201; i <= 380; i++) {
    //         b -= 0.001;
    //         r += 0.003;
    //         shape.setColor(r, 0, b, 1f);
    //         shape.line(0f, i, 640f, i);
    //     }
    //     shape.end();                                        // draw the sky
    // }
    
    private void drawPlayers(Batch batch) {                 // draw one sprite for each Player
        for (int i = 0; i < getPlayers().size(); i++) {
            Sprite sprite = sprites[i];
            Player player = getPlayers().get(i);
            sprite.draw(batch);
            font.draw(batch, player.getName(), sprite.getX() - 15, 8);
        }
    }
    
    // These methods must be implemented but don't have to do anything
    @SuppressWarnings("EmptyMethod")
    @Override
    public void show() {
    }
    @SuppressWarnings("EmptyMethod")
    @Override
    public void hide() {
    }
    @SuppressWarnings("EmptyMethod")
    @Override
    public void dispose() {
    }
}
