package com.JingyuYao.ClassicTanks;

/**
 * Hp: -1 = outer wall, -2 = water, hp > 0 == normal destructible wall
 */
public class Wall extends GameObj{
    public Wall(float x, float y, int hp){
        super(x,y,GameScreen.TILE_SIZE,GameScreen.TILE_SIZE);
        this.hp = hp;
    }
}
