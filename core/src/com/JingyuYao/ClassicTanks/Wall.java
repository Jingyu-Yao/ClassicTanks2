package com.JingyuYao.ClassicTanks;

/**
 * Hp: -1 = outer wall, -2 = water, hp > 0 == normal destructible wall
 */
public class Wall extends GhostObj {

    private final WallType wallType;

    public Wall(Level level, float x, float y, WallType wallType) {
        super(level, null, x, y, Level.TILE_SIZE, Level.TILE_SIZE);
        this.wallType = wallType;
        switch (wallType) {
            case NORMAL:
                setHp(2);
                break;
            case WATER:
                setHp(-1);
                break;
            case CONCRETE:
                setHp(2);
                break;
            case INDESTRUCTIBLE:
                setHp(-1);
                break;
        }
        gameObjType = GameObjType.WALL;
    }

    public WallType getWallType() {
        return wallType;
    }

    public static enum WallType {
        NORMAL,
        WATER,
        CONCRETE,
        INDESTRUCTIBLE
    }
}
