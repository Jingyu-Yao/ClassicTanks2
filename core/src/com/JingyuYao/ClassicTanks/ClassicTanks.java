package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class ClassicTanks extends Game {

    public static final int SCREEN_SIZE = 500;

    BitmapFont font;
    SpriteBatch batch;
    AssetManager assetManager;

    @Override
    public void create() {
        font = new BitmapFont();
        font.setColor(Color.RED);
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        setToLevelSelectionScreen();
    }

    public void setToLevelSelectionScreen(){
        this.setScreen(new LevelSelectionScreen(this));
    }

    public void setToGameScreen(int levelNumber){
        this.setScreen(new GameScreen(this, levelNumber));
    }

    @Override
    public void dispose() {
        font.dispose();
        assetManager.dispose();
    }

    @Override
    public void render() {
        super.render();
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
}
