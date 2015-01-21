package com.JingyuYao.ClassicTanks;

/**
 * Hp: -1 = outer wall, -2 = water, hp > 0 == normal destructible wall
 */
public class Wall extends GameObj {
    public Wall(Level level, float x, float y, int hp) {
        super(level, x, y, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
        this.hp = hp;
    }

    @Override
    public void damage() {
        if (hp > 0) {
            hp--;
        }
        if (hp == 0) {
            // Hp = 0 = wall disappears
            level.killCell(body.x, body.y);
            level.walls.removeValue(this, true);
        }
    }
}
