package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Rectangle;

/**
 * Base class for all objects in the game.
 * Created by Jingyu_Yao on 1/15/2015.
 */
public class GameObj {
    protected Rectangle body;
    protected Direction direction;
    protected float velocity;
    protected int hp;

    public GameObj(float x, float y, float width, float height){
        body = new Rectangle(x, y, width, height);
        direction = Direction.NONE;
        velocity = 0.0f;
        hp = 1;
    }

    public GameObj(float x, float y, float width, float height, float velocity){
        body = new Rectangle(x, y, width, height);
        direction = Direction.NONE;
        this.velocity = velocity;
        hp = 1;
    }

    public GameObj(float x, float y, float width, float height, float velocity, Direction direction){
        body = new Rectangle(x, y, width, height);
        this.direction = direction;
        this.velocity = velocity;
        hp = 1;
    }

    //Setters
    public void setDirection(Direction direction) {this.direction = direction;}
    public void setVelocity(float velocity) {this.velocity = velocity;}
    public void setX(float x) { body.x = x;}
    public void setY(float y) { body.y = y;}
    public void setWidth(float width) { body.width = width; }
    public void setHeight(float height) { body.height = height; }
    public void setHp(int hp) { this.hp = hp; }

    //Getters
    public Direction getDirection() { return direction; }
    public float getVelocity() { return velocity; }
    public float getX() { return body.x; }
    public float getY() { return body.y; }
    public float getWidth() { return body.width; }
    public float getHeight() { return body.height; }
    public int getHp() { return hp; }

    /**
     * Check collision with a point
     * @param x
     * @param y
     * @return
     */
    public boolean collidePoint(float x, float y){
        return body.contains(x, y);
    }

    /**
     * Check collision with another GameObj
     * @param obj
     * @return
     */
    public boolean collideRect(GameObj obj){
        return body.overlaps(obj.body);
    }

    /**
     * Updates the object's position based on deltaTime.
     * @param deltaTime
     */
    public void update(float deltaTime){
        float curMove = deltaTime * velocity;
        //change the distance
        switch (direction) {
            case DOWN:
                this.body.y -= curMove;
                break;
            case LEFT:
                this.body.x -= curMove;
                break;
            case RIGHT:
                this.body.x += curMove;
                break;
            case UP:
                this.body.y += curMove;
                break;
        }
    }

    /**
     * Reduce object's hp by one
     */
    public void damage(){
        if(hp>0)hp--;
    }
}
