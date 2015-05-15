package com.JingyuYao.ClassicTanks.objects;

import com.JingyuYao.ClassicTanks.level.Level;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Jingyu_Yao on 2/13/2015.
 */
public class Buff extends GameObj {
    private static final long BUFF_DURATION = 10l * 1000000000l; //Sec

    private final BuffType buffType;
    private final float expiration;

    /**
     * @param level
     * @param x     world coordinates
     * @param y     world coordinates
     */
    public Buff(Level level, float x, float y) {
        super(level, null, x, y, Level.TILE_SIZE, Level.TILE_SIZE);
        gameObjType = GameObjType.BUFF;
        switch (Level.RANDOM.nextInt(BuffType.values().length)) {
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
        sprite = chooseSprite();
        expiration = TimeUtils.nanoTime() + BUFF_DURATION;
    }

    private Sprite chooseSprite() {
        TextureAtlas atlas = getLevel().getTextureAtlas();
        switch (buffType) {
            case STAR:
                return new Sprite(atlas.findRegion("STAR"));
            case FREEZE:
                return new Sprite(atlas.findRegion("FREEZE"));
            case BOOM:
                return new Sprite(atlas.findRegion("BOOM"));
            case LIFE:
                return new Sprite(atlas.findRegion("LIFE"));
        }
        return null;
    }

    public BuffType getBuffType() {
        return buffType;
    }

    @Override
    public void act(float delta) {
        if (TimeUtils.nanoTime() > expiration) {
            getLevel().removeObject(this);
        }
    }

    public enum BuffType {
        STAR,
        FREEZE,
        BOOM,
        LIFE
    }
}
