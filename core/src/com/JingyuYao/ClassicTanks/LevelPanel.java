package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by Jingyu_Yao on 1/30/2015.
 */
public class LevelPanel extends Actor {

    private final int levelNumber;
    private final String levelString;
    private final LevelSelectionScreen levelSelectionScreen;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;

    public LevelPanel(final LevelSelectionScreen levelSelectionScreen, final int levelNumber, ShapeRenderer shapeRenderer, BitmapFont font, int x, int y){
        this.levelSelectionScreen = levelSelectionScreen;
        this.levelNumber = levelNumber;
        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.levelString = "Level " + levelNumber;

        setBounds(x, y, LevelSelectionScreen.PANEL_SIZE, LevelSelectionScreen.PANEL_SIZE);

        addListener(new InputListener(){
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                levelSelectionScreen.startLevel(levelNumber);
                return true;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.setProjectionMatrix(levelSelectionScreen.getCamera().combined);
        shapeRenderer.rect(getX(),getY(),getWidth(),getHeight());
        shapeRenderer.end();
        batch.begin();
        font.draw(batch, levelString, getX(), getY() + getHeight()/2);
    }
}
