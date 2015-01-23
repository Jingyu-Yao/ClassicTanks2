package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

/**
 * Data class that describes a level.
 */
public class Level {
    final GameScreen gameScreen;

    private int levelNumber;
    private String levelString;
    private int startX, startY;

    // TODO: Find ways to make these private
    public Array<Enemy> enemies;
    public Array<Bullet> bullets;
    public Array<Wall> walls;
    public Player player;

    private TiledMap map;
    private TiledMapTileLayer backgroundLayer;
    private TiledMapTileLayer wallLayer;

    private TiledMapTile backgroundTile;

    /**
     * Also starts the {@code GameInputProcessor}
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

        enemies = new Array<Enemy>();
        walls = new Array<Wall>();
        bullets = new Array<Bullet>();

        levelString = "level" + levelNumber + ".tmx";

        // load tiled map
        gameScreen.game.assetManager.load(levelString, TiledMap.class);
        gameScreen.game.assetManager.finishLoading();

        map = gameScreen.game.assetManager.get(levelString);

        // get a default background grass tile to replace the wallz
        wallLayer = (TiledMapTileLayer) map.getLayers().get("Walls");
        backgroundLayer = (TiledMapTileLayer) (map.getLayers().get("Background"));
        backgroundTile = backgroundLayer.getCell(0, 0).getTile();

        makeWallFromTile();

        addObject(new Player(this, startX, startY, Tank.TankType.NORMAL, Direction.UP));

        addObject(new Enemy(this, 1, 5, Tank.TankType.FAST, Direction.RIGHT));
        addObject(new Enemy(this, 1, 6, Tank.TankType.ARMORED, Direction.RIGHT));
        addObject(new Enemy(this, 12, 3, Tank.TankType.NORMAL, Direction.RIGHT));

        Gdx.input.setInputProcessor(new GameInputProcessor(this));
    }

    /**
     * Creates {@code walls} from {@code wallLayer}
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
                    addObject(new Wall(this, i, j, prop));
                }
            }
        }
    }

    public TiledMap getMap() { return map; }

    /**
     * Add an object to the level
     * @param object the object to add
     */
    public void addObject(GameObj object){
        if(object instanceof Player){
            player = (Player) object;
        }else if(object instanceof Wall){
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
        }else if(object instanceof Enemy){
            enemies.add((Enemy) object);
        }else if(object instanceof Bullet){
            bullets.add((Bullet) object);
        }
    }

    /**
     * Removes the given object from this level.
     * @param object the object to remove
     */
    public void removeObject(GameObj object){
        if(object instanceof Player){
            gameOver();
        }else if(object instanceof Wall){
            walls.removeValue((Wall) object, true);
            wallLayer.getCell((int) (object.getX() / gameScreen.TILE_SIZE),
                    (int) (object.getY() / gameScreen.TILE_SIZE)).setTile(backgroundTile);
        }else if(object instanceof Enemy){
            enemies.removeValue((Enemy) object, true);
        }else if(object instanceof Bullet){
            bullets.removeValue((Bullet) object, true);
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
