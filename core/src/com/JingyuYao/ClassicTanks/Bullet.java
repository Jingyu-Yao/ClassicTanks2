package com.JingyuYao.ClassicTanks;

public class Bullet extends GameObj {

    static final float HEIGHT = 8f;
    static final float WIDTH = 4f;
    static final float BULLET_SPEED = 300f;

    // Properties
    private Tank origin;
    private BulletType type;

    /**
     * @param x
     * @param y
     * @param direction
     * @param origin    the source of the bullet
     */
    public Bullet(Level level, float x, float y, Direction direction, Tank origin, BulletType type) {
        super(level, x / GameScreen.TILE_SIZE, y / GameScreen.TILE_SIZE, HEIGHT, WIDTH, BULLET_SPEED, direction);
        this.origin = origin;
        setType(type);
        setProperRecBound();
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

    public void setType(BulletType type){
        this.type = type;
    }

    public BulletType getType(){
        return type;
    }

    /**
     * Updates the bullet's location. Also handles collision
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        GameObj result = collideAll(0.0f, 0.0f);

        if (result != null) {
            // If both objects are bullets, they cancel each other out
            if (result instanceof Bullet) {
                ((Bullet) result).removeSelf();
                removeSelf();
            }

            // If bullet is fired by the player
            if (this.getOrigin() instanceof Player) {
                // and it hits a wall
                if (result instanceof Wall) {
                    switch (((Wall) result).getType()){
                        case NORMAL:
                            result.damage();
                            removeSelf();
                            break;
                        case WATER:
                            break;
                        case CONCRETE:
                            if(getType() == BulletType.SUPER) {
                                result.damage();
                            }
                            removeSelf();
                            break;
                        case INDESTRUCTIBLE:
                            removeSelf();
                            break;
                    }
                } else {
                    result.damage();
                    removeSelf();
                }
            } else {
                // Enemy tank damages everything except other enemies
                if (result instanceof Enemy) {
                    removeSelf();
                } else if (result instanceof Wall) {
                    switch (((Wall) result).getType()){
                        case NORMAL:
                            result.damage();
                            removeSelf();
                            break;
                        case WATER:
                            break;
                        case CONCRETE:
                            if(getType() == BulletType.SUPER) {
                                result.damage();
                            }
                            removeSelf();
                            break;
                        case INDESTRUCTIBLE:
                            removeSelf();
                            break;
                    }
                } else {
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
        getLevel().removeObject(this);
        origin.addBullet();
    }

    public enum BulletType{
        NORMAL,
        SUPER
    }
}
