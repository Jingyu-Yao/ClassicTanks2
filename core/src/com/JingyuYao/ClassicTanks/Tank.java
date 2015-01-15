package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("serial")
public class Tank extends Rectangle{

    // Constants
    static final float ONE_DISTANCE = 16f; // pixels
    static final float SIZE = 32f;
    static final float HALF_SIZE = SIZE / 2f;

    // Tank properties
    static GameScreen g;
    TankType type;
    Direction direction;
    float velocity = 100f; // 10 pixel/sec
    boolean player;
    int hp; //In the case of player, hp = life

    // Variables for movement
    boolean moving;
    float target;
    float distanceLeft = ONE_DISTANCE;

    // Constructors
    public Tank(GameScreen screen) {
        g = screen;
        x = 0;
        y = 0;
        this.height = this.width = SIZE;
        type = TankType.NORMAL;
        direction = Direction.UP;
        moving = false;
        player=false;
        hp = 1;
    }

    public Tank(GameScreen screen,float xd, float yd, TankType t, Direction d, boolean is) {
        g = screen;
        this.x = xd;
        this.y = yd;
        this.height = this.width = SIZE;
        type = t;
        direction = d;
        moving = false;
        player = is;
        if(t == TankType.ARMORED)hp = 3;
        else hp = 1;
    }

    // Setters
    public void setXY(float x, float y) {
        this.x  = x;
        this.y = y;
    }

    public void setType(TankType t) {
        type = t;
        if(t == TankType.ARMORED)hp = 3;
        else hp = 1;
    }

    public void setDirection(Direction d) {
        direction = d;
    }

    public void setVelocity(float s) {
        velocity = s;
    }

    // Getters
    public TankType getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public float getVelocity() {
        return velocity;
    }

    public boolean isPlayer(){
        return player;
    }

    // Functions
    // make bullet, takes into consideration the direction of tank
    public void shoot() {
        Array<Bullet> array;
        if(player){
            array = g.playerBullets;
        }else{
            array = g.enemyBullets;
        }
        switch (direction) {
            case DOWN:
                array.add(new Bullet(x+HALF_SIZE-Bullet.WIDTH/2f, y-Bullet.HEIGHT, direction, player));
                break;
            case LEFT:
                array.add(new Bullet(x-Bullet.HEIGHT, y+HALF_SIZE-Bullet.WIDTH+1, direction, player));
                break;
            case RIGHT:
                array.add(new Bullet(x+SIZE+Bullet.WIDTH, y+HALF_SIZE-Bullet.HEIGHT/2f+1, direction, player));
                break;
            case UP:
                array.add(new Bullet(x+HALF_SIZE-Bullet.WIDTH/2f, y+SIZE, direction, player));
                break;
            default:
                break;
        }
    }

    // Tell the update method to start actually updating
    // Also checks for collision with walls
    public void move() {
        moving = true;
        // set a target value to avoid rounding errors
        switch (direction) {
            case DOWN:
                target = y - ONE_DISTANCE;
                checkWall(x,target);
                break;
            case LEFT:
                target = x - ONE_DISTANCE;
                checkWall(target,y);
                break;
            case RIGHT:
                target = x + ONE_DISTANCE;
                checkWall(target,y);
                break;
            case UP:
                target = y + ONE_DISTANCE;
                checkWall(x,target);
                break;
        }
    }

    private void checkWall(float a, float b){
        for(Wall w : g.walls){
            if(w.contains(a+SIZE/2, b+SIZE/2))
                moving = false;
        }
        if(player){
            for(Tank t :g.enemies){
                if(t.contains(a+SIZE/2, b+SIZE/2))
                    moving = false;
            }
        }
    }

    // Updates the tanks position
    public void update(float t) {
        if (moving) {
            float curMove = t * velocity;
            distanceLeft -= curMove;
            if (distanceLeft < 0) {
                curMove += distanceLeft;// travel w/e is distance left
                distanceLeft = ONE_DISTANCE;// reset
                moving = false;// finished moving

                // this prevents final coordinate rounding error
                switch (direction) {
                    case DOWN:
                        this.y = target;
                        break;
                    case LEFT:
                        this.x = target;
                        break;
                    case RIGHT:
                        this.x = target;
                        break;
                    case UP:
                        this.y = target;
                        break;
                }
                return;
            }
            // change the distance
            switch (direction) {
                case DOWN:
                    y -= curMove;
                    break;
                case LEFT:
                    x -= curMove;
                    break;
                case RIGHT:
                    x += curMove;
                    break;
                case UP:
                    y += curMove;
                    break;
            }
        }
    }

    public void damage(){
        hp--;
    }
    // Allowed Types
    protected enum TankType {
        NORMAL, BARRAGE, // Fast bullets
        DUAL, // Dual shot
        FAST, // Fast movement
        ARMORED, // Extra health
        GM, // 'God mode'
    }

}
