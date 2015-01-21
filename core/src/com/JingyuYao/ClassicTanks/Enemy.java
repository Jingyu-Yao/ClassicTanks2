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
        if (hp == 0) {
            level.enemies.removeValue(this, true);
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
        if (moving == true || forward()) return;

        int i;
        Direction d;
        i = random.nextInt(4);
        //System.out.println("moveenemy " + i);
        switch (i) {
            case 0:
                d = Direction.UP;
                break;
            case 1:
                d = Direction.LEFT;
                break;
            case 2:
                d = Direction.RIGHT;
                break;
            case 3:
                d = Direction.DOWN;
                break;
            default:
                d = Direction.UP;
                break;
        }
        direction = d;
        forward();
    }
}
