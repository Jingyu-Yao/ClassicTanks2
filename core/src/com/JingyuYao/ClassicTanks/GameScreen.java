package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * this class is completely replaced by level..... wtf
 */
public class GameScreen implements Screen {

    public static final int CAMERA_SIZE = Level.TILE_SIZE * 20;
    public static final int CAMERA_INNER_BOUND = CAMERA_SIZE / 8;
    public static final float TILED_SCALE = 1f;

    private final ClassicTanks game;

    // Loaded sprites
    private final Sprite bulletSprite;
    private final Map<Tank.TankType, Sprite> tankSprites;

    /*
    Renderer objects
     */
    private final Viewport viewPort;
    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;

    private Level level;

    public GameScreen(final ClassicTanks g, int levelNumber) {
        game = g;

        bulletSprite = new Sprite(new Texture(Gdx.files.internal("bullet.png")));
        tankSprites = new HashMap<Tank.TankType, Sprite>();
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

        // Camera setup
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_SIZE, CAMERA_SIZE);
        viewPort = new ScreenViewport();
        viewPort.setCamera(camera);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(null, TILED_SCALE);

        loadLevel(levelNumber);
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

        level = new Level(levelNumber, game.assetManager, tankSprites, bulletSprite, viewPort);

        // set up tiled map renderer
        tiledMapRenderer.setMap(level.getMap());
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
         * openGL stuff
		 * ********************************************************
		 */
        Gdx.gl.glClearColor(0, 0, 0.2f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /*
         * Render tiled map including background and walls
		 */
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        level.advanceTime(delta);

        if(level.checkLevelCompletion()){
            level.dispose();
            game.assetManager.clear();
            game.setScreen(new LevelSelectionScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
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
