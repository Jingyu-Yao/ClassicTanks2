package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Vector3;
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
        directionPauseTime = 100000000l;
    }

    /**
     * Move the player in the direction passed
     * @param direction
     */
    public void moveTowards(Direction direction) {
        curTime = TimeUtils.nanoTime();

        if(!moving && curTime - lastDirectionTime > directionPauseTime) {
            if (this.direction == direction) {
                forward();
            } else {
                setDirection(direction);
                lastDirectionTime = curTime;
            }
        }
    }
}
