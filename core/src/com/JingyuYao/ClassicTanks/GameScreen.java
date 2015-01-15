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
import com.badlogic.gdx.math.Rectangle;
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

        enemies.add(new Tank(this,32, 64, Tank.TankType.ARMORED, Direction.RIGHT, false));
    }

    public GameScreen(final ClassicTanks g, int l) {
        game = g;
        level = l;

        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        createSprites();
    }

    // Constructor help methods
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

    private void makeWallFromTile() {
        // walls.adddddd
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

    // TODO
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

    // Player control helper methods
    private void playerMove(Direction d) {
        if (player.direction == d)
            player.move();
        else {
            player.setDirection(d);
            lastDirectionTime = curTime;
        }
    }

    private boolean isPressed(int k) {
        switch (k) {
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
    private void drawTank(Tank t) {
        Sprite s = tankSprites.get(t.type);
        s.setX(t.x);
        s.setY(t.y);
        switch (t.direction) {
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

    private void drawBullet(Bullet b) {
        bulletSprite.setX(b.x);
        bulletSprite.setY(b.y);
        switch (b.direction) {
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

    // TODO
    // Collision!
    // i values: 0=enemy bullet, 1=player bullet
    private void collisionDetection(Bullet b) {
        if (b.player) {
            // check enemies
            for (Tank t : enemies) {
                if (b.overlaps(t)) {
                    game.font.draw(game.batch, "Enemy HIT", 0, yBound - 100);
                    t.damage();
                    if(t.hp == 0) enemies.removeValue(t, true);
                    playerBullets.removeValue(b, true);
                }
            }
            for(Bullet e : enemyBullets){
                if (b.overlaps(e)) {
                    playerBullets.removeValue(b, true);
                    enemyBullets.removeValue(e, true);
                }
            }
        } else {
            // check player
            if (b.overlaps(player)) {
                game.font.draw(game.batch, "BOOM", 0, yBound - 50);
                enemyBullets.removeValue(b, true);
                player.damage();
                if(player.hp == 0) gameOver();
                else resetPlayer();
            }
            for(Bullet p : playerBullets){
                if (b.overlaps(p)) {
                    playerBullets.removeValue(b, true);
                    enemyBullets.removeValue(p, true);
                }
            }
        }

        // check walls
        for (Wall w : walls) {
            if (w.hp != -2 && b.overlaps(w)) {
                // only player bullets damage walls
                if (b.player) {
                    playerBullets.removeValue(b, true);
                    w.damage();
                    if (w.hp == 0) {
                        // if damage = 0 disappear
                        wallLayer.getCell((int) (w.x / TILE_SIZE),
                                (int) (w.y / TILE_SIZE)).setTile(backgroundTile);
                        walls.removeValue(w, true);
                    }
                } else {
                    System.out.println(b);
                    enemyBullets.removeValue(b, true);
                }
            }
        }
    }

    private void moveEnemy(Tank t){
        if(t.moving == true) return;
        int i;
        Direction d;
        //check for valid directions
//		while(!t.moving){
        i = random.nextInt(4);
        System.out.println("moveenemy " + i);
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
        t.setDirection(d);
        t.move();
        System.out.println(t.moving);
//		}

    }

    private void resetPlayer(){
        player.x = startX;
        player.y = startY;
    }

    private void gameOver(){
        //TODO
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
