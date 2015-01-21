package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Rectangle;

public class Bullet extends GameObj {

    static final float HEIGHT = 8f;
    static final float WIDTH = 4f;

    // Properties
    private Tank origin;

    /**
     * @param x
     * @param y
     * @param direction
     * @param origin    the source of the bullet
     */
    public Bullet(Level level, float x, float y, Direction direction, Tank origin) {
        this.level = level;
        this.body = new Rectangle(x, y, HEIGHT, WIDTH);
        this.direction = direction;
        this.velocity = 300f;
        this.origin = origin;
        setProperRecBound();
    }

    /**
     * Set the width and height of the bullet based on its direction.
     */
    private void setProperRecBound() {
        switch (direction) {
            case UP:
            case DOWN:
                this.body.width = WIDTH;
                this.body.height = HEIGHT;
                break;
            case LEFT:
            case RIGHT:
                // Fall through
                // Intentional
                this.body.width = HEIGHT;
                this.body.height = WIDTH;
                break;
        }
    }

    // Getter / Setter
    public Tank getOrigin() {
        return origin;
    }

    /**
     * Updates the bullet's location. Also handles collision
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        GameObj result = collideAll(0.0f, 0.0f);

        if (result == null) {
            return;
        } else {
            // If both objects are bullets, they cancel each other out
            if (result instanceof Bullet) {
                ((Bullet) result).removeSelf();
                removeSelf();
            }

            // If bullet is fired by the player
            if (this.getOrigin() instanceof Player) {
                // and it hits a wall
                if (result instanceof Wall) {
                    if (result.getHp() != -2) {
                        result.damage();
                        removeSelf();
                    }
                } else {
                    result.damage();
                    removeSelf();
                }
            } else {
                // Enemy tank only damages players
                if (result instanceof Player) {
                    result.damage();
                }
                removeSelf();
            }
        }
    }

    /**
     * Remove self from bullets array of level and reduce origin's bullet
     * count by one.
     */
    private void removeSelf() {
        level.bullets.removeValue(this, true);
        origin.numBullets--;
    }
}
