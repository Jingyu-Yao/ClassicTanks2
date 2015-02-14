package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Jingyu_Yao on 2/13/2015.
 */
public class Buff extends GameObj {
    private static final long BUFF_DURATION = 10l * 1000000000l; //Sec

    private final BuffType buffType;
    private final float expiration;

    /**
     *
     * @param level
     * @param x world coordinates
     * @param y world coordinates
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
                this.buffType = BuffType.LIFE;
                break;
            default:
                this.buffType = BuffType.STAR;
                break;
        }
        sprite = getLevel().buffSprites.get(buffType);
        expiration = TimeUtils.nanoTime() + BUFF_DURATION;
    }

    public BuffType getBuffType(){
        return buffType;
    }

    @Override
    public void act(float delta){
        if(TimeUtils.nanoTime() > expiration){
            getLevel().removeObject(this);
        }
    }

    public enum BuffType{
        STAR,
        FREEZE,
        BOOM,
        LIFE
    }
}
