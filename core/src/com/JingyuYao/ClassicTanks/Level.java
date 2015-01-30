package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

/**
 * Data class that describes a level.
 */
public class Level {

    public static final Random random = new Random();

    public static final int TILE_SIZE = 32;
    static final int CAMERA_SIZE = TILE_SIZE * 20;
    static final int CAMERA_INNER_BOUND = CAMERA_SIZE / 8;
    static final float TILED_SCALE = 1f;
    public final GameScreen gameScreen;
    private final int levelNumber;
    private final String levelString;

    private Stage stage;
    private Array<GameObj> spawnPoints;
    private Array<Enemy> remainingEnemies;
    private int enemiesOnMap;
    private Player player;
    private Vector2 start;
    private long spawnInterval;
    private long lastSpawn;
    private float baseX, baseY;
    /*
    A map have the following layers:
    Walls, Background, Base, Spawns, Start, Enemies
     */
    private TiledMap map;
    private TiledMapTileLayer wallLayer;

    OrthographicCamera camera;
    Viewport viewPort;
    OrthogonalTiledMapRenderer tiledMapRenderer;

    /**
     * Also starts the {@code KeyboardInputListener}
     *
     * @param levelNumber
     * @param gameScreen
     */
    public Level(int levelNumber, GameScreen gameScreen) {
        this.levelNumber = levelNumber;
        this.gameScreen = gameScreen;
        spawnInterval = 7000000000l; //7s
        lastSpawn = 0l;
        spawnPoints = new Array<GameObj>();
        remainingEnemies = new Array<Enemy>();
        enemiesOnMap = 0;

        stage = new Stage();
        // create camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAMERA_SIZE, CAMERA_SIZE);
        viewPort = new ScreenViewport();
        viewPort.setCamera(camera);
        stage.setViewport(viewPort);

        levelString = "level" + levelNumber + ".tmx";

        // load tiled map
        this.gameScreen.loadAsset(levelString, TiledMap.class);

        map = this.gameScreen.getAsset(levelString);
        // set up tiled map renderer
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map, TILED_SCALE);

        // get a default background grass tile to replace the wallz
        makeWallFromTileLayer();
        makeBaseFromTileLayer();
        makeSpawnPointsFromTileLayer();
        getStartPoint();
        populateEnemiesList();

        player = new Player(this, start.x, start.y, Tank.TankType.NORMAL, GameObj.Direction.UP);

        addObject(player);
        stage.setKeyboardFocus(player);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Populates {@code walls} from given layer.
     */
    private void makeWallFromTileLayer() {
        wallLayer = (TiledMapTileLayer) map.getLayers().get("Walls");
        TiledMapTileLayer.Cell cell;
        String type;
        for (int i = 0; i < wallLayer.getWidth(); i++) {
            for (int j = 0; j < wallLayer.getHeight(); j++) {
                cell = wallLayer.getCell(i, j);
                if (cell != null) {
                    type = cell.getTile().getProperties().get("type", String.class);
                    if (type != null) {
                        // switch avoided here because libGdx wants java 6 compatibility
                        if (type.equals("NORMAL")) {
                            addObject(new Wall(this, i, j, Wall.WallType.NORMAL));
                        } else if (type.equals("WATER")) {
                            addObject(new Wall(this, i, j, Wall.WallType.WATER));
                        } else if (type.equals("CONCRETE")) {
                            addObject(new Wall(this, i, j, Wall.WallType.CONCRETE));
                        } else if (type.equals("INDESTRUCTIBLE")) {
                            addObject(new Wall(this, i, j, Wall.WallType.INDESTRUCTIBLE));
                        }
                    }
                }
            }
        }
    }

