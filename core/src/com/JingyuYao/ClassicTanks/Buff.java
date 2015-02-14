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
    public Buff(Level level, float x, float y) {
        super(level, null, x, y, Level.TILE_SIZE, Level.TILE_SIZE);
        gameObjType = GameObjType.BUFF;
        switch (Level.RANDOM.nextInt(BuffType.values().length)){
            case 0:
                this.buffType = BuffType.STAR;
                break;
            case 1:
                this.buffType = BuffType.FREEZE;
                break;
            case 2:
                this.buffType = BuffType.BOOM;
                break;
            case 3:
                this.buffType = BuffType.ARMOR_UP;
                break;
            case 4:
                this.buffType = BuffType.LIFE;
                break;
            default:
                this.buffType = BuffType.STAR;
                break;
        }
        sprite = getLevel().buffSprites.get(buffType);
    }

    public BuffType getBuffType(){
        return buffType;
    }

    public enum BuffType{
        STAR,
        FREEZE,
        BOOM,
        ARMOR_UP,
        LIFE
    }
}
