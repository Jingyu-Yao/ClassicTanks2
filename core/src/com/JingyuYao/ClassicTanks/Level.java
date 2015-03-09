package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Map;
import java.util.Random;

/**
 * Data class that describes a level.
 */
public class Level {

    /*
    GLOBAL CONSTANTS
     */
    public static final Random RANDOM = new Random();
    public static final int TILE_SIZE = 32;
    public static final float SHINY_CHANCE = 0.15f;
    /*
    Meta data
     */
    private final int levelNumber;
    private final String assetName;

    private final AssetManager assetManager;
    private final TextureAtlas textureAtlas;
    private final BitmapFont font;
    protected final Viewport viewPort;
    private final SpriteBatch batch;

    /*
    Level data
     */
    // Layers: Walls, Background, Base, Spawns, Start, Enemies
    private final TiledMap map;
    private final TiledMapTileLayer wallLayer;

    private final Stage stage;
    private final Player player;

    private final Array<GameObj> spawnPositions;
    private final Vector2 startPosition;
    private final Vector2 basePosition;

    private final Array<Enemy> remainingEnemies;
    private int numEnemiesOnMap;

    private long spawnInterval;
    private int spawnLimit;
    private long lastSpawn;

    private boolean levelLost;
    private boolean levelComplete;

    /*
    Level stat
     */
    private final LevelStat stat;
    private long startTime;

    /**
     * Also starts the {@code GameScreenKeyboardListener}
     *
     * @param levelNumber
     *
     */
    public Level(int levelNumber, AssetManager assetManager,
                 Viewport viewPort, BitmapFont font, SpriteBatch batch) {
        // Meta data
        this.levelNumber = levelNumber;
        this.assetName = "level" + levelNumber + ".tmx";
        this.viewPort = viewPort;
        this.assetManager = assetManager;
        this.textureAtlas = assetManager.get("sprites/texture-atlas.txt", TextureAtlas.class);
        this.font = font;
        this.batch = batch;

        // TiledMap setup
        this.assetManager.load(assetName, TiledMap.class);
        this.assetManager.finishLoading();
        System.out.println("Asset loaded: " + assetName);
        this.map = this.assetManager.get(assetName, TiledMap.class);

        // Data extraction loads objects into stage
        stage = new Stage();

        // Extract data from map
        wallLayer = extractWallData(map);
        basePosition = extractBaseData(map);
        spawnPositions = extractSpawnData(map);
        startPosition = extractStartPositionData(map);
        remainingEnemies = extractEnemyListData(map);

        // Misc.
        lastSpawn = 0l;
        numEnemiesOnMap = 0;
        levelLost = false;
        levelComplete = false;
        stat = new LevelStat();
        startTime = TimeUtils.nanoTime();

        stage.setViewport(viewPort);

        // Setup player
        player = new Player(this, startPosition.x, startPosition.y, Tank.TankType.NORMAL, GameObj.Direction.UP);
        addObject(player);
        stage.setKeyboardFocus(player);

        // Set input processor to stage
        Gdx.input.setInputProcessor(stage);

        System.out.println("Level loaded: " + levelNumber);
    }

    //************** Constructor Helpers ***************************************

    /**
     * Populates {@code walls} from given layer.
     */
    private TiledMapTileLayer extractWallData(TiledMap map) {
        TiledMapTileLayer wallLayer = (TiledMapTileLayer) map.getLayers().get("Walls");
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
        return wallLayer;
    }

