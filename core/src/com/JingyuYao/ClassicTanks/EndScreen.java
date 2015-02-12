package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Jingyu_Yao on 2/12/2015.
 */
public class EndScreen implements Screen{
    private final ClassicTanks game;
    private final SpriteBatch batch; // From game
    private final BitmapFont font;// From game
    private LevelStat stat;

    public EndScreen(ClassicTanks game) {
        this.game = game;
        batch = game.batch;
        font = game.font;
    }

    public void setStat(LevelStat stat){
        this.stat = stat;
    }

    @Override
    public void show() {
        System.out.println("EndScreen show");
    }

    @Override
    public void render(float delta) {
        // Delay guard
        if (delta > 0.1f) {
            return;
        }

        Gdx.gl20.glClearColor(1.0f, 1.0f, 1.0f, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.drawMultiLine(batch, "test", 100, 100);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

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
