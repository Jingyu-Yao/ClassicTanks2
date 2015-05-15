package com.JingyuYao.ClassicTanks.objects;

import com.JingyuYao.ClassicTanks.level.Level;

/**
 * Created by Jingyu_Yao on 1/23/2015.
 */
public class Base extends GhostObj {

    public Base(Level level, float x, float y, float width, float height) {
        super(level, null, x, y, width, height);
        gameObjType = GameObjType.BASE;
    }
}
