package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.utils.TimeUtils;

@SuppressWarnings("serial")
public class Tank extends GameObj {

    // Constants
    static final float SIZE = GameScreen.TILE_SIZE;
    static final float HALF_SIZE = SIZE / 2f;
    static final float ONE_DISTANCE = HALF_SIZE; // pixels
    private float distanceLeft = ONE_DISTANCE;
    // Tank type defaults
    static final float DEFAULT_VELOCITY = 100f;
    static final long DEFAULT_FIRE_RATE = 1000000000l;
    static final long BARRAGE_FIRE_RATE = DEFAULT_FIRE_RATE / 3;
    static final long DUAL_FIRE_RATE = DEFAULT_FIRE_RATE / 5;
    static final float SUPER_VELOCITY = 175.0f;
    // Tank properties
    private TankType type;
    private Bullet.BulletType bulletType;
    private long lastBulletTime;
    private long fireRate; // 1s
    private int numBulletsOut;
    private int maxBullets;
    private boolean shooting;
    // Variables for movement
    private Direction moveTowards;
    private boolean moving;
    private float target;

    // Constructors
    public Tank(Level level, float x, float y, TankType type) {
        super(level, x, y, SIZE, SIZE, DEFAULT_VELOCITY, getRandomDirection());
        setType(type);
        moving = false;
        lastBulletTime = 0l;
        fireRate = DEFAULT_FIRE_RATE;
        numBulletsOut = 0;
        maxBullets = 1;
        moveTowards = Direction.NONE;
        shooting = false;
        bulletType = Bullet.BulletType.NORMAL;
    }

    public Tank(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, SIZE, SIZE, DEFAULT_VELOCITY, direction);
        setType(type);
        moving = false;
        lastBulletTime = 0l;
        fireRate = DEFAULT_FIRE_RATE;
        numBulletsOut = 0;
        maxBullets = 1;
        moveTowards = Direction.NONE;
        shooting = false;
        bulletType = Bullet.BulletType.NORMAL;
    }

    public void addBullet() {
        numBulletsOut--;
    }

    // Getters
    public TankType getType() {
        return type;
    }

    /**
     * @return a evenly distributed TankType (except GM)
     */
    public static TankType getRandomTankType() {
        switch (Level.random.nextInt(6)) {
            case 0:
                return TankType.NORMAL;
            case 1:
                return TankType.BARRAGE;
            case 2:
                return TankType.DUAL;
            case 3:
                return TankType.FAST;
            case 4:
                return TankType.ARMORED;
            case 5:
                return TankType.SUPER;
            default:
                return TankType.NORMAL;
        }
    }

    // Setters
    public void setType(TankType t) {
        resetType();
        type = t;
        switch (type) {
            case NORMAL:
                break;
            case BARRAGE:
                fireRate = BARRAGE_FIRE_RATE;
                break;
            case DUAL:
                maxBullets = 2;
                break;
            case FAST:
                setVelocity(SUPER_VELOCITY);
                break;
            case ARMORED:
                setHp(3);
                break;
            case SUPER:
                fireRate = BARRAGE_FIRE_RATE;
                setHp(2);
                bulletType = Bullet.BulletType.SUPER;
                setVelocity(SUPER_VELOCITY);
                break;
            case GM:
                fireRate = DUAL_FIRE_RATE;
                bulletType = Bullet.BulletType.SUPER;
                setVelocity(SUPER_VELOCITY);
                setHp(1000); //Basically God mode...
                break;
        }
    }

    private void resetType(){
        setHp(1);
        bulletType = Bullet.BulletType.NORMAL;
        fireRate = DEFAULT_FIRE_RATE;
        setVelocity(DEFAULT_VELOCITY);
        maxBullets = 1;
    }

    public boolean getMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public Direction getMoveTowards() {
        return moveTowards;
    }

    public void moveTowards(Direction direction) {
        moveTowards = direction;
    }

    public void startShooting() {
        shooting = true;
    }

    public void stopShooting() {
        shooting = false;
    }

    /**
     * Fire a bullet iff {@code curTime - lastBulletTime < fireRate || numBulletsOut >= maxBullets}
     */
    public void shoot() {
        long curTime = TimeUtils.nanoTime();
        if (curTime - lastBulletTime > fireRate && numBulletsOut < maxBullets) {
            lastBulletTime = curTime;

            float bodyX = getX(), bodyY = getY();
            Direction direction = getDirection();
            Bullet bullet = new Bullet(getLevel(), -1, -1, direction, this, bulletType);
            switch (direction) {
                case DOWN:
                    bullet.setX(bodyX + HALF_SIZE - Bullet.WIDTH / 2f);
                    bullet.setY(bodyY - Bullet.HEIGHT);
                    break;
                case LEFT:
                    bullet.setX(bodyX - Bullet.HEIGHT);
                    bullet.setY(bodyY + HALF_SIZE - Bullet.WIDTH + 1);
                    break;
                case RIGHT:
                    bullet.setX(bodyX + SIZE + Bullet.WIDTH);
                    bullet.setY(bodyY + HALF_SIZE - Bullet.HEIGHT / 2f + 1);
                    break;
                case UP:
                    bullet.setX(bodyX + HALF_SIZE - Bullet.WIDTH / 2f);
                    bullet.setY(bodyY + SIZE);
                    break;
                case NONE:
                default:
                    bullet = null;
                    break;
            }
            if (bullet != null) {
                getLevel().addObject(bullet);
                numBulletsOut++;

                //special conditions for DUAL type
                //flips fire rate every bullet
                if(getType() == TankType.DUAL){
                    if(fireRate == DEFAULT_FIRE_RATE){
                        fireRate = DUAL_FIRE_RATE;
                    }else{
                        fireRate = DEFAULT_FIRE_RATE;
                    }
                }
            }
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
     * Updates the tanks position while preventing rounding error of final position.
     * Forward the tank if {@code moveTowards} has been set to anything besides {@code NONE}
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        if (moving) {
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
        } else if (moveTowards != Direction.NONE) {
            if (moveTowards != getDirection()) {
                setDirection(moveTowards);
            } else {
                forward();
            }
        }
        if (shooting) {
            shoot();
        }
    }

    /**
     * TODO: Use subclass instead of enum
     * All type of tanks.
     */
    public enum TankType {
        NORMAL, BARRAGE, // Fast bullets
        DUAL, // Dual shot
        FAST, // Fast movement
        ARMORED, // Extra health
        SUPER,
        GM, // 'God mode'
    }

    @Override
    public String toString() {
        return "Tank{" +
                ", type=" + type +
                ", lastBulletTime=" + lastBulletTime +
                ", fireRate=" + fireRate +
                ", numBulletsOut=" + numBulletsOut +
                ", maxBullets=" + maxBullets +
                ", shooting=" + shooting +
                ", moveTowards=" + moveTowards +
                ", moving=" + moving +
                "} ";
    }
}
