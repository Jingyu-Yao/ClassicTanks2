package com.JingyuYao.ClassicTanks;

import java.util.Hashtable;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    final ClassicTanks game;

    public OrthographicCamera camera;

    static Level level;
    final static int TILE_SIZE = 32;
    static int cameraSize = 640;
    static int cameraInnerBound = 80;

    long lastBulletTime = 0l;
    long fireRate = 1000000000l; // 1s
    long lastDirectionTime = 0l;
    long directionPauseTime = 100000000l;
    long curTime;

    float tiledScale = 1f;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    Sprite bulletSprite;
    Hashtable<Tank.TankType, Sprite> tankSprites;

    FPSLogger fps = new FPSLogger();

    public GameScreen(final ClassicTanks g) {
        game = g;

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, cameraSize, cameraSize);

        tankSprites = new Hashtable<Tank.TankType, Sprite>();
        createSprites();

        level = new Level(1, TILE_SIZE, TILE_SIZE, this);

        // set up tiled map renderer
        tiledMapRenderer = new OrthogonalTiledMapRenderer(level.map, tiledScale);
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

    // Player control helper methods
    /**
     * Move the player in the direction passed
     * @param direction
     */
    private void playerMove(Direction direction) {
        if (level.player.getDirection() == direction){
            level.player.move();
        }
        else {
            level.player.setDirection(direction);
            lastDirectionTime = curTime;
        }
    }

    /**
     * Returns true if the current key is the only one being pressed
     * @param key
     * @return
     */
    private boolean isPressed(int key) {
        switch (key) {
            case Keys.UP:
                return Gdx.input.isKeyPressed(Keys.UP)
                        && !Gdx.input.isKeyPressed(Keys.DOWN)
                        && !Gdx.input.isKeyPressed(Keys.LEFT)
                        && !Gdx.input.isKeyPressed(Keys.RIGHT);
            case Keys.DOWN:
                return Gdx.input.isKeyPressed(Keys.DOWN)
                        && !Gdx.input.isKeyPressed(Keys.UP)
                        && !Gdx.input.isKeyPressed(Keys.LEFT)
                        && !Gdx.input.isKeyPressed(Keys.RIGHT);
            case Keys.LEFT:
                return Gdx.input.isKeyPressed(Keys.LEFT)
                        && !Gdx.input.isKeyPressed(Keys.DOWN)
                        && !Gdx.input.isKeyPressed(Keys.UP)
                        && !Gdx.input.isKeyPressed(Keys.RIGHT);
            case Keys.RIGHT:
                return Gdx.input.isKeyPressed(Keys.RIGHT)
                        && !Gdx.input.isKeyPressed(Keys.DOWN)
                        && !Gdx.input.isKeyPressed(Keys.LEFT)
                        && !Gdx.input.isKeyPressed(Keys.UP);
            default:
                return false;
        }
    }

    // Drawing helper methods

    /**
     * Draw a tank
     * @param tank
     */
    private void drawTank(Tank tank) {
        Sprite s = tankSprites.get(tank.getType());
        s.setX(tank.getX());
        s.setY(tank.getY());
        switch (tank.direction) {
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
     * @param bullet
     */
    private void drawBullet(Bullet bullet) {
        bulletSprite.setX(bullet.getX());
        bulletSprite.setY(bullet.getY());
        switch (bullet.direction) {
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
     * Reset the player's position
     */
    private void resetPlayer(){
        level.player.setX(level.startX);
        level.player.setY(level.startY);
    }

    /**
     * TODO
     */
    private void gameOver(){
    }

    /**
     * TODO: Bound distance changes with direction to allow larger view
     * Changes the x and y position of the camera to follow the player.
     * The movement allowance is set by {@code GameScreen.cameraInnerBound}
     */
    private void moveCamera(){
        Vector3 cameraPosition = camera.position;
        float cameraX = cameraPosition.x, cameraY = cameraPosition.y;
        float playerX = level.player.body.x, playerY = level.player.body.y;
        int innerBound = cameraInnerBound;

        float dx = 0, dy = 0;
        if(cameraX - playerX > innerBound){
            dx = playerX - cameraX + innerBound;
        }
        else if(playerX - cameraX > innerBound){
            dx = playerX - cameraX - innerBound;
        }
        if(cameraY - playerY > innerBound){
            dy = playerY - cameraY + innerBound;
        }
        else if(playerY - cameraY > innerBound){
            dy = playerY - cameraY - innerBound;
        }
        if(dx != 0 || dy != 0){
            camera.translate(dx, dy);
            camera.update();
        }
    }

    /**
     * TODO
     * The main game loop for this screen.
     * @param delta
     */
    @Override
    public void render(float delta) {
        curTime = TimeUtils.nanoTime();

		/* *********************************************************
		 * openGL stuff, should decide on using GL20 or not
		 * ********************************************************
		 */
        Gdx.gl.glClearColor(0, 0, 0.2f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/*
		 * Render tiled map
		 */
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        // note for matrices: projection=lense,view=camera,model=model(duh)
        game.batch.setProjectionMatrix(camera.combined);

		/* *********************************************************
		 * Draw and then updates the position in one place saves the use of
		 * another loop and easier to organize Also handles collisions
		 * ********************************************************
		 */
        game.batch.begin();

        // Draw and update player
        drawTank(level.player);
        level.player.update(delta);
        moveCamera();

        // draw and update enemies
        for(int i = 0; i < level.enemies.size; i++) {
            Enemy e = level.enemies.get(i);
            drawTank(e);
            e.update(delta);
        }

        // draw and update bullets
        for(int i = 0; i < level.bullets.size; i++) {
            Bullet b = level.bullets.get(i);
            drawBullet(b);
            b.update(delta);
        }

        game.batch.end();

		/* *********************************************************
		 * Input handling
		 * ********************************************************
		 */
        if (!level.player.moving && curTime - lastDirectionTime > directionPauseTime) {
            if (isPressed(Keys.LEFT)) {
                playerMove(Direction.LEFT);
            }
            if (isPressed(Keys.RIGHT)) {
                playerMove(Direction.RIGHT);
            }
            if (isPressed(Keys.UP)) {
                playerMove(Direction.UP);
            }
            if (isPressed(Keys.DOWN)) {
                playerMove(Direction.DOWN);
            }
        }

        // controlled bullet fire rate
        if (Gdx.input.isKeyPressed(Keys.SPACE)) {
            if (curTime - lastBulletTime > fireRate) {
                lastBulletTime = curTime;
                level.player.shoot();
            }
        }
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