    /**
     * Populates {@code bases} from given layer.
     */
    private Vector2 extractBaseData(TiledMap map) {
        Vector2 basePosition = null;
        TiledMapTileLayer baseLayer = (TiledMapTileLayer) map.getLayers().get("Base");
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < baseLayer.getWidth(); i++) {
            for (int j = 0; j < baseLayer.getHeight(); j++) {
                cell = baseLayer.getCell(i, j);
                if (cell != null) {
                    addObject(new Base(this, i, j, TILE_SIZE, TILE_SIZE));
                    basePosition = new Vector2(i*TILE_SIZE,j*TILE_SIZE);
                }
            }
        }
        return basePosition;
    }

    /**
     * Populates {@code spawnPositions} from given layer.
     * Also sets spawnInterval and spawnLimit
     */
    private Array<GameObj> extractSpawnData(TiledMap map) {
        Array<GameObj> spawnPositions = new Array<GameObj>();
        TiledMapTileLayer spawnLayer = (TiledMapTileLayer) map.getLayers().get("Spawns");
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < spawnLayer.getWidth(); i++) {
            for (int j = 0; j < spawnLayer.getHeight(); j++) {
                cell = spawnLayer.getCell(i, j);
                if (cell != null) {
                    spawnPositions.add(new GameObj(this, null, i, j, TILE_SIZE, TILE_SIZE));
                }
            }
        }

        MapProperties properties = spawnLayer.getProperties();
        spawnInterval = Long.parseLong(properties.get("SpawnInterval", String.class)) * 1000000000l;
        spawnLimit = Integer.parseInt(properties.get("SpawnLimit", String.class));

        return spawnPositions;
    }

    private Vector2 extractStartPositionData(TiledMap map) {
        TiledMapTileLayer startLayer = (TiledMapTileLayer) map.getLayers().get("Start");
        TiledMapTileLayer.Cell cell;
        for (int i = 0; i < startLayer.getWidth(); i++) {
            for (int j = 0; j < startLayer.getHeight(); j++) {
                cell = startLayer.getCell(i, j);
                if (cell != null) {
                    return new Vector2(i, j);
                }
            }
        }
        return null;
    }

    private Array<Enemy> extractEnemyListData(TiledMap map) {
        TiledMapTileLayer enemiesLayer = (TiledMapTileLayer) map.getLayers().get("Enemies");
        MapProperties properties = enemiesLayer.getProperties();
        int numNormal = Integer.parseInt(properties.get("Normal", String.class));
        int numBarrage = Integer.parseInt(properties.get("Barrage", String.class));
        int numDual = Integer.parseInt(properties.get("Dual", String.class));
        int numArmored = Integer.parseInt(properties.get("Armored", String.class));
        int numFast = Integer.parseInt(properties.get("Fast", String.class));

        Array<Enemy> remainingEnemies = new Array<Enemy>();

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

        return remainingEnemies;
    }

    //************************** Getters *******************************

    public TiledMap getMap() {
        return map;
    }

    public Vector2 getBasePosition() { return basePosition; }

    public Vector2 getStartPosition() {
        return startPosition;
    }

    public LevelStat getStat(){
        return stat;
    }

    public boolean isLevelLost(){
        return levelLost;
    }

    public AssetManager getAssetManager() { return assetManager; }

    public TextureAtlas getTextureAtlas() { return textureAtlas; }

    //************************* Level maintenance ******************

    /**
     * Tries to spawn a {@code Enemy} from the to-spawn list at a RANDOM spawn location
     * if {@code spawnInterval} nanoseconds has passed.
     */
    public void spawn() {
        long curTime = TimeUtils.nanoTime();
        if (curTime - lastSpawn > spawnInterval &&
                remainingEnemies.size > 0 &&
                numEnemiesOnMap < spawnLimit) {
            GameObj spawnPoint = spawnPositions.get(RANDOM.nextInt(spawnPositions.size));
            addObject(spawnPoint); //Add to stage so collision will work
            if (spawnPoint.collideAll(0.0f, 0.0f) == null) {
                Enemy toAdd = remainingEnemies.random();
                if(RANDOM.nextFloat() <= SHINY_CHANCE){
                    toAdd.setShiny();
                }
                remainingEnemies.removeValue(toAdd, true);

                toAdd.setX(spawnPoint.getX());
                toAdd.setY(spawnPoint.getY());
                addObject(toAdd);
                lastSpawn = curTime;
            }
            removeObject(spawnPoint);
        }
    }

    /**
     * Add an object to the level
     *
     * @param object the object to add
     */
    public void addObject(GameObj object) {
        stage.addActor(object);
        if(object.getGameObjType() == GameObj.GameObjType.ENEMY){
            numEnemiesOnMap++;
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
                handleEnemyDeath((Enemy) object);
                break;
            case PLAYER:
                levelLost();
                break;
            case TANK:
                break;
            case BULLET:
                break;
            case WALL:
                wallLayer.setCell((int) (object.getX() / TILE_SIZE),
                        (int) (object.getY() / TILE_SIZE), null);
                stat.wallKills++;
                break;
            case BASE:
                levelLost();
                break;
        }
        object.remove();
    }

    private void handleEnemyDeath(Enemy enemy){
        numEnemiesOnMap--;
        switch(enemy.getTankType()){
            case NORMAL:
                stat.normalKills++;
                break;
            case BARRAGE:
                stat.barrageKills++;
                break;
            case DUAL:
                stat.dualKills++;
                break;
            case FAST:
                stat.fastKills++;
                break;
            case ARMORED:
                stat.armoredKills++;
                break;
        }
        if(enemy.isShiny()){
            //TODO: spawn different types of buff
            Buff tb = new Buff(this, 0, 0);
            tb.setPosition(enemy.getX(), enemy.getY());
            addObject(tb);
        }
        if(remainingEnemies.size == 0 && numEnemiesOnMap == 0){
            levelComplete();
        }
    }

    //************************** Stage functions *************************

    /**
     * Advance the state of the level, both the model and the view.
     * @param delta change in time
     */
    public void actLevel(float delta){
        stage.act(delta);
    }

    public void drawLevel(){
        stage.draw();
        batch.begin();
        font.drawMultiLine(batch, "Enemies remaining: " + remainingEnemies.size + "\n"
                + "Enemies on map: " + numEnemiesOnMap + "\n"
                + "HP: " + player.getHp(),
                0, viewPort.getScreenHeight()- font.getScaleY());
        batch.end();
    }

    //****************** Level conditions ******************

    /**
     * Check whether the level is complete or not. If conditions are met, call
     * {@code levelLost()}
     */
    public boolean isLevelEnded() {
        return levelLost || levelComplete;
    }

    private void levelComplete(){
        levelComplete = true;
        fillEndGameStat();
    }

    private void levelLost(){
        levelLost = true;
        fillEndGameStat();
    }

    //******************** Misc. *************************************

    private void fillEndGameStat(){
        stat.currentPlayerType = player.getTankType();
        stat.lifeLeft = player.getHp();
        stat.won = levelComplete;
        stat.levelDuration = TimeUtils.timeSinceNanos(startTime);
        stat.levelNumber = levelNumber;
    }

    /**
     * Unloads the level file from {@code assetManager} and disposes {@code map}
     */
    public void dispose() {
        System.out.println("Level dispose");
        if(assetManager.isLoaded(assetName)) {
            assetManager.unload(assetName);
            System.out.println("Asset disposed: " + assetName);
        }

        stage.dispose();
    }

    @Override
    public String toString() {
        return "Level{" +
                "remainingEnemies.size=" + remainingEnemies.size +
                ", startPosition=" + startPosition +
                ", levelNumber=" + levelNumber +
                ", spawnInterval=" + spawnInterval +
                ", lastSpawn=" + lastSpawn +
                '}';
    }
}
