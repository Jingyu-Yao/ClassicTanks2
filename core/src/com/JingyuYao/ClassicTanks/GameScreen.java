package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

/**
 * this class is completely replaced by level..... wtf
 */
public class GameScreen implements Screen {

    private final ClassicTanks game;

    // Loaded sprites
    public Sprite bulletSprite;
    public Map<Tank.TankType, Sprite> tankSprites;

    private Level level;

    public GameScreen(final ClassicTanks g) {
        game = g;

        tankSprites = new HashMap<Tank.TankType, Sprite>();
        createSprites();

        loadLevel(1);
    }

    /**
     * Create a new {@code Level} using the given {@code levelNumber}.
     * Also set {@code tiledMapRenderer} to draw the current level
     *
     * @param levelNumber set the current level of the game
     */
    public void loadLevel(int levelNumber) {
        if (level != null) {
            level.dispose();
        }
        level = new Level(levelNumber, this);
    }

    /**
     * Loads up the sprites for tanks and bullets
     */
    private void createSprites() {
        bulletSprite = new Sprite(new Texture(Gdx.files.internal("bullet.png")));
        tankSprites.put(Tank.TankType.ARMORED,
                new Sprite(new Texture(Gdx.files.internal("ARMORED.png"))));
        tankSprites.put(Tank.TankType.NORMAL,
                new Sprite(new Texture(Gdx.files.internal("NORMAL.png"))));
        tankSprites.put(Tank.TankType.BARRAGE,
                new Sprite(new Texture(Gdx.files.internal("BARRAGE.png"))));
        tankSprites.put(Tank.TankType.DUAL,
                new Sprite(new Texture(Gdx.files.internal("DUAL.png"))));
        tankSprites.put(Tank.TankType.FAST,
                new Sprite(new Texture(Gdx.files.internal("FAST.png"))));
        tankSprites.put(Tank.TankType.SUPER,
                new Sprite(new Texture(Gdx.files.internal("SUPER.png"))));
        tankSprites.put(Tank.TankType.GM,
                new Sprite(new Texture(Gdx.files.internal("GM.png"))));
    }

    public void loadAsset(String fileName, Class type) {
        game.assetManager.load(fileName, type);
        game.assetManager.finishLoading();
    }

    public <T> T getAsset(String assetName) {
        return game.assetManager.get(assetName);
    }

    public void unloadAsset(String assetName) {
        game.assetManager.unload(assetName);
    }

    /**
     * TODO
     * The main game loop for this screen.
     *
     * @param delta
     */
    @Override
    public void render(float delta) {
        // Delay guard
        if (delta > 0.1f) {
            return;
        }

		/* *********************************************************
         * Draw and then updates the position in one place saves the use of
		 * another loop and easier to organize Also handles collisions
		 * ********************************************************
		 */
        // note for matrices: projection=lense,view=camera,model=model(duh)
        level.advanceTime(delta);

		/* *********************************************************
         * Input handling has been replaced by KeyboardInputListener class
		 * which is started when a level is created.
		 * ********************************************************
		 */
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

}
