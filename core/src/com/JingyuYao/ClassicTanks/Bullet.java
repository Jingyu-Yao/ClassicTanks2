package com.JingyuYao.ClassicTanks;

import com.badlogic.gdx.math.Rectangle;

@SuppressWarnings("serial")
public class Bullet extends Rectangle{

    static final float HEIGHT = 8f;
    static final float WIDTH = 4f;

    // Properties
    Direction direction;
    boolean player;
    float velocity = 200f; //20 pixel/sec

    // Constructor
    public Bullet(float xd, float yd, Direction d) {
        super();
        this.x = xd;
        this.y = yd;
        direction = d;
        player = true;
        setProperRecBound();
    }

    public Bullet(float xd, float yd, Direction d, boolean p) {
        super();
        this.x = xd;
        this.y = yd;
        direction = d;
        player = p;
        setProperRecBound();
    }

    public Bullet(float xd, float yd, Direction d, boolean p, float s) {
        super();
        this.x = xd;
        this.y = yd;
        direction = d;
        player = p;
        velocity = s;
        setProperRecBound();
    }

    private void setProperRecBound(){
        switch(direction){
            case DOWN:
                this.width = WIDTH;
                this.height = HEIGHT;
                break;
            case LEFT:
                // Intentional
                this.width = HEIGHT;
                this.height = WIDTH;
                break;
            case RIGHT:
                // Intentional
                this.width = HEIGHT;
                this.height = WIDTH;
                break;
            case UP:
                this.width = WIDTH;
                this.height = HEIGHT;
                break;
        }
    }

    // Getter / Setter
    public void setDirection(Direction d) {
        direction = d;
    }

    public void setVelocity(float s) {
        velocity = s;
    }

    public void setPlayer(boolean b) {
        player = b;
    }

    public Direction getDirection() {
        return direction;
    }

    public float getVelocity() {
        return velocity;
    }

    public boolean isPlayer() {
        return player;
    }

    //Updates the bullets position
    public void update(float t){
        float curMove = t * velocity;
        //change the distance
        switch (direction) {
            case DOWN:
                this.y -= curMove;
                break;
            case LEFT:
                this.x -= curMove;
                break;
            case RIGHT:
                this.x += curMove;
                break;
            case UP:
                this.y += curMove;
                break;
        }
    }
}
