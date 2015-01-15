package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Rectangle;

// -1 = outer wall, -2 = water, hp > 0 == normal destrutible wall
public class Wall extends Rectangle{

    int hp;

    public Wall(float x, float y, int h){
        super(x,y,GameScreen.TILE_SIZE,GameScreen.TILE_SIZE);
        hp = h;
    }

    public void damage(){
        if(hp>0)hp--;
    }
}
