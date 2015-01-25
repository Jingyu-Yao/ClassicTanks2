package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

/**
 * Data class that describes a level.
 */
public class Level {
    public static final Random random = new Random();
    public static final int TILE_SIZE = 32;
    final GameScreen gameScreen;
    // TODO: Find ways to make these private
    public Array<Enemy> enemies;
    public Array<Bullet> bullets;
    public Array<Wall> walls;
    public Array<Base> bases;
    public Array<GameObj> spawns;
    public Player player;
    private int levelNumber;
    private String levelString;
    private int startX, startY;
    private long spawnInterval;
    private long lastSpawn;
    private TiledMap map;
    private TiledMapTileLayer backgroundLayer;
    private TiledMapTileLayer wallLayer;
    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer spawnLayer;
    private TiledMapTile backgroundTile;

    /**
     * Also starts the {@code GameInputProcessor}
     *
     * @param levelNumber
     * @param startX
     * @param startY
     * @param gameScreen
     */
    public Level(int levelNumber, int startX, int startY, GameScreen gameScreen) {
        this.levelNumber = levelNumber;
        this.startX = startX;
        this.startY = startY;
        this.gameScreen = gameScreen;
        spawnInterval = 7000000000l; //7s
        lastSpawn = 0l;

        enemies = new Array<Enemy>();
        walls = new Array<Wall>();
        bullets = new Array<Bullet>();
        bases = new Array<Base>();
        spawns = new Array<GameObj>();

        levelString = "level" + levelNumber + ".tmx";

        // load tiled map
        gameScreen.game.assetManager.load(levelString, TiledMap.class);
        gameScreen.game.assetManager.finishLoading();

        map = gameScreen.game.assetManager.get(levelString);

        // get a default background grass tile to replace the wallz
        wallLayer = (TiledMapTileLayer) map.getLayers().get("Walls");
        backgroundLayer = (TiledMapTileLayer) (map.getLayers().get("Background"));
        baseLayer = (TiledMapTileLayer) map.getLayers().get("Base");
        spawnLayer = (TiledMapTileLayer) map.getLayers().get("Spawns");

        backgroundTile = backgroundLayer.getCell(0, 0).getTile();

        makeWallFromTileLayer(wallLayer);
        makeBaseFromTileLayer(baseLayer);
        makeSpawnPointsFromTileLayer(spawnLayer);

        addObject(new Player(this, startX, startY, Tank.TankType.NORMAL, Direction.UP));

        Gdx.input.setInputProcessor(new GameInputProcessor(this));
    }

    /**
     * Populates {@code walls} from given layer.
     *
     * @param layer the layer used to populate {@code walls} with
     */
    private void makeWallFromTileLayer(TiledMapTileLayer layer) {
        TiledMapTileLayer.Cell cell;
        int prop;
        for (int i = 0; i < layer.getWidth(); i++) {
            for (int j = 0; j < layer.getHeight(); j++) {
                cell = layer.getCell(i, j);
                if (cell != null) {
                    prop = Integer.parseInt(cell.getTile().getProperties()
                            .get("hp", String.class));
                    addObject(new Wall(this, i, j, prop));
                }
            }
        }
    }

    /**
     * Populates {@code bases} from given layer.
     *
     * @param layer the layer used to populate {@code bases} with
     */
    private void makeBaseFromTileLayer(TiledMapTileLayer layer) {
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < layer.getWidth(); i++) {
            for (int j = 0; j < layer.getHeight(); j++) {
                cell = layer.getCell(i, j);
                if (cell != null) {
                    bases.add(new Base(this, i, j, TILE_SIZE, TILE_SIZE));
                }
            }
        }
    }

    /**
     * Populates {@code spawns} from given layer.
     *
     * @param layer the layer used to populate {@code spawns} with
     */
    private void makeSpawnPointsFromTileLayer(TiledMapTileLayer layer) {
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < layer.getWidth(); i++) {
            for (int j = 0; j < layer.getHeight(); j++) {
                cell = layer.getCell(i, j);
                if (cell != null) {
                    spawns.add(new GameObj(this, i, j, TILE_SIZE, TILE_SIZE));
                }
            }
        }
    }

    /**
     * Tries to spawn a {@code Enemy} at a random spawn location if {@code spawnInterval} nanoseconds
     * has passed.
     */
    public void spawn() {
        long curTime = TimeUtils.nanoTime();
        if (curTime - lastSpawn > spawnInterval) {
            GameObj spawnPoint = spawns.get(random.nextInt(spawns.size));
            if (spawnPoint.collideAll(0.0f, 0.0f) == null) {
                addObject(new Enemy(this, spawnPoint.getGridX(), spawnPoint.getGridY(),
                        Tank.getRandomTankType(), GameObj.getRandomDirection()));
                lastSpawn = curTime;
            }
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public float getBaseX(){
        return spawns.first().getX();
    }

    public float getBaseY(){
        return spawns.first().getY();
    }

    /**
     * Add an object to the level
     *
     * @param object the object to add
     */
    public void addObject(GameObj object) {
        if (object instanceof Bullet) {
            bullets.add((Bullet) object);
        } else if (object instanceof Player) {
            player = (Player) object;
        } else if (object instanceof Wall) {
            walls.add((Wall) object);
            // TODO: Add a cell to the wall layer
            /*
            if(wallLayer.getCell((int) (object.getX() / gameScreen.TILE_SIZE),
                    (int) (object.getY() / gameScreen.TILE_SIZE)) == null){

                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(backgroundTile);

                wallLayer.setCell((int) (object.getX() / gameScreen.TILE_SIZE),
                        (int) (object.getY() / gameScreen.TILE_SIZE), cell);
            }
            */
        } else if (object instanceof Enemy) {
            enemies.add((Enemy) object);
        }
    }

    /**
     * Removes the given object from this level.
     *
     * @param object the object to remove
     */
    public void removeObject(GameObj object) {
        if (object instanceof Bullet) {
            bullets.removeValue((Bullet) object, true);
        } else if (object instanceof Wall) {
            walls.removeValue((Wall) object, true);
            wallLayer.getCell((int) (object.getX() / GameScreen.TILE_SIZE),
                    (int) (object.getY() / GameScreen.TILE_SIZE)).setTile(backgroundTile);
        } else if (object instanceof Enemy) {
            enemies.removeValue((Enemy) object, true);
        } else if (object instanceof Player) {
            gameOver();
        } else if (object instanceof Base) {
            gameOver();
        }
    }

    /**
     * GG, currently gg = reset level to 1
     */
    public void gameOver() {
        gameScreen.loadLevel(1);
    }

    /**
     * Unloads the level file from {@code assetManager} and disposes {@code map}
     */
    public void dispose() {
        gameScreen.game.assetManager.unload(levelString);
        map.dispose();
    }
}
