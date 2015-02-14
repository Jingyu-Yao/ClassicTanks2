package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Jingyu_Yao on 2/14/2015.
 * Does nothing and not show itself
 */
public class GhostObj extends GameObj {
    public GhostObj(Level level, Sprite sprite, float x, float y, float width, float height) {
        super(level, sprite, x, y, width, height);
    }

    public GhostObj(Level level, Sprite sprite, float x, float y, float width, float height, float velocity, Direction direction) {
        super(level, sprite, x, y, width, height, velocity, direction);
    }

    @Override
    public void act(float deltaTime){
        //does nothing
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        //does nothing
    }
}
