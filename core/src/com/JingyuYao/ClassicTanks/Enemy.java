package com.JingyuYao.ClassicTanks;

import java.util.Random;

/**
 * Created by Jingyu_Yao on 1/16/2015.
 */
public class Enemy extends Tank {
    Random random = new Random();

    public Enemy(GameScreen gameScreen, float x, float y, TankType type, Direction direction) {
        super(gameScreen, x, y, type, direction);
    }

    @Override
    public void damage(){
        super.damage();
        if(hp == 0){
            gameScreen.enemies.removeValue(this, true);
        }
    }

    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        if(moving == true || move()) return;

        int i;
        Direction d;
        i = random.nextInt(4);
        //System.out.println("moveenemy " + i);
        switch(i){
            case 0:
                d=Direction.UP;
                break;
            case 1:
                d=Direction.LEFT;
                break;
            case 2:
                d=Direction.RIGHT;
                break;
            case 3:
                d=Direction.DOWN;
                break;
            default:
                d=Direction.UP;
                break;
        }
        direction = d;
        move();
    }
}
