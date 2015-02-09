package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

/**
 * Base class for all objects in the game.
 * Created by Jingyu_Yao on 1/15/2015.
 */
public class GameObj extends Actor {

    /*
    Meta data
     */
    private final Level level;
    protected GameObjType gameObjType;
    protected Sprite sprite;

    /*
    Object data
     */
    protected Direction direction;
    protected float velocity;
    protected int hp;

    /**
     * Create a GameObj in a {@code Level}.
     *  @param level  the {@code Level} this object belongs in
     * @param x      the x coordinate of the object in grid location
     * @param y      the y coordinate of the object in grid location
     * @param width  the width of the object
     * @param height the height of the object
     */
    public GameObj(Level level, Sprite sprite, float x, float y, float width, float height) {
        this.level = level;
        setBounds(x * Level.TILE_SIZE, y * Level.TILE_SIZE, width, height);
        direction = Direction.NONE;
        velocity = 0.0f;
        hp = 1;
        gameObjType = GameObjType.GAMEOBJ;
        this.sprite = sprite;
    }

    /**
     * Create a GameObj in a {@code Level}.
     *  @param level     the {@code Level} this object belongs in
     * @param x         the x coordinate of the object in grid location
     * @param y         the y coordinate of the object in grid location
     * @param width     the width of the object
     * @param height    the height of the object
     * @param velocity  the velocity of the object
     * @param direction the direction of the object
     */
    public GameObj(Level level, Sprite sprite, float x, float y, float width, float height, float velocity, Direction direction) {
        this(level, sprite, x, y, width, height);
        this.direction = direction;
        this.velocity = velocity;
    }

    //Getters
    public Direction getDirection() {
        return direction;
    }

    /**
     * @return a evenly distributed RANDOM direction
     */
    public static Direction getRandomDirection() {
        switch (Level.RANDOM.nextInt(4)) {
            case 0:
                return Direction.UP;
            case 1:
                return Direction.LEFT;
            case 2:
                return Direction.RIGHT;
            case 3:
                return Direction.DOWN;
            default:
                return Direction.UP;
        }
    }

    //Setters
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public int getGridX() {
        return (int) getX() / Level.TILE_SIZE;
    }

    public int getGridY() {
        return (int) getY() / Level.TILE_SIZE;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Level getLevel() {
        return level;
    }

    public GameObjType getGameObjType() {
        return gameObjType;
    }

    /**
     * Check collision with another GameObj
     *
     * @param obj the other {@code GameObj} to test for collision
     * @param dx  the change in x position of this object
     * @param dy  the change in y position of this object
     * @return the collided {@code GameObj} or {@code null} if no collision
     */
    public GameObj collideObj(float dx, float dy, GameObj obj) {
        if (!this.equals(obj)
                && Intersector.overlaps(new Rectangle(obj.getX(),
                        obj.getY(),
                        obj.getWidth(),
                        obj.getHeight()),
                new Rectangle(getX() + dx,
                        getY() + dy,
                        getWidth(),
                        getHeight()))) {
            return obj;
        }
        return null;
    }

    /**
     * Checks collision with all actors in this object's stage.
     *
     * @param dx the change in x position
     * @param dy the change in y position
     * @return the collided {@code GameObj} or {@code null} if no collision
     */
    public GameObj collideAll(float dx, float dy){
        if(getStage() == null){
            return null;
        }

        GameObj result = null;
        Array<Actor> actors = getStage().getActors();

        for(int i = 0; i < actors.size; i++){
            GameObj obj = (GameObj) actors.get(i);
            result = this.collideObj(dx, dy, obj);
            if (result != null) {
                return result;
            }
        }

        return result;
    }

    /**
     * Updates the object's position based on deltaTime.
     *
     * @param deltaTime
     */
    @Override
    public void act(float deltaTime) {
        float curMove = deltaTime * getVelocity();
        //change the distance
        switch (getDirection()) {
            case UP:
                setY(getY() + curMove);
                break;
            case DOWN:
                setY(getY() - curMove);
                break;
            case LEFT:
                setX(getX() - curMove);
                break;
            case RIGHT:
                setX(getX() + curMove);
                break;
        }
    }

    /**
     * Reduce object's hp by one
     */
    public void damage() {
        if(hp > 0){
            hp--;
        }
        if(hp == 0){
            level.removeObject(this);
        }
    }

    /**
     * Draw this object using its sprite in the correct orientation
     * @param batch
     * @param parentAlpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(sprite != null){
            sprite.setX(getX());
            sprite.setY(getY());
            switch (getDirection()) {
                case DOWN:
                    sprite.setRotation(180);
                    break;
                case LEFT:
                    sprite.setRotation(90);
                    break;
                case RIGHT:
                    sprite.setRotation(270);
                    break;
                case UP:
                    sprite.setRotation(0);
                    break;
            }
            sprite.draw(batch);
        }
    }

    @Override
    public String toString() {
        return "GameObj{" +
                ", direction=" + direction +
                ", velocity=" + velocity +
                ", hp=" + hp +
                '}';
    }

    public static enum GameObjType {
        GAMEOBJ,
        ENEMY,
        PLAYER,
        TANK,
        BULLET,
        WALL,
        BASE,
    }

    public static enum Direction {
        NONE,
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}
