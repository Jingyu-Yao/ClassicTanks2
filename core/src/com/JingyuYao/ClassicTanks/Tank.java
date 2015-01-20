package com.JingyuYao.ClassicTanks;

@SuppressWarnings("serial")
public class Tank extends GameObj{

    // Constants
    static final float ONE_DISTANCE = 16f; // pixels
    static final float SIZE = 32f;
    static final float HALF_SIZE = SIZE / 2f;

    // Tank properties
    protected TankType type;

    // Variables for movement
    protected boolean moving;
    private float target;
    private float distanceLeft = ONE_DISTANCE;

    // Constructors
    public Tank(Level level,float x, float y, TankType type, Direction direction) {
        super(level, x, y, SIZE, SIZE, 100f, direction);
        this.type = type;
        moving = false;
        if(type == TankType.ARMORED){
            hp = 3;
        }
    }

    // Setters
    public void setType(TankType t) {
        type = t;
    }

    // Getters
    public TankType getType() {
        return type;
    }
    public boolean isMoving() {
        return moving;
    }

    /**
     * Makes a bullet based on the direction of the tank
     */
    public void shoot() {
        Bullet bullet;
        switch (direction) {
            case DOWN:
                bullet = new Bullet(level, body.x + HALF_SIZE - Bullet.WIDTH / 2f, body.y - Bullet.HEIGHT, direction, this);
                break;
            case LEFT:
                bullet = new Bullet(level, body.x-Bullet.HEIGHT, body.y+HALF_SIZE-Bullet.WIDTH+1, direction, this);
                break;
            case RIGHT:
                bullet = new Bullet(level, body.x+SIZE+Bullet.WIDTH, body.y+HALF_SIZE-Bullet.HEIGHT/2f+1, direction, this);
                break;
            case UP:
                bullet = new Bullet(level, body.x+HALF_SIZE-Bullet.WIDTH/2f, body.y+SIZE, direction, this);
                break;
            case NONE:
            default:
                bullet = null;
                break;
        }
        if(bullet != null){
            level.bullets.add(bullet);
        }
    }

    /**
     * Set a target position for this unit to move towards.
     * Also checks for collision with other objects before moving.
     * @return
     */
    public boolean move() {
        GameObj result = null;
        // set a target value to avoid rounding errors
        switch (direction) {
            case DOWN:
                target = body.y - ONE_DISTANCE;
                result = collideAll(0.0f, -ONE_DISTANCE);
                break;
            case LEFT:
                target = body.x - ONE_DISTANCE;
                result = collideAll(-ONE_DISTANCE, 0.0f);
                break;
            case RIGHT:
                target = body.x + ONE_DISTANCE;
                result = collideAll(ONE_DISTANCE, 0.0f);
                break;
            case UP:
                target = body.y + ONE_DISTANCE;
                result = collideAll(0.0f, ONE_DISTANCE);
                break;
        }
        // This prevents tanks dodging bullets
        if(result == null || result instanceof Bullet){
            moving = true;
        }else{
            moving = false;
        }
        return moving;
    }

    /**
     * Updates the tanks position while preventing rounding error of final position
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        if (moving) {
            float curMove = deltaTime * velocity;
            distanceLeft -= curMove;
            if (distanceLeft < 0) {
                distanceLeft = ONE_DISTANCE;// reset
                moving = false;// finished moving

                // this prevents final coordinate rounding error
                switch (direction) {
                    case DOWN:
                        this.body.y = target;
                        break;
                    case LEFT:
                        this.body.x = target;
                        break;
                    case RIGHT:
                        this.body.x = target;
                        break;
                    case UP:
                        this.body.y = target;
                        break;
                }
                return;
            }
            // change the distance
            switch (direction) {
                case DOWN:
                    body.y -= curMove;
                    break;
                case LEFT:
                    body.x -= curMove;
                    break;
                case RIGHT:
                    body.x += curMove;
                    break;
                case UP:
                    body.y += curMove;
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
