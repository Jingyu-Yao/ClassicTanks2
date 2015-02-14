package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles level deployment and interaction with LevelSelectionScreen
 */
public class GameScreen implements Screen {

    public static final int CAMERA_SIZE = Level.TILE_SIZE * 20;
    public static final int CAMERA_INNER_BOUND = CAMERA_SIZE / 8;
    public static final float TILED_SCALE = 1f;

    private final ClassicTanks game;

    // Loaded sprites
    private final Sprite bulletSprite;
    private final Map<Tank.TankType, Sprite> tankSprites;
    private final Map<Buff.BuffType, Sprite> buffSprites;

    /*
    Renderer objects
     */
    private final Viewport viewPort;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final SpriteBatch batch; //From game
    private final BitmapFont font; //From game

    private Level level;

    /**
     * Create a new {@code GameScreen} of {@code levelNumber}
     *
     * @param g the {@code Game} this {@code Screen} belongs to
     */
    public GameScreen(final ClassicTanks g) {
        game = g;
        batch = game.batch;
        font = game.font;

        bulletSprite = new Sprite(new Texture(Gdx.files.internal("sprites/bullet.png")));
        tankSprites = new HashMap<Tank.TankType, Sprite>();
        tankSprites.put(Tank.TankType.ARMORED,
                new Sprite(new Texture(Gdx.files.internal("sprites/ARMORED.png"))));
        tankSprites.put(Tank.TankType.NORMAL,
                new Sprite(new Texture(Gdx.files.internal("sprites/NORMAL.png"))));
        tankSprites.put(Tank.TankType.BARRAGE,
                new Sprite(new Texture(Gdx.files.internal("sprites/BARRAGE.png"))));
        tankSprites.put(Tank.TankType.DUAL,
                new Sprite(new Texture(Gdx.files.internal("sprites/DUAL.png"))));
        tankSprites.put(Tank.TankType.FAST,
                new Sprite(new Texture(Gdx.files.internal("sprites/FAST.png"))));
        tankSprites.put(Tank.TankType.SUPER,
                new Sprite(new Texture(Gdx.files.internal("sprites/SUPER.png"))));

        buffSprites = new HashMap<Buff.BuffType, Sprite>();
        buffSprites.put(Buff.BuffType.STAR,
                new Sprite(new Texture(Gdx.files.internal("sprites/STAR.png"))));
        buffSprites.put(Buff.BuffType.FREEZE,
                new Sprite(new Texture(Gdx.files.internal("sprites/FREEZE.png"))));
        buffSprites.put(Buff.BuffType.BOOM,
                new Sprite(new Texture(Gdx.files.internal("sprites/BOOM.png"))));
        buffSprites.put(Buff.BuffType.LIFE,
                new Sprite(new Texture(Gdx.files.internal("sprites/LIFE.png"))));
        buffSprites.put(Buff.BuffType.ARMOR_UP,
                new Sprite(new Texture(Gdx.files.internal("sprites/ARMOR_UP.png"))));

        // Camera setup
        viewPort = new ScalingViewport(Scaling.fit, CAMERA_SIZE, CAMERA_SIZE);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(null, TILED_SCALE);
    }

    public void loadNewLevel(int levelNumber) {
        viewPort.setScreenX(CAMERA_SIZE / 2);
        viewPort.setScreenY(CAMERA_SIZE / 2);

        level = new Level(levelNumber, game.assetManager,
                tankSprites, bulletSprite, buffSprites,
                viewPort, font, batch);

        // set up tiled map renderer
        tiledMapRenderer.setMap(level.getMap());
    }

    /**
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
        Gdx.gl20.glClearColor(0, 0, 0.2f, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /*
         * Render tiled map including background and walls
		 */
        tiledMapRenderer.setView((OrthographicCamera) viewPort.getCamera());
        tiledMapRenderer.render();

        if (level.isLevelEnded()) {
            // Disposing doesn't get rid of the stat
            level.dispose();
            game.setToEndScreen(level.getStat());
        }

        level.actLevel(delta);
        level.spawn();
        level.drawLevel();

        //Test mode stuff
        if (ClassicTanks.DEBUG) {
            batch.begin();
            font.drawMultiLine(batch, "Debug mode," + ClassicTanks.BUILD_DATE + "\n" +
                            "Tank type change keys: A,S,D,F,B,N",
                    0, viewPort.getScreenHeight() - font.getScaleY());
            batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width, height);
    }

    @Override
    public void show() {
        System.out.println("GameScreen show");
        font.setColor(Color.RED);
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
        System.out.println("GameScreen dispose");
        tiledMapRenderer.dispose();
    }

}
