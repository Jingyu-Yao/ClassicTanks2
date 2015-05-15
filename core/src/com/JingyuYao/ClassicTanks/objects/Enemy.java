package com.JingyuYao.ClassicTanks.objects;

import com.JingyuYao.ClassicTanks.level.Level;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Enemy extends Tank {

    private final static float BIASED_CHANCE = 0.6f;
    private final static long flickerTime = 100000000l; //0.1s
    protected boolean shiny;
    private long lastTint;

    public Enemy(Level level, float x, float y, TankType type) {
        super(level, x, y, type);
        startShooting();
        gameObjType = GameObjType.ENEMY;
        shiny = false;
        lastTint = 0l;
    }

    public void setShiny() {
        shiny = true;
    }

    public boolean isShiny() {
        return shiny;
    }

    /**
     * Updates this unit's position. If it is moving already, keep moving until collision occurs.
     * Else decide a new direction to move towards.
     *
     * @param deltaTime
     */
    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);

        //Only find a new direction when this unit is not moving
        //This unit will stop moving only when it hits a wall
        if (!getMoving()) {
            moveTowards(getBiasedDirection());
            forward();
        }
    }

    /**
     * Draw this object using its sprite in the correct orientation
     *
     * @param batch
     * @param parentAlpha
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (sprite != null) {
            sprite.setX(getX());
            sprite.setY(getY());
            switch (getDirection()) {
                case DOWN:
                    sprite.setRotation(180);
                    break;
                case LEFT:
                    sprite.setRotation(90);
                    break;
                case RIGHT:
                    sprite.setRotation(270);
                    break;
                case UP:
                    sprite.setRotation(0);
                    break;
            }
            if (shiny && TimeUtils.nanoTime() - lastTint > flickerTime) {
                sprite.setColor(Color.YELLOW);
                lastTint = TimeUtils.nanoTime();
            }

            sprite.draw(batch);
            if (shiny) {
                sprite.setColor(Color.WHITE);
            }
        }
    }

    /**
     * Return a new RANDOM direction that is biased towards the base
     *
     * @return a new biased RANDOM direction
     */
    public Direction getBiasedDirection() {
        if (Level.RANDOM.nextFloat() < BIASED_CHANCE) {
            Vector2 basePosition = getLevel().getBasePosition();
            // Half chance to adjust X or Y position
            if (Level.RANDOM.nextBoolean()) {
                if (this.getX() < basePosition.x) {
                    return Direction.RIGHT;
                } else {
                    return Direction.LEFT;
                }
            } else {
                if (this.getY() < basePosition.y) {
                    return Direction.UP;
                } else {
                    return Direction.DOWN;
                }
            }
        } else {
            // Evenly distribute chance if no bias
            switch (Level.RANDOM.nextInt(4)) {
                case 0:
                    return Direction.UP;
                case 1:
                    return Direction.LEFT;
                case 2:
                    return Direction.RIGHT;
                case 3:
                    return Direction.DOWN;
                default:
                    return Direction.UP;
            }
        }
    }
}
