package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Bullet extends GameObj {

    static final float HEIGHT = 8f;
    static final float WIDTH = 4f;
    static final float BULLET_SPEED = 300f;

    // Properties
    private final Tank origin;
    private final BulletType bulletType;

    /**
     * @param x
     * @param y
     * @param direction
     * @param origin    the source of the bullet
     */
    public Bullet(Level level, Sprite sprite, float x, float y, Direction direction, Tank origin, BulletType bulletType) {
        super(level, sprite, x / Level.TILE_SIZE, y / Level.TILE_SIZE, HEIGHT, WIDTH, BULLET_SPEED, direction);
        this.origin = origin;
        this.bulletType = bulletType;
        setProperRecBound();
        gameObjType = GameObjType.BULLET;
    }

    /**
     * Set the width and height of the bullet based on its direction.
     */
    private void setProperRecBound() {
        switch (getDirection()) {
            case UP:
            case DOWN:
                setWidth(WIDTH);
                setHeight(HEIGHT);
                break;
            case LEFT:
            case RIGHT:
                // Fall through
                // Intentional
                setWidth(HEIGHT);
                setHeight(WIDTH);
                break;
        }
    }

    // Getter / Setter
    public Tank getOrigin() {
        return origin;
    }

    public BulletType getBulletType() {
        return bulletType;
    }

    /**
     * Updates the bullet's location. Also handles collision
     *
     * @param deltaTime
     */
    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);
        GameObj result = collideAll(0.0f, 0.0f);

        if (result != null) {
            // If both objects are bullets, they cancel each other out
            if (result instanceof Bullet) {
                ((Bullet) result).removeSelf();
                removeSelf();
            }

            // If bullet is fired by the player
            if (origin.gameObjType == GameObjType.PLAYER) {
                // and it hits a wall
                if (result.getGameObjType() == GameObjType.WALL) {
                    switch (((Wall) result).getWallType()) {
                        case NORMAL:
                            result.damage();
                            removeSelf();
                            break;
                        case WATER:
                            break;
                        case CONCRETE:
                            if (getBulletType() == BulletType.SUPER) {
                                result.damage();
                            }
                            removeSelf();
                            break;
                        case INDESTRUCTIBLE:
                            removeSelf();
                            break;
                    }
                } else if (result.getGameObjType() != GameObjType.BUFF){
                    result.damage();
                    removeSelf();
                }
            } else {
                // Enemy tank damages everything except other enemies
                if (result.getGameObjType() == GameObjType.ENEMY) {
                    removeSelf();
                } else if (result.getGameObjType() == GameObjType.WALL) {
                    switch (((Wall) result).getWallType()) {
                        case NORMAL:
                            result.damage();
                            removeSelf();
                            break;
                        case WATER:
                            break;
                        case CONCRETE:
                            if (getBulletType() == BulletType.SUPER) {
                                result.damage();
                            }
                            removeSelf();
                            break;
                        case INDESTRUCTIBLE:
                            removeSelf();
                            break;
                    }
                } else if (result.getGameObjType() != GameObjType.BUFF){
                    result.damage();
                    removeSelf();
                }
            }
        }
    }

    /**
     * Remove self from bullets array of level and reduce origin's bullet
     * count by one.
     */
    private void removeSelf() {
        //same as super.damage()
        //getLevel().removeObject(this);
        remove();
        origin.addBullet();
    }

    public static enum BulletType {
        NORMAL,
        SUPER
    }
}
