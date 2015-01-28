package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Rectangle;

/**
 * Base class for all objects in the game.
 * Created by Jingyu_Yao on 1/15/2015.
 */
public class GameObj {

    private final Level level;
    private Rectangle body;
    private Direction direction;
    private float velocity;
    private int hp;

    /**
     * Create a GameObj in a {@code Level}.
     * @param level the {@code Level} this object belongs in
     * @param x the x coordinate of the object in grid location
     * @param y the y coordinate of the object in grid location
     * @param width the width of the object
     * @param height the height of the object
     */
    public GameObj(Level level, float x, float y, float width, float height) {
        this.level = level;
        body = new Rectangle(x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE, width, height);
        direction = Direction.NONE;
        velocity = 0.0f;
        hp = 1;
    }

    /**
     * Create a GameObj in a {@code Level}.
     * @param level the {@code Level} this object belongs in
     * @param x the x coordinate of the object in grid location
     * @param y the y coordinate of the object in grid location
     * @param width the width of the object
     * @param height the height of the object
     * @param velocity the velocity of the object
     * @param direction the direction of the object
     */
    public GameObj(Level level, float x, float y, float width, float height, float velocity, Direction direction) {
        this.level = level;
        body = new Rectangle(x * GameScreen.TILE_SIZE, y * GameScreen.TILE_SIZE, width, height);
        this.direction = direction;
        this.velocity = velocity;
        hp = 1;
    }

    //Getters
    public Direction getDirection() {
        return direction;
    }

    /**
     * @return a evenly distributed random direction
     */
    public static Direction getRandomDirection() {
        switch (Level.random.nextInt(4)) {
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

    public float getX() {
        return body.x;
    }

    public void setX(float x) {
        body.x = x;
    }

    public int getGridX() {
        return (int) body.x / Level.TILE_SIZE;
    }

    public float getY() {
        return body.y;
    }

    public void setY(float y) {
        body.y = y;
    }

    public int getGridY() {
        return (int) body.y / Level.TILE_SIZE;
    }

    public float getWidth() {
        return body.width;
    }

    public void setWidth(float width) {
        body.width = width;
    }

    public float getHeight() {
        return body.height;
    }

    public void setHeight(float height) {
        body.height = height;
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
        for (int i = 0; i < level.bases.size; i++) {
            GameObj base = level.bases.get(i);
            result = this.collideObj(dx, dy, base);
            if (result != null) {
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

    @Override
    public String toString() {
        return "GameObj{" +
                ", body=" + body +
                ", direction=" + direction +
                ", velocity=" + velocity +
                ", hp=" + hp +
                '}';
    }
}
