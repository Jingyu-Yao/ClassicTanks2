package com.JingyuYao.ClassicTanks;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Enemy extends Tank {

    private final static float BIASED_CHANCE = 0.6f;

    public Enemy(Level level, float x, float y, TankType type){
        super(level, x, y, type);
        startShooting();
    }

    public Enemy(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, type, direction);
        startShooting();
    }

    @Override
    public void damage() {
        super.damage();
        if (getHp() == 0) {
            getLevel().removeObject(this);
        }
    }

    /**
     * Updates this unit's position. If it is moving already, keep moving until collision occurs.
     * Else decide a new direction to move towards.
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        //Only find a new direction when this unit is not moving
        //This unit will stop moving only when it hits a wall
        if (!getMoving()) {
            moveTowards(getBiasedDirection());
            forward();
        }
    }

    /**
     * Return a new random direction that is biased towards the base
     * @return a new biased random direction
     */
    public Direction getBiasedDirection(){
        if(Level.random.nextFloat() < BIASED_CHANCE){
            // Half chance to adjust X or Y position
            if(Level.random.nextBoolean()){
                if(this.getX() < getLevel().getBaseX()){
                    return Direction.RIGHT;
                }else{
                    return Direction.LEFT;
                }
            }else{
                if(this.getY() < getLevel().getBaseY()){
                    return Direction.UP;
                }else{
                    return Direction.DOWN;
                }
            }
        }else {
            // Evenly distribute chance if no bias
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
    }
}
