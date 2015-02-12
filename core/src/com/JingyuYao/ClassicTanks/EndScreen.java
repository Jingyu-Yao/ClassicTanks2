package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Jingyu_Yao on 2/12/2015.
 */
public class EndScreen implements Screen{
    private final ClassicTanks game;
    private final SpriteBatch batch; // From game
    private final BitmapFont font;// From game
    private final LevelStat stat;

    public EndScreen(ClassicTanks game, LevelStat stat) {
        this.game = game;
        this.stat = stat;
        batch = game.batch;
        font = game.font;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
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
