package com.JingyuYao.ClassicTanks;

/**
 * Hp: -1 = outer wall, -2 = water, hp > 0 == normal destructible wall
 */
public class Wall extends GameObj {
    public Wall(Level level, float x, float y, int hp) {
        super(level, x, y, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
        setHp(hp);
    }

    @Override
    public void damage() {
        if (getHp() > 0) {
            setHp(getHp() - 1);
        }
        if (getHp() == 0) {
            // Hp = 0 = wall disappears
            getLevel().removeObject(this);
        }
    }
}
