package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Jingyu_Yao on 2/2/2015.
 */
public class StaticObj extends GameObj {

    public StaticObj(Level level, Sprite sprite, float x, float y, float width, float height) {
        super(level, sprite, x, y, width, height);
    }

    @Override
    public void act(float deltaTime){
        //do nothing
    }
}
