package com.JingyuYao.ClassicTanks;

import java.util.Random;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Enemy extends Tank {
    static final Random random = new Random();

    public Enemy(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, type, direction);
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
        shoot();
        if (getMoving() == true || forward()) return;

        int i;
        i = random.nextInt(4);
        //System.out.println("moveenemy " + i);
        switch (i) {
            case 0:
                setDirection(Direction.UP);
                break;
            case 1:
                setDirection(Direction.LEFT);
                break;
            case 2:
                setDirection(Direction.RIGHT);
                break;
            case 3:
                setDirection(Direction.DOWN);
                break;
            default:
                setDirection(Direction.UP);
                break;
        }
        forward();
    }
}
