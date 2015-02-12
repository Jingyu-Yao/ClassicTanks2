package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
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

    /*
    Renderer objects
     */
    private final Viewport viewPort;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;
    private final SpriteBatch batch; //From game
    private final BitmapFont font; //From game

    private Level level;
    private boolean levelRunning;
    private boolean showEndLevelText;
    private String endLevelText;

    /**
     * Create a new {@code GameScreen} of {@code levelNumber}
     * @param g the {@code Game} this {@code Screen} belongs to
     */
    public GameScreen(final ClassicTanks g) {
        game = g;
        batch = game.batch;
        font = game.font;
        endLevelText = "";

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
        viewPort = new ScalingViewport(Scaling.fit, CAMERA_SIZE, CAMERA_SIZE);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(null, TILED_SCALE);
    }

    public void loadNewLevel(int levelNumber){
        levelRunning = true;
        showEndLevelText = true;
        viewPort.setScreenX(CAMERA_SIZE / 2);
        viewPort.setScreenY(CAMERA_SIZE / 2);

        level = new Level(levelNumber, game.assetManager,
                tankSprites, bulletSprite,
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
        tiledMapRenderer.setView((OrthographicCamera)viewPort.getCamera());
        tiledMapRenderer.render();

        if(!levelRunning){
            if(showEndLevelText) {
                batch.begin();
                font.drawMultiLine(batch, endLevelText,
                        viewPort.getScreenWidth() / 3, viewPort.getScreenHeight() / 3);
                batch.end();
            }else{
                level.dispose();
                game.setToLevelSelectionScreen();
            }
        }else{
            if(level.isLevelEnded()) {
                levelRunning = false;
                if(level.isLevelLost()){
                    endLevelText = "You lose. \n" +
                            "Press anywhere to return to level select.";
                }else{
                    endLevelText = "You win. \n" +
                            "Press anywhere to return to level select.";
                }
                Gdx.input.setInputProcessor(new InputProcessor() {
                    @Override
                    public boolean keyDown(int keycode) {
                        return false;
                    }

                    @Override
                    public boolean keyUp(int keycode) {
                        return false;
                    }

                    @Override
                    public boolean keyTyped(char character) {
                        return false;
                    }

                    @Override
                    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                        showEndLevelText = false;
                        return true;
                    }

                    @Override
                    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                        return false;
                    }

                    @Override
                    public boolean touchDragged(int screenX, int screenY, int pointer) {
                        return false;
                    }

                    @Override
                    public boolean mouseMoved(int screenX, int screenY) {
                        return false;
                    }

                    @Override
                    public boolean scrolled(int amount) {
                        return false;
                    }
                });
            }else{
                level.actLevel(delta);
                level.spawn();
            }
        }
        level.drawLevel();

        //Test mode stuff
        if(ClassicTanks.DEBUG) {
            batch.begin();
            font.drawMultiLine(batch, "Debug mode," + ClassicTanks.BUILD_DATE + "\n" +
                            "Tank type change keys: A,S,D,F,G,B,N",
                    0, viewPort.getScreenHeight() - 10);
            batch.end();
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
        System.out.println("GameScreen dispose");
        if(level != null){
            level.dispose();
        }
        tiledMapRenderer.dispose();
    }

}
