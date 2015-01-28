package com.JingyuYao.ClassicTanks;

/**
 * Hp: -1 = outer wall, -2 = water, hp > 0 == normal destructible wall
 */
public class Wall extends GameObj {

    private WallType type;

    public Wall(Level level, float x, float y, WallType type) {
        super(level, x, y, GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
        setType(type);
    }

    public void setType(WallType type){
        this.type = type;
        switch(type){
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
    }

    public WallType getType(){
        return type;
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

    public enum WallType{
        NORMAL,
        WATER,
        CONCRETE,
        INDESTRUCTIBLE
    }
}
