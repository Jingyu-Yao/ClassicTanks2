package com.JingyuYao.ClassicTanks;

@SuppressWarnings("serial")
public class Bullet extends GameObj{

    static final float HEIGHT = 8f;
    static final float WIDTH = 4f;

    // Properties
    private boolean player;

    // Constructor
    public Bullet(float x, float y, Direction d, boolean p) {
        super(x,y,HEIGHT,WIDTH,200f);
        direction = d;
        player = p;
        setProperRecBound();
    }

    /**
     * Set the width and height of the bullet based on its direction.
     */
    private void setProperRecBound(){
        switch(direction){
            case DOWN:
                this.body.width = WIDTH;
                this.body.height = HEIGHT;
                break;
            case LEFT:
                // Intentional
                this.body.width = HEIGHT;
                this.body.height = WIDTH;
                break;
            case RIGHT:
                // Intentional
                this.body.width = HEIGHT;
                this.body.height = WIDTH;
                break;
            case UP:
                this.body.width = WIDTH;
                this.body.height = HEIGHT;
                break;
        }
    }

    // Getter / Setter
    public void setPlayer(boolean b) {
        player = b;
    }
    public boolean isPlayer() {
        return player;
    }

}
