package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by Jingyu_Yao on 1/30/2015.
 */
public class LevelPanel extends Actor {

    private final int levelNumber;
    private LevelScreen levelScreen;
    Sprite sprite;

    public LevelPanel(final LevelScreen levelScreen, int x, int y, final int levelNumber){
        this.levelScreen = levelScreen;
        this.levelNumber = levelNumber;

        setBounds(x, y, 64, 64);
        sprite = new Sprite(new Texture(Gdx.files.internal("level1.png")));

        addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                levelScreen.game.setScreen(new GameScreen(levelScreen.game, levelNumber));
                return true;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        sprite.setX(getX());
        sprite.setY(getY());
        sprite.draw(batch);
    }
}
