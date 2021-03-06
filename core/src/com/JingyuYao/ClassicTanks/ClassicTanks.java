package com.JingyuYao.ClassicTanks;

import com.JingyuYao.ClassicTanks.level.LevelStat;
import com.JingyuYao.ClassicTanks.screens.EndScreen;
import com.JingyuYao.ClassicTanks.screens.GameScreen;
import com.JingyuYao.ClassicTanks.screens.LevelSelectionScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class ClassicTanks extends Game {

    public static final int SCREEN_SIZE = 500;
    public static final boolean DEBUG = false;
    public static final String BUILD_DATE = "2/12/15";

    public BitmapFont font;
    public SpriteBatch batch;
    public AssetManager assetManager;
    public GameScreen gameScreen;
    public LevelSelectionScreen levelSelectionScreen;
    public EndScreen endScreen;

    public GameData playerData;

    @Override
    public void create() {
        font = new BitmapFont();
        font.setColor(Color.RED);

        playerData = new GameData();

        batch = new SpriteBatch();

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        assetManager.load("sounds/shoot.wav", Sound.class);
        assetManager.load("sprites/texture-atlas.txt", TextureAtlas.class);

        assetManager.finishLoading();
        System.out.println("sounds/shoot.wav loaded");
        System.out.println("sprites/texture-atlas.txt loaded");

        gameScreen = new GameScreen(this);
        levelSelectionScreen = new LevelSelectionScreen(this);
        endScreen = new EndScreen(this);

        setToLevelSelectionScreen();
    }

    public void setToLevelSelectionScreen() {
        this.setScreen(levelSelectionScreen);
    }

    public void setToGameScreen(int levelNumber) {
        gameScreen.loadNewLevel(levelNumber);
        this.setScreen(gameScreen);
    }

    public void setToEndScreen(LevelStat stat) {
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
