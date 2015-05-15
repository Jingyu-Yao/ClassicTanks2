package com.JingyuYao.ClassicTanks.screens;

import com.JingyuYao.ClassicTanks.ClassicTanks;
import com.JingyuYao.ClassicTanks.level.LevelStat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Created by Jingyu_Yao on 2/12/2015.
 */
public class EndScreen implements Screen {
    private final ClassicTanks game;
    private final SpriteBatch batch; // From game
    private final BitmapFont font;// From game
    private LevelStat stat;
    private String statString;
    private final Stage stage;
    private final TextButton returnToLevelSelect;
    private final float statStringYLoc;

    public EndScreen(final ClassicTanks game) {
        this.game = game;
        batch = game.batch;
        font = game.font;
        stage = new Stage();
        statStringYLoc = Gdx.graphics.getHeight() - font.getScaleY();

        TextButton.TextButtonStyle rtls = new TextButton.TextButtonStyle();
        rtls.font = font;
        rtls.fontColor = Color.BLUE;
        returnToLevelSelect = new TextButton("Return to level select.", rtls);
        returnToLevelSelect.setBounds(100, 100, 100, 100);
        returnToLevelSelect.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setToLevelSelectionScreen();
                return true;
            }
        });

        stage.addActor(returnToLevelSelect);
    }

    public void setStat(LevelStat stat) {
        this.stat = stat;
        statString = stat.toString();
    }

    @Override
    public void show() {
        System.out.println("EndScreen show");
        Gdx.input.setInputProcessor(stage);
        font.setColor(Color.BLACK);
    }

    @Override
    public void render(float delta) {
        // Delay guard
        if (delta > 0.1f) {
            return;
        }

        Gdx.gl20.glClearColor(1.0f, 1.0f, 1.0f, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        batch.begin();
        font.drawMultiLine(batch, statString, 0, statStringYLoc);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
