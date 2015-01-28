package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
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
    public Array<GameObj> spawnPoints;
    public Array<Enemy> remainingEnemies;
    public Player player;
    private Vector2 start;
    private int levelNumber;
    private String levelString;
    private long spawnInterval;
    private long lastSpawn;
    /*
    A map have the following layers:
    Walls, Background, Base, Spawns, Start, Enemies
     */
    private TiledMap map;
    private TiledMapTileLayer backgroundLayer;
    private TiledMapTileLayer wallLayer;
    private TiledMapTileLayer baseLayer;
    private TiledMapTileLayer spawnLayer;
    private TiledMapTileLayer startLayer;
    private TiledMapTileLayer enemiesLayer;
    private TiledMapTile backgroundTile;

    //Number of enemies of each type remain to spawn

    /**
     * Also starts the {@code GameInputProcessor}
     *
     * @param levelNumber
     * @param gameScreen
     */
    public Level(int levelNumber, GameScreen gameScreen) {
        this.levelNumber = levelNumber;
        this.gameScreen = gameScreen;
        spawnInterval = 7000000000l; //7s
        lastSpawn = 0l;

        enemies = new Array<Enemy>();
        walls = new Array<Wall>();
        bullets = new Array<Bullet>();
        bases = new Array<Base>();
        spawnPoints = new Array<GameObj>();
        remainingEnemies = new Array<Enemy>();

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
        startLayer = (TiledMapTileLayer) map.getLayers().get("Start");
        enemiesLayer = (TiledMapTileLayer) map.getLayers().get("Enemies");

        backgroundTile = backgroundLayer.getCell(0, 0).getTile();

        makeWallFromTileLayer(wallLayer);
        makeBaseFromTileLayer(baseLayer);
        makeSpawnPointsFromTileLayer(spawnLayer);
        getStartPoint(startLayer);
        populateEnemiesList(enemiesLayer);

        addObject(new Player(this, start.x, start.y, Tank.TankType.NORMAL, Direction.UP));

        Gdx.input.setInputProcessor(new GameInputProcessor(this));
    }

    /**
     * Populates {@code walls} from given layer.
     *
     * @param layer the layer used to populate {@code walls} with
     */
    private void makeWallFromTileLayer(TiledMapTileLayer layer) {
        TiledMapTileLayer.Cell cell;
        String type;
        for (int i = 0; i < layer.getWidth(); i++) {
            for (int j = 0; j < layer.getHeight(); j++) {
                cell = layer.getCell(i, j);
                if (cell != null) {
                    type = cell.getTile().getProperties().get("type", String.class);
                    if(type != null) {
                        switch (type) {
                            case "NORMAL":
                                addObject(new Wall(this, i, j, Wall.WallType.NORMAL));
                                break;
                            case "WATER":
                                addObject(new Wall(this, i, j, Wall.WallType.WATER));
                                break;
                            case "CONCRETE":
                                addObject(new Wall(this, i, j, Wall.WallType.CONCRETE));
                                break;
                            case "INDESTRUCTIBLE":
                                addObject(new Wall(this, i, j, Wall.WallType.INDESTRUCTIBLE));
                                break;
                        }
                    }
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
     * Populates {@code spawnPoints} from given layer.
     *
     * @param layer the layer used to populate {@code spawnPoints} with
     */
    private void makeSpawnPointsFromTileLayer(TiledMapTileLayer layer) {
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < layer.getWidth(); i++) {
            for (int j = 0; j < layer.getHeight(); j++) {
                cell = layer.getCell(i, j);
                if (cell != null) {
                    spawnPoints.add(new GameObj(this, i, j, TILE_SIZE, TILE_SIZE));
                }
            }
        }
    }

    private void getStartPoint(TiledMapTileLayer layer) {
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < layer.getWidth(); i++) {
            for (int j = 0; j < layer.getHeight(); j++) {
                cell = layer.getCell(i, j);
                if (cell != null) {
                    start = new Vector2(i, j);
                    return;
                }
            }
        }
    }

    private void populateEnemiesList(TiledMapTileLayer layer) {
        MapProperties properties = layer.getProperties();
        int numNormal = Integer.parseInt(properties.get("Normal", String.class));
        int numBarrage = Integer.parseInt(properties.get("Barrage", String.class));
        int numDual = Integer.parseInt(properties.get("Dual", String.class));
        int numArmored = Integer.parseInt(properties.get("Armored", String.class));
        int numFast = Integer.parseInt(properties.get("Fast", String.class));

        for(int i = 0; i < numNormal; i++){
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.NORMAL));
        }
        for(int i = 0; i < numBarrage; i++){
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.BARRAGE));
        }
        for(int i = 0; i < numDual; i++){
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.DUAL));
        }
        for(int i = 0; i < numArmored; i++){
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.ARMORED));
        }
        for(int i = 0; i < numFast; i++){
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.FAST));
        }
    }

    /**
     * Tries to spawn a {@code Enemy} from the to-spawn list at a random spawn location
     * if {@code spawnInterval} nanoseconds has passed.
     */
    public void spawn() {
        long curTime = TimeUtils.nanoTime();
        if (curTime - lastSpawn > spawnInterval &&
                remainingEnemies.size > 0) {
            GameObj spawnPoint = spawnPoints.get(random.nextInt(spawnPoints.size));
            if (spawnPoint.collideAll(0.0f, 0.0f) == null) {
                Enemy toAdd = remainingEnemies.random();
                remainingEnemies.removeValue(toAdd, true);

                toAdd.setX(spawnPoint.getX());
                toAdd.setY(spawnPoint.getY());
                addObject(toAdd);
                lastSpawn = curTime;
            }
        }
    }

    public TiledMap getMap() {
        return map;
    }

    public float getBaseX() {
        return bases.first().getX();
    }

    public float getBaseY() {
        return bases.first().getY();
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
            levelComplete();
        } else if (object instanceof Base) {
            levelComplete();
        }
    }

    /**
     * GG, currently gg = reset level to 1
     */
    public void levelComplete() {
        gameScreen.loadLevel(1);
    }

    /**
     * Unloads the level file from {@code assetManager} and disposes {@code map}
     */
    public void dispose() {
        gameScreen.game.assetManager.unload(levelString);
        map.dispose();
    }

    /**
     * Check whether the level is complete or not. If conditions are met, call
     * {@code levelComplete()}
     */
    public void checkLevelCompletion(){
        if(remainingEnemies.size == 0 && enemies.size == 0){
            levelComplete();
        }
    }

    @Override
    public String toString() {
        return "Level{" + "enemies.size=" + enemies.size +
                ", bullets.size=" + bullets.size +
                ", walls.size=" + walls.size +
                ", remainingEnemies.size=" + remainingEnemies.size +
                ", player info=" + player.toString() +
                ", start=" + start +
                ", levelNumber=" + levelNumber +
                ", spawnInterval=" + spawnInterval +
                ", lastSpawn=" + lastSpawn +
                '}';
    }
}
