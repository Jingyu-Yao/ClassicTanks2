package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Player extends Tank {
    long lastDirectionTime;
    long directionPauseTime;

    public Player(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, type, direction);
        lastDirectionTime = 0l;
        directionPauseTime = 50000000l;
    }

    /**
     * Move the player in the direction passed
     *
     * @param direction
     */
    public void moveTowards(Direction direction) {
        long curTime = TimeUtils.nanoTime();

        if (!getMoving() && curTime - lastDirectionTime > directionPauseTime) {
            if (getDirection() == direction) {
                forward();
            } else {
                setDirection(direction);
                lastDirectionTime = curTime;
            }
        }
    }

    @Override
    public void damage() {
        super.damage();
        if (getHp() == 0) {
            getLevel().removeObject(this);
        }
    }
}
