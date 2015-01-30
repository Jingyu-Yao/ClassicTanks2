package com.JingyuYao.ClassicTanks;

/**
 * Created by Jingyu_Yao on 1/23/2015.
 */
public class Base extends GameObj {

    public Base(Level level, float x, float y, float width, float height) {
        super(level, x, y, width, height);
        gameObjType = GameObjType.BASE;
    }

}
