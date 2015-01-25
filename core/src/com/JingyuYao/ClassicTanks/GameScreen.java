package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen {

    final static int TILE_SIZE = 32;
    final ClassicTanks game;
    final int CAMERA_SIZE = 640;
    final int CAMERA_INNER_BOUND = 80;

    private OrthographicCamera camera;

    // Objects used for rendering
    private float tiledScale;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Sprite bulletSprite;
    private Map<Tank.TankType, Sprite> tankSprites;

    private Level level;
    //FPSLogger fps = new FPSLogger();

    public GameScreen(final ClassicTanks g) {
        game = g;
        tiledScale = 1f;

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_SIZE, CAMERA_SIZE);

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
        level = new Level(levelNumber, 1, 1, this);
        // set up tiled map renderer
        tiledMapRenderer = new OrthogonalTiledMapRenderer(level.getMap(), tiledScale);
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
        tankSprites.put(Tank.TankType.GM,
                new Sprite(new Texture(Gdx.files.internal("GM.png"))));
    }

    /**
     * Draw a tank
     *
     * @param tank
     */
    private void drawTank(Tank tank) {
        Sprite s = tankSprites.get(tank.getType());
        s.setX(tank.getX());
        s.setY(tank.getY());
        switch (tank.getDirection()) {
            case DOWN:
                s.setRotation(180);
                break;
            case LEFT:
                s.setRotation(90);
                break;
            case RIGHT:
                s.setRotation(270);
                break;
            case UP:
                s.setRotation(0);
                break;
        }
        s.draw(game.batch);
    }

    /**
     * Draw a bullet
     *
     * @param bullet
     */
    private void drawBullet(Bullet bullet) {
        bulletSprite.setX(bullet.getX());
        bulletSprite.setY(bullet.getY());
        switch (bullet.getDirection()) {
            case DOWN:
                bulletSprite.setRotation(180);
                break;
            case LEFT:
                bulletSprite.setRotation(90);
                break;
            case RIGHT:
                bulletSprite.setRotation(270);
                break;
            case UP:
                bulletSprite.setRotation(0);
                break;
        }
        bulletSprite.draw(game.batch);
    }

    /**
     * TODO: Bound distance changes with direction to allow larger view
     * Changes the x and y position of the camera to follow the player.
     * The movement allowance is set by {@code GameScreen.CAMERA_INNER_BOUND}
     */
    private void moveCamera() {
        float cameraX = camera.position.x, cameraY = camera.position.y;
        float playerX = level.player.getX(), playerY = level.player.getY();

        float dx = 0, dy = 0;
        if (cameraX - playerX > CAMERA_INNER_BOUND) {
            dx = playerX - cameraX + CAMERA_INNER_BOUND;
        } else if (playerX - cameraX > CAMERA_INNER_BOUND) {
            dx = playerX - cameraX - CAMERA_INNER_BOUND;
        }
        if (cameraY - playerY > CAMERA_INNER_BOUND) {
            dy = playerY - cameraY + CAMERA_INNER_BOUND;
        } else if (playerY - cameraY > CAMERA_INNER_BOUND) {
            dy = playerY - cameraY - CAMERA_INNER_BOUND;
        }
        if (dx != 0 || dy != 0) {
            camera.translate(dx, dy);
            camera.update();
        }
    }

    /**
     * TODO
     * The main game loop for this screen.
     *
     * @param delta
     */
    @Override
    public void render(float delta) {

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

		/* *********************************************************
         * Draw and then updates the position in one place saves the use of
		 * another loop and easier to organize Also handles collisions
		 * ********************************************************
		 */
        // note for matrices: projection=lense,view=camera,model=model(duh)
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Draw and update player
        drawTank(level.player);
        level.player.update(delta);
        moveCamera();

        // draw and update enemies
        for (int i = 0; i < level.enemies.size; i++) {
            Enemy e = level.enemies.get(i);
            drawTank(e);
            e.update(delta);
        }

        // draw and update bullets
        for (int i = 0; i < level.bullets.size; i++) {
            Bullet b = level.bullets.get(i);
            drawBullet(b);
            b.update(delta);
        }

        game.batch.end();

        level.spawn();

		/* *********************************************************
         * Input handling has been replaced by GameInputProcessor class
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
