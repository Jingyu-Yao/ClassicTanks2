package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Enemy extends Tank {

    private final static float BIASED_CHANCE = 0.6f;
    protected boolean shiny;

    public Enemy(Level level, float x, float y, TankType type) {
        super(level, x, y, type);
        startShooting();
        gameObjType = GameObjType.ENEMY;
        shiny = false;
    }

    public void setShiny(){
        shiny = true;
    }

    public boolean isShiny(){
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
