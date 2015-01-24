package com.JingyuYao.ClassicTanks;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Enemy extends Tank {

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
            moveTowards(getRandomDirection());
            forward();
        }
    }
}
