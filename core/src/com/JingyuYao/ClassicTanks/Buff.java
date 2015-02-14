package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Jingyu_Yao on 2/13/2015.
 */
public class Buff extends StaticObj {
    private final BuffType buffType;

    /**
     *
     * @param level
     * @param x world coordinates
     * @param y world coordinates
     * @param type
     */
    public Buff(Level level, float x, float y, BuffType type) {
        super(level, null, x, y, Level.TILE_SIZE, Level.TILE_SIZE);
        gameObjType = GameObjType.BUFF;
        this.buffType = type;
        switch(type){
            case STAR:
                sprite = getLevel().buffSprites.get(type);
                break;
            case FREEZE:
                break;
            case BOOM:
                break;
            case ARMOR_UP:
                break;
        }
    }

    public BuffType getBuffType(){
        return buffType;
    }

    public enum BuffType{
        STAR,
        FREEZE,
        BOOM,
        ARMOR_UP,
    }
}
