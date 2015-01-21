package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.utils.TimeUtils;

@SuppressWarnings("serial")
public class Tank extends GameObj {

    // Constants
    static final float SIZE = GameScreen.TILE_SIZE;
    static final float HALF_SIZE = SIZE / 2f;
    static final float ONE_DISTANCE = HALF_SIZE; // pixels

    // Tank properties
    private TankType type;
    private long lastBulletTime;
    private long fireRate; // 1s
    private int numBulletsLeft;
    private int maxBullets;

    // Variables for movement
    private boolean moving;
    private float target;
    private float distanceLeft = ONE_DISTANCE;

    // Constructors
    public Tank(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, SIZE, SIZE, 100f, direction);
        setType(type);
        moving = false;
        lastBulletTime = 0l;
        fireRate = 1000000000l;
        numBulletsLeft = 1;
        maxBullets = 1;
    }

    // Setters
    public void setType(TankType t) {
        type = t;
        switch (type) {
            case NORMAL:
                break;
            case BARRAGE:
                fireRate = 500000000l;
                break;
            case DUAL:
                break;
            case FAST:
                setVelocity(150f);
                break;
            case ARMORED:
                setHp(3);
                break;
            case GM:
                break;
        }
    }

    public void addBullet(){
        numBulletsLeft++;
    }

    // Getters
    public TankType getType() {
        return type;
    }

    public boolean getMoving() {
        return moving;
    }

    public void setMoving(boolean moving) { this.moving = moving; }

    /**
     * Fire a bullet iff {@code curTime - lastBulletTime < fireRate || numBulletsLeft >= maxBullets}
     */
    public void shoot() {
        long curTime = TimeUtils.nanoTime();
        if (curTime - lastBulletTime < fireRate || numBulletsLeft < maxBullets) {
            return;
        }

        lastBulletTime = curTime;

        float bodyX = getX(), bodyY = getY();
        Direction direction = getDirection();
        Bullet bullet;
        switch (direction) {
            case DOWN:
                bullet = new Bullet(getLevel(), bodyX + HALF_SIZE - Bullet.WIDTH / 2f, bodyY - Bullet.HEIGHT, direction, this);
                break;
            case LEFT:
                bullet = new Bullet(getLevel(), bodyX - Bullet.HEIGHT, bodyY + HALF_SIZE - Bullet.WIDTH + 1, direction, this);
                break;
            case RIGHT:
                bullet = new Bullet(getLevel(), bodyX + SIZE + Bullet.WIDTH, bodyY + HALF_SIZE - Bullet.HEIGHT / 2f + 1, direction, this);
                break;
            case UP:
                bullet = new Bullet(getLevel(), bodyX + HALF_SIZE - Bullet.WIDTH / 2f, bodyY + SIZE, direction, this);
                break;
            case NONE:
            default:
                bullet = null;
                break;
        }
        if (bullet != null) {
            getLevel().bullets.add(bullet);
            numBulletsLeft--;
        }
    }

    /**
     * Set a target position for this unit to move towards.
     * Also checks for collision with other objects before moving.
     *
     * @return
     */
    public boolean forward() {
        GameObj result = null;
        float bodyX = getX(), bodyY = getY();
        // set a target value to avoid rounding errors
        switch (getDirection()) {
            case DOWN:
                target = bodyY - ONE_DISTANCE;
                result = collideAll(0.0f, -ONE_DISTANCE);
                break;
            case LEFT:
                target = bodyX - ONE_DISTANCE;
                result = collideAll(-ONE_DISTANCE, 0.0f);
                break;
            case RIGHT:
                target = bodyX + ONE_DISTANCE;
                result = collideAll(ONE_DISTANCE, 0.0f);
                break;
            case UP:
                target = bodyY + ONE_DISTANCE;
                result = collideAll(0.0f, ONE_DISTANCE);
                break;
        }
        // This prevents tanks dodging bullets
        if (result == null || result instanceof Bullet) {
            setMoving(true);
        } else {
            setMoving(false);
        }
        return getMoving();
    }

    /**
     * Updates the tanks position while preventing rounding error of final position
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        if (getMoving()) {
            float curMove = deltaTime * getVelocity();
            distanceLeft -= curMove;
            if (distanceLeft < 0) {
                distanceLeft = ONE_DISTANCE;// reset
                setMoving(false);// finished moving

                // this prevents final coordinate rounding error
                switch (getDirection()) {
                    case UP:
                    case DOWN:
                        // Fall through
                        setY(target);
                        break;
                    case LEFT:
                    case RIGHT:
                        // Fall through
                        setX(target);
                        break;
                }
                return;
            }
            // change the distance
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
    }

    /**
     * TODO: Use subclass instead of enum
     * All type of tanks.
     */
    protected enum TankType {
        NORMAL, BARRAGE, // Fast bullets
        DUAL, // Dual shot
        FAST, // Fast movement
        ARMORED, // Extra health
        GM, // 'God mode'
    }

}