    /**
     * Populates {@code bases} from given layer.
     */
    private void makeBaseFromTileLayer() {
        TiledMapTileLayer baseLayer = (TiledMapTileLayer) map.getLayers().get("Base");
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < baseLayer.getWidth(); i++) {
            for (int j = 0; j < baseLayer.getHeight(); j++) {
                cell = baseLayer.getCell(i, j);
                if (cell != null) {
                    addObject(new Base(this, i, j, TILE_SIZE, TILE_SIZE));
                    baseX = i * TILE_SIZE;
                    baseY = j * TILE_SIZE;
                }
            }
        }
    }

    /**
     * Populates {@code spawnPoints} from given layer.
     */
    private void makeSpawnPointsFromTileLayer() {
        TiledMapTileLayer spawnLayer = (TiledMapTileLayer) map.getLayers().get("Spawns");
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < spawnLayer.getWidth(); i++) {
            for (int j = 0; j < spawnLayer.getHeight(); j++) {
                cell = spawnLayer.getCell(i, j);
                if (cell != null) {
                    spawnPoints.add(new GameObj(this, null, i, j, TILE_SIZE, TILE_SIZE));
                }
            }
        }
    }

    private void getStartPoint() {
        TiledMapTileLayer startLayer = (TiledMapTileLayer) map.getLayers().get("Start");
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < startLayer.getWidth(); i++) {
            for (int j = 0; j < startLayer.getHeight(); j++) {
                cell = startLayer.getCell(i, j);
                if (cell != null) {
                    start = new Vector2(i, j);
                    return;
                }
            }
        }
    }

    private void populateEnemiesList() {
        TiledMapTileLayer enemiesLayer = (TiledMapTileLayer) map.getLayers().get("Enemies");
        MapProperties properties = enemiesLayer.getProperties();
        int numNormal = Integer.parseInt(properties.get("Normal", String.class));
        int numBarrage = Integer.parseInt(properties.get("Barrage", String.class));
        int numDual = Integer.parseInt(properties.get("Dual", String.class));
        int numArmored = Integer.parseInt(properties.get("Armored", String.class));
        int numFast = Integer.parseInt(properties.get("Fast", String.class));

        for (int i = 0; i < numNormal; i++) {
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.NORMAL));
        }
        for (int i = 0; i < numBarrage; i++) {
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.BARRAGE));
        }
        for (int i = 0; i < numDual; i++) {
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.DUAL));
        }
        for (int i = 0; i < numArmored; i++) {
            remainingEnemies.add(new Enemy(this, -1, -1, Tank.TankType.ARMORED));
        }
        for (int i = 0; i < numFast; i++) {
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
        return baseX;
    }

    public float getBaseY() {
        return baseY;
    }

    /**
     * Add an object to the level
     *
     * @param object the object to add
     */
    public void addObject(GameObj object) {
        stage.addActor(object);
        if(object.getGameObjType() == GameObj.GameObjType.ENEMY){
            enemiesOnMap++;
        }
    }

    /**
     * Removes the given object from this level.
     *
     * @param object the object to remove
     */
    public void removeObject(GameObj object) {
        GameObj.GameObjType objType = object.getGameObjType();
        switch (objType) {
            case GAMEOBJ:
                break;
            case ENEMY:
                enemiesOnMap--;
                break;
            case PLAYER:
                levelComplete();
                break;
            case TANK:
                break;
            case BULLET:
                break;
            case WALL:
                wallLayer.setCell((int) (object.getX() / TILE_SIZE),
                        (int) (object.getY() / TILE_SIZE), null);
                break;
            case BASE:
                levelComplete();
                break;
        }
        object.remove();
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
        gameScreen.unloadAsset(levelString);
        map.dispose();
    }

    /**
     * Advance the state of the level, both the model and the view.
     * @param delta change in time
     */
    public void advanceTime(float delta){
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

        stage.act(delta);
        stage.draw();
        spawn();
        checkLevelCompletion();
    }

    /**
     * Check whether the level is complete or not. If conditions are met, call
     * {@code levelComplete()}
     */
    public void checkLevelCompletion() {
        if (remainingEnemies.size == 0 && enemiesOnMap == 0) {
            levelComplete();
        }
    }

    @Override
    public String toString() {
        return "Level{" +
                "remainingEnemies.size=" + remainingEnemies.size +
                ", player info=" + player.toString() +
                ", start=" + start +
                ", levelNumber=" + levelNumber +
                ", spawnInterval=" + spawnInterval +
                ", lastSpawn=" + lastSpawn +
                '}';
    }
}
