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
    public static final boolean DEBUG = false;
    public static final String BUILD_DATE = "2/12/15";

    BitmapFont font;
    SpriteBatch batch;
    AssetManager assetManager;
    GameScreen gameScreen;
    LevelSelectionScreen levelSelectionScreen;
    EndScreen endScreen;

    @Override
    public void create() {
        font = new BitmapFont();
        font.setColor(Color.RED);

        batch = new SpriteBatch();

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        gameScreen = new GameScreen(this);
        levelSelectionScreen = new LevelSelectionScreen(this);
        endScreen = new EndScreen(this);

        setToLevelSelectionScreen();
    }

    public void setToLevelSelectionScreen(){
        this.setScreen(levelSelectionScreen);
    }

    public void setToGameScreen(int levelNumber){
        gameScreen.loadNewLevel(levelNumber);
        this.setScreen(gameScreen);
    }

    public void setToEndScreen(LevelStat stat){
        endScreen.setStat(stat);
        this.setScreen(endScreen);
    }

    @Override
    public void dispose() {
        System.out.println("ClassicTanks dispose");
        font.dispose();
        batch.dispose();
        assetManager.dispose();
        gameScreen.dispose();
        levelSelectionScreen.dispose();
        endScreen.dispose();
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
