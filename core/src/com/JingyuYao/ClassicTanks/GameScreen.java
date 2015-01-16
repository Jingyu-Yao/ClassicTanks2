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

    OrthographicCamera camera;

    static int level;
    static int xBound = 960, yBound = 960;
    final static int TILE_SIZE = 32;
    static float startX = TILE_SIZE, startY = TILE_SIZE; //This need to change dynamically with map

    Array<Tank> enemies = new Array<Tank>();
    Array<Bullet> playerBullets = new Array<Bullet>();
    Array<Bullet> enemyBullets = new Array<Bullet>();
    Array<Wall> walls = new Array<Wall>();
    Tank player = new Tank(this,startX, startY, Tank.TankType.NORMAL, Direction.UP, true);

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
    Random random = new Random();

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
        camera.setToOrtho(false, xBound, yBound);

        // set up tiled map renderer
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, tiledScale);

        createSprites();

        enemies.add(new Tank(this,32, 128, Tank.TankType.ARMORED, Direction.RIGHT, false));
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
                    walls.add(new Wall(i * TILE_SIZE, j * TILE_SIZE, prop));
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

    // Collision!
    /**
     * TODO
     * Bullet collision detection.
     * @param bullet
     */
    private void collisionDetection(Bullet bullet) {
        if (bullet.isPlayer()) {
            // check enemies
            for (Tank t : enemies) {
                if (bullet.collideRect(t)) {
                    game.font.draw(game.batch, "Enemy HIT", 0, yBound - 100);
                    t.damage();
                    if(t.hp == 0) enemies.removeValue(t, true);
                    playerBullets.removeValue(bullet, true);
                }
            }
            for(Bullet e : enemyBullets){
                if (bullet.collideRect(e)) {
                    playerBullets.removeValue(bullet, true);
                    enemyBullets.removeValue(e, true);
                }
            }
        } else {
            // check player
            if (bullet.collideRect(player)) {
                game.font.draw(game.batch, "BOOM", 0, yBound - 50);
                enemyBullets.removeValue(bullet, true);
                player.damage();
                if(player.hp == 0) gameOver();
                else resetPlayer();
            }
            for(Bullet p : playerBullets){
                if (bullet.collideRect(p)) {
                    playerBullets.removeValue(bullet, true);
                    enemyBullets.removeValue(p, true);
                }
            }
        }

        // check walls
        for (Wall w : walls) {
            if (w.hp != -2 && bullet.collideRect(w)) {
                // only player bullets damage walls
                if (bullet.isPlayer()) {
                    playerBullets.removeValue(bullet, true);
                    w.damage();
                    if (w.hp == 0) {
                        // if damage = 0 disappear
                        wallLayer.getCell((int) (w.body.x / TILE_SIZE),
                                (int) (w.body.y / TILE_SIZE)).setTile(backgroundTile);
                        walls.removeValue(w, true);
                    }
                } else {
                    System.out.println(bullet);
                    enemyBullets.removeValue(bullet, true);
                }
            }
        }
    }

    /**
     * TODO
     * Enemy tank movement AI.
     * @param tank
     */
    private void moveEnemy(Tank tank){
        if(tank.isMoving() == true || tank.move()) return;

        int i;
        Direction d;
        i = random.nextInt(4);
        //System.out.println("moveenemy " + i);
        switch(i){
            case 0:
                d=Direction.UP;
                break;
            case 1:
                d=Direction.LEFT;
                break;
            case 2:
                d=Direction.RIGHT;
                break;
            case 3:
                d=Direction.DOWN;
                break;
            default:
                d=Direction.UP;
                break;
        }
        tank.setDirection(d);
        tank.move();
        //System.out.println(tank.moving);
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

        camera.update(); // re-compute the position of the camera

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
        // and update player
        drawTank(player);
        player.update(delta);

        // draw and update enemies
        for (Tank t : enemies) {
            drawTank(t);
            t.update(delta);
            moveEnemy(t);
        }

        // draw and update bullets
        // also calls collision detection on bullets
        for (Bullet b : playerBullets) {
            drawBullet(b);
            b.update(delta);
            collisionDetection(b);
        }
        for (Bullet b : enemyBullets) {
            drawBullet(b);
            b.update(delta);
            collisionDetection(b);
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
//				enemies.first().shoot();
            }
        }

        // *** Testers ***
        // System.out.println(player.x + " " + player.y);
        // System.out.println(bullets.size);
        // fps.log();
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
