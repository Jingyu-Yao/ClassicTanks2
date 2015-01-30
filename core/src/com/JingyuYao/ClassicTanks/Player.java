package com.JingyuYao.ClassicTanks;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Player extends Tank {

    public Player(Level level, float x, float y, TankType type, Direction direction) {
        super(level, x, y, type, direction);
        gameObjType = GameObjType.PLAYER;
    }
}
