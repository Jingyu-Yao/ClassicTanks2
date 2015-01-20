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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    final ClassicTanks game;

    public OrthographicCamera camera;

    static int level;
    static int xBound = 960, yBound = 960;
    final static int TILE_SIZE = 32;
    static float startX = TILE_SIZE, startY = TILE_SIZE; //This need to change dynamically with map
    static int cameraSize = 640;
    static int cameraInnerBound = 80;

    Array<Enemy> enemies = new Array<Enemy>();
    Array<Bullet> bullets = new Array<Bullet>();
    Array<Wall> walls = new Array<Wall>();
    Player player = new Player(this,startX, startY, Tank.TankType.NORMAL, Direction.UP);

    long lastBulletTime = 0l;
    long fireRate = 1000000000l; // 1s
    long lastDirectionTime = 0l;
    long directionPauseTime = 100000000l;
    long curTime;
    TiledMap map;
    float tiledScale = 1f;
    OrthogonalTiledMapRenderer tiledMapRenderer;
    Sprite bulletSprite;
    Hashtable<Tank.TankType, Sprite> tankSprites = new Hashtable<Tank.TankType, Sprite>();
    TiledMapTileLayer wallLayer;
    TiledMapTile backgroundTile;

    FPSLogger fps = new FPSLogger();

    public GameScreen(final ClassicTanks g) {
        game = g;
        level = 1;
        String levelString = "level1.tmx";

        // load tiled map
        game.assetManager.load(levelString, TiledMap.class);
        game.assetManager.finishLoading();
        map = game.assetManager.get("level1.tmx");
        // get a default background grass tile to replace the wallz
        wallLayer = (TiledMapTileLayer) map.getLayers().get("Walls");
        TiledMapTileLayer tmp = (TiledMapTileLayer) (map.getLayers()
                .get("Background"));
        backgroundTile = tmp.getCell(0, 0).getTile();
        makeWallFromTile();

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, cameraSize, cameraSize);

        // set up tiled map renderer
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, tiledScale);

        createSprites();

        enemies.add(new Enemy(this,32, 128, Tank.TankType.ARMORED, Direction.RIGHT));
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
     * Populates the walls array with every wall in wallLayer with the appropriate x,y,width,height
     */
    private void makeWallFromTile() {
        TiledMapTileLayer.Cell cell;
        int prop;
        for (int i = 0; i < wallLayer.getWidth(); i++) {
            for (int j = 0; j < wallLayer.getHeight(); j++) {
                cell = wallLayer.getCell(i, j);
                if (cell != null) {
                    prop = Integer.parseInt(cell.getTile().getProperties()
                            .get("hp", String.class));
                    // multiply by tile size to match pixel coordinates
                    walls.add(new Wall(this, i * TILE_SIZE, j * TILE_SIZE, prop));
                }
            }
        }
    }

    // Player control helper methods

    /**
     * Move the player in the direction passed
     * @param direction
     */
    private void playerMove(Direction direction) {
        if (player.getDirection() == direction){
            player.move();
        }
        else {
            player.setDirection(direction);
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
     * Kill the cell at (x,y) position in world space
     * @param x
     * @param y
     */
    public void killCell(float x, float y){
        wallLayer.getCell((int) (x / TILE_SIZE),
                (int) (y / TILE_SIZE)).setTile(backgroundTile);
    }

    /**
     * Reset the player's position
     */
    private void resetPlayer(){
        player.setX(startX);
        player.setY(startY);
    }

    /**
     * TODO
     */
    private void gameOver(){
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
        drawTank(player);
        player.update(delta);

        // draw and update enemies
        for(int i = 0; i < enemies.size; i++) {
            Enemy e = enemies.get(i);
            drawTank(e);
            e.update(delta);
        }

        // draw and update bullets
        for(int i = 0; i < bullets.size; i++) {
            Bullet b = bullets.get(i);
            drawBullet(b);
            b.update(delta);
        }

        game.batch.end();

		/* *********************************************************
		 * Input handling
		 * ********************************************************
		 */
        if (!player.moving && curTime - lastDirectionTime > directionPauseTime) {
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
                player.shoot();
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
