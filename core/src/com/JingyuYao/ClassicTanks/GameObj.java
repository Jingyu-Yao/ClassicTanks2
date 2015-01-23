package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.Type;

/**
 * Base class for all objects in the game.
 * Created by Jingyu_Yao on 1/15/2015.
 */
public class GameObj {
    private Rectangle body;
    private Direction direction;
    private float velocity;
    private int hp;
    private final Level level;

    public GameObj(Level level, float x, float y, float width, float height) {
        this.level = level;
        body = new Rectangle(x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE, width, height);
        direction = Direction.NONE;
        velocity = 0.0f;
        hp = 1;
    }

    public GameObj(Level level, float x, float y, float width, float height, float velocity) {
        this.level = level;
        body = new Rectangle(x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE, width, height);
        direction = Direction.NONE;
        this.velocity = velocity;
        hp = 1;
    }

    public GameObj(Level level, float x, float y, float width, float height, float velocity, Direction direction) {
        this.level = level;
        body = new Rectangle(x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE, width, height);
        this.direction = direction;
        this.velocity = velocity;
        hp = 1;
    }

    //Setters
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setX(float x) {
        body.x = x;
    }

    public void setY(float y) {
        body.y = y;
    }

    public void setWidth(float width) {
        body.width = width;
    }

    public void setHeight(float height) {
        body.height = height;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    //Getters
    public Direction getDirection() {
        return direction;
    }

    public float getVelocity() {
        return velocity;
    }

    public float getX() {
        return body.x;
    }

    public float getY() {
        return body.y;
    }

    public float getWidth() {
        return body.width;
    }

    public float getHeight() {
        return body.height;
    }

    public int getHp() {
        return hp;
    }

    public Level getLevel() { return level; }

    /**
     * Check collision with another GameObj
     *
     * @param obj the other {@code GameObj} to test for collision
     * @param dx  the change in x position of this object
     * @param dy  the change in y position of this object
     * @return the collided {@code GameObj} or {@code null} if no collision
     */
    public GameObj collideObj(float dx, float dy, GameObj obj) {
        if (!this.equals(obj) && obj.body.overlaps(new Rectangle(getX() + dx, getY() + dy, getWidth(), getHeight()))) {
            return obj;
        }
        return null;
    }

    /**
     * Checks collision all the walls, enemies, bullets and players in the level.
     *
     * @param dx the change in x position
     * @param dy the change in y position
     * @return the collided {@code GameObj} or {@code null} if no collision
     */
    public GameObj collideAll(float dx, float dy) {
        GameObj result = null;

        // Check collision against walls
        for (int i = 0; i < level.walls.size; i++) {
            Wall wall = level.walls.get(i);
            result = this.collideObj(dx, dy, wall);
            if (result != null) {
                return result;
            }
        }

        // Check collision with other tanks
        for (int i = 0; i < level.enemies.size; i++) {
            Enemy enemy = level.enemies.get(i);
            result = this.collideObj(dx, dy, enemy);
            if (result != null) {
                return result;
            }
        }

        // Check collision with bullets
        for (int i = 0; i < level.bullets.size; i++) {
            Bullet bullet = level.bullets.get(i);
            result = this.collideObj(dx, dy, bullet);
            if (result != null) {
                return result;
            }
        }

        // Check collision with bases
        for(int i = 0; i < level.bases.size; i++){
            GameObj base = level.bases.get(i);
            result = this.collideObj(dx,dy, base);
            if(result != null){
                return result;
            }
        }

        // Check collision with player
        return this.collideObj(dx, dy, level.player);
    }

    /**
     * Updates the object's position based on deltaTime.
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {
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
        setHp(getHp() - 1);
    }
}
