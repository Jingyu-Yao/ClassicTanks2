package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.utils.Array;

@SuppressWarnings("serial")
public class Tank extends GameObj{

    // Constants
    static final float ONE_DISTANCE = 16f; // pixels
    static final float SIZE = 32f;
    static final float HALF_SIZE = SIZE / 2f;

    // Tank properties
    protected static GameScreen g;
    protected TankType type;
    protected boolean player;

    // Variables for movement
    boolean moving;
    float target;
    float distanceLeft = ONE_DISTANCE;

    // Constructors
    public Tank(GameScreen screen,float x, float y, TankType type, Direction direction, boolean isPlayer) {
        super(x, y, SIZE, SIZE, 100f, direction);
        g = screen;
        this.type = type;
        moving = false;
        player = isPlayer;
        if(type == TankType.ARMORED){
            hp = 3;
        }
    }

    // Setters
    public void setType(TankType t) {
        type = t;
    }

    // Getters
    public TankType getType() {
        return type;
    }
    public boolean isPlayer(){
        return player;
    }
    public boolean isMoving() {
        return moving;
    }

    /**
     * Makes a bullet based on the direction of the tank
     */
    public void shoot() {
        Array<Bullet> array;
        if(player){
            array = g.playerBullets;
        }else{
            array = g.enemyBullets;
        }
        switch (direction) {
            case DOWN:
                array.add(new Bullet(body.x+HALF_SIZE-Bullet.WIDTH/2f, body.y-Bullet.HEIGHT, direction, player));
                break;
            case LEFT:
                array.add(new Bullet(body.x-Bullet.HEIGHT, body.y+HALF_SIZE-Bullet.WIDTH+1, direction, player));
                break;
            case RIGHT:
                array.add(new Bullet(body.x+SIZE+Bullet.WIDTH, body.y+HALF_SIZE-Bullet.HEIGHT/2f+1, direction, player));
                break;
            case UP:
                array.add(new Bullet(body.x+HALF_SIZE-Bullet.WIDTH/2f, body.y+SIZE, direction, player));
                break;
            default:
                break;
        }
    }

    /**
     * Tell the update method to start actually updating
     * Also checks for collision with walls
     * @return
     */
    public boolean move() {
        moving = true;
        // set a target value to avoid rounding errors
        switch (direction) {
            case DOWN:
                target = body.y - ONE_DISTANCE;
                moving = !collideWithGameObjects(body.x,target);
                break;
            case LEFT:
                target = body.x - ONE_DISTANCE;
                moving = !collideWithGameObjects(target,body.y);
                break;
            case RIGHT:
                target = body.x + ONE_DISTANCE;
                moving = !collideWithGameObjects(target,body.y);
                break;
            case UP:
                target = body.y + ONE_DISTANCE;
                moving = !collideWithGameObjects(body.x,target);
                break;
        }
        return moving;
    }

    /**
     * Checks collision with walls in the level. Also checks collision with enemy tanks
     * if the player flag is true.
     * @param x
     * @param y
     * @return
     */
    private boolean collideWithGameObjects(float x, float y){
        for(Wall w : g.walls){
            if(w.collidePoint(x+HALF_SIZE, y+HALF_SIZE))
                return true;
        }
        if(player){
            for(Tank t :g.enemies){
                if(t.collidePoint(x+HALF_SIZE, y+HALF_SIZE))
                    return true;
            }
        }
        else{
            return g.player.collidePoint(x+HALF_SIZE, y+HALF_SIZE);
        }
        return false;
    }

    /**
     * Updates the tanks position while preventing rounding error of final position
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        if (moving) {
            float curMove = deltaTime * velocity;
            distanceLeft -= curMove;
            if (distanceLeft < 0) {
                distanceLeft = ONE_DISTANCE;// reset
                moving = false;// finished moving

                // this prevents final coordinate rounding error
                switch (direction) {
                    case DOWN:
                        this.body.y = target;
                        break;
                    case LEFT:
                        this.body.x = target;
                        break;
                    case RIGHT:
                        this.body.x = target;
                        break;
                    case UP:
                        this.body.y = target;
                        break;
                }
                return;
            }
            // change the distance
            switch (direction) {
                case DOWN:
                    body.y -= curMove;
                    break;
                case LEFT:
                    body.x -= curMove;
                    break;
                case RIGHT:
                    body.x += curMove;
                    break;
                case UP:
                    body.y += curMove;
                    break;
            }
        }
    }

    /**
     * TODO: Use subclass instead of enum
     * All type of tanks.
     */
    protected enum TankType {
        NORMAL, BARRAGE, // Fast bullets
        DUAL, // Dual shot
        FAST, // Fast movement
        ARMORED, // Extra health
        GM, // 'God mode'
    }

}
